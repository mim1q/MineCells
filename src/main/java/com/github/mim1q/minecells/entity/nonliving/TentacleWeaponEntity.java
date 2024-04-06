package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class TentacleWeaponEntity extends Entity {
  private Vec3d startingPos;
  private PlayerEntity owner;

  private static final TrackedData<Boolean> RETRACTING = DataTracker.registerData(TentacleWeaponEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Vector3f> TARGET_POS = DataTracker.registerData(TentacleWeaponEntity.class, TrackedDataHandlerRegistry.VECTOR3F);

  private final AnimationProperty length = new AnimationProperty(0.0F, MathUtils::easeOutQuad);

  public TentacleWeaponEntity(EntityType<TentacleWeaponEntity> type, World world) {
    super(type, world);
    this.ignoreCameraFrustum = true;
  }

  public static TentacleWeaponEntity create(World world, PlayerEntity owner, Vec3d targetPos) {
    TentacleWeaponEntity entity = MineCellsEntities.TENTACLE_WEAPON.create(world);
    if (entity == null) {
      return null;
    }
    entity.owner = owner;
    entity.setPos(owner.getX(), owner.getY() + 1.5D, owner.getZ());
    entity.setTargetPos(targetPos);
    entity.startingPos = entity.getPos();

    entity.startRiding(owner, true);

    return entity;
  }

  @Override
  public void tick() {
    super.tick();
    if (this.isRetracting()) {
      this.length.setupTransitionTo(0.0F, 10.0F);
    } else {
      this.length.setupTransitionTo(1.0F, 10.0F);
    }

    if (getWorld().isClient) {
      this.tickClient();
    } else {
      this.tickServer();
    }
  }

  public void tickClient() { }

  public void tickServer() {
    if (this.owner == null || !this.hasVehicle()) {
      this.discard();
      return;
    }
    if (this.isRetracting()) {
      var length = this.getLength(0.0F);
      if (length >= 0.01F) {
        this.pullOwner();
      }
      this.owner.fallDistance = 0.0F;
      if (length <= 0.01F && this.age > 30) {
        this.discard();
      }
      return;
    }
    if (this.age > this.getTargetPos().length() / 10) {
      this.owner.getItemCooldownManager().set(MineCellsItems.TENTACLE, 10);
      this.setRetracting(true);
    }

    if (this.startingPos != null) {
      HitResult collision = this.getCollision();
      if (collision.getType() != HitResult.Type.MISS) {
        Vec3d pos = this.getEndPos(this.getLength(0.0F));
        this.playSound(MineCellsSounds.TENTACLE_RELEASE, 0.5F, 1.0F);
        this.setRetracting(true);
        var yDiff = Math.abs(this.owner.getPos().y - pos.y) * 0.15;
        yDiff = Math.max(yDiff, 0.05);
        if (collision.getType() == HitResult.Type.ENTITY) {
          Entity entity = ((EntityHitResult) collision).getEntity();
          entity.damage(getDamageSources().playerAttack(this.owner), 1.0F);
        }
      }
    }
  }

  public HitResult getCollision() {
    Vec3d pos = this.getEndPos(this.getLength(1.0F));
    if (pos == null) {
      return BlockHitResult.createMissed(Vec3d.ZERO, null, null);
    }
    var entity = getWorld().getOtherEntities(
      this,
      Box.of(pos, 1.0D, 1.0D, 1.0D),
      e -> e != this.owner && e != this
    ).stream().findFirst();
    if (entity.isPresent()) {
      return new EntityHitResult(entity.get());
    }
    var blockPos = BlockPos.ofFloored(pos);

    if (age == 10) {
      blockPos = BlockPos.ofFloored(getTargetPos());
    }

    if (!getWorld().getBlockState(blockPos).getCollisionShape(getWorld(), blockPos).isEmpty()) {
      return new BlockHitResult(pos, null, blockPos, true);
    }
    Vec3d minPos = this.getEndPos(this.getLength(0.0F));
    Vec3d maxPos = this.getEndPos(this.getLength(1.0F));
    return getWorld().raycast(new RaycastContext(minPos, maxPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
  }

  private void pullOwner() {
    var ownerPos = this.owner.getPos();
    var targetPos = this.getTargetPos().add(
      0.0,
      2.5,
      0.0
    );
    var direction = targetPos.subtract(ownerPos).multiply(0.15);
    this.owner.setVelocity(direction);
    this.owner.velocityModified = true;
  }

  public float getLength(float tickDelta) {
    return this.length.update(this.age + tickDelta);
  }

  public Vec3d getEndPos(float length) {
    return this.getTargetPos().subtract(this.startingPos).multiply(length).add(this.startingPos);
  }

  public Vec3d getStartingPos() {
    return this.startingPos;
  }

  @Override
  protected void initDataTracker() {
    this.dataTracker.startTracking(RETRACTING, false);
    this.dataTracker.startTracking(TARGET_POS, new Vector3f((float)this.getX(), (float)this.getY(), (float)this.getZ()));
  }

  private Vec3d getTargetPos() {
    var pos = this.dataTracker.get(TARGET_POS);
    return new Vec3d(pos.x(), pos.y(), pos.z());
  }

  private void setTargetPos(Vec3d pos) {
    this.dataTracker.set(TARGET_POS, new Vector3f((float) pos.x, (float) pos.y, (float) pos.z));
  }

  public boolean isRetracting() {
    return this.dataTracker.get(RETRACTING);
  }

  public void setRetracting(boolean retracting) {
    this.dataTracker.set(RETRACTING, retracting);
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    this.setTargetPos(new Vec3d(nbt.getDouble("TargetX"), nbt.getDouble("TargetY"), nbt.getDouble("TargetZ")));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putDouble("TargetX", this.getTargetPos().x);
    nbt.putDouble("TargetY", this.getTargetPos().y);
    nbt.putDouble("TargetZ", this.getTargetPos().z);
  }


  @Override
  public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);
    this.startingPos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
  }
}
