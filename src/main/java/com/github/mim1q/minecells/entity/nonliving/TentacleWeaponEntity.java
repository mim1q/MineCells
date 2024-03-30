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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class TentacleWeaponEntity extends Entity {
  public static final double BASE_LENGTH = 10.0F;

  private Vec3d startingPos;
  private Vec3d targetPos;
  private PlayerEntity owner;
  private Vec3d ownerVelocity = null;

  private static final TrackedData<Boolean> RETRACTING = DataTracker.registerData(TentacleWeaponEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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
    entity.setVelocity(Vec3d.ZERO);
    entity.setPitch(owner.getPitch());
    entity.prevPitch = owner.getPitch();
    entity.setYaw(owner.getYaw());
    entity.prevYaw = owner.getYaw();
    entity.startRiding(owner, true);
    entity.targetPos = targetPos;
    entity.startingPos = entity.getPos();
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
      if (this.ownerVelocity != null) {
        this.pullOwner();
      }
      if (this.getLength(0.0f) <= 0.01F) {
        this.discard();
      }
      return;
    }
    if (this.age > 10) {
      this.owner.getItemCooldownManager().set(MineCellsItems.TENTACLE, 10);
      this.setRetracting(true);
    }

    if (this.targetPos != null && this.startingPos != null) {
      HitResult collision = this.getCollision();
      if (collision.getType() != HitResult.Type.MISS) {
        Vec3d pos = this.getEndPos(this.getLength(0.0F));
        this.playSound(MineCellsSounds.TENTACLE_RELEASE, 0.5F, 1.0F);
        this.setRetracting(true);
        this.ownerVelocity = pos.subtract(this.owner.getPos()).multiply(0.15D);
        if (owner.getY() < targetPos.getY()) {
          this.ownerVelocity = this.ownerVelocity.add(0.0D, 0.05D, 0.0D);
        }
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
    if (getWorld().getBlockState(blockPos).isSolidBlock(getWorld(), blockPos)) {
      return new BlockHitResult(pos, null, blockPos, false);
    }
    Vec3d minPos = this.getEndPos(this.getLength(0.0F));
    Vec3d maxPos = this.getEndPos(this.getLength(1.0F));
    return getWorld().raycast(new RaycastContext(minPos, maxPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
  }

  private void pullOwner() {
    this.owner.setVelocity(this.ownerVelocity);
    this.owner.velocityModified = true;
    this.owner.fallDistance = 0.0F;
  }

  public float getLength(float tickDelta) {
    if (this.targetPos == null) {
      return 0.0F;
    }
    this.length.update(this.age + tickDelta);
    float progress = this.length.getValue();
    return MathHelper.clamp(progress, 0.0F, 1.0F);
  }

  public Vec3d getEndPos(float length) {
    return this.targetPos.subtract(this.startingPos).multiply(length).add(this.startingPos);
  }

  public Vec3d getStartingPos() {
    return this.startingPos;
  }

  @Override
  protected void initDataTracker() {
    this.dataTracker.startTracking(RETRACTING, false);
  }

  public boolean isRetracting() {
    return this.dataTracker.get(RETRACTING);
  }

  public void setRetracting(boolean retracting) {
    this.dataTracker.set(RETRACTING, retracting);
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    this.targetPos = new Vec3d(nbt.getDouble("TargetX"), nbt.getDouble("TargetY"), nbt.getDouble("TargetZ"));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putDouble("TargetX", this.targetPos.x);
    nbt.putDouble("TargetY", this.targetPos.y);
    nbt.putDouble("TargetZ", this.targetPos.z);
  }

  @Override
  public Packet<ClientPlayPacketListener> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }

  @Override
  public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);
    Vec3d spawnPos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
    this.targetPos = spawnPos.add(this.getRotationVector().multiply(BASE_LENGTH));
    this.startingPos = spawnPos;
  }
}
