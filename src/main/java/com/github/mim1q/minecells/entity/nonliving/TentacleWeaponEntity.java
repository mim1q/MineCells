package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class TentacleWeaponEntity extends Entity {
  private float serverYaw;
  private float serverPitch;
  private int interpolationSteps;
  private Vec3d targetPos;
  private PlayerEntity owner;
  private Vec3d ownerVelocity = null;

  private static final TrackedData<Boolean> RETRACTING = DataTracker.registerData(TentacleWeaponEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private final AnimationProperty length = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public TentacleWeaponEntity(EntityType<TentacleWeaponEntity> type, World world) {
    super(type, world);
    this.ignoreCameraFrustum = true;
  }

  public static TentacleWeaponEntity create(World world, PlayerEntity owner) {
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
    entity.targetPos = entity.getPos().add(entity.getRotationVector().multiply(16.0D));
    return entity;
  }

  @Override
  public void tick() {
    super.tick();
    if (this.isRetracting()) {
      this.length.setupTransitionTo(0.0F, 10.0F);
    } else {
      this.length.setupTransitionTo(1.0F, 20.0F);
    }

    if (world.isClient) {
      this.tickClient();
    } else {
      this.tickServer();
    }
  }

  public void tickClient() {
    if (this.interpolationSteps > 0) {
      float g = MathHelper.wrapDegrees(this.serverYaw - this.getYaw());
      this.setYaw(this.getYaw() + g / this.interpolationSteps);
      this.setPitch(this.getPitch() + (this.serverPitch - this.getPitch()) / this.interpolationSteps);
      --this.interpolationSteps;
    }
  }

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
      this.setRetracting(true);
    }

    if (this.targetPos != null) {
      this.rotateTowards(this.targetPos);
      Vec3d pos = this.getEndPos(this.getLength(0.0F));
      HitResult collision = this.getCollision();
      if (collision.getType() != HitResult.Type.MISS) {
        this.playSound(MineCellsSounds.TENTACLE_RELEASE, 0.5F, 1.0F);
        this.setRetracting(true);
        this.ownerVelocity = pos.subtract(this.owner.getPos()).multiply(0.15D).add(0.0D, 0.075D, 0.0D);
        if (collision.getType() == HitResult.Type.ENTITY) {
          Entity entity = ((EntityHitResult) collision).getEntity();
          entity.damage(DamageSource.player(this.owner), 1.0F);
        }
      }
    }
  }

  public HitResult getCollision() {
    Vec3d pos = this.getEndPos(this.getLength(0.0F));
    var entity = this.world.getOtherEntities(
      this,
      Box.of(pos, 1.0D, 1.0D, 1.0D),
      e -> e != this.owner && e != this
    ).stream().findFirst();
    if (entity.isPresent()) {
      return new EntityHitResult(entity.get());
    }
    return this.world.raycast(new RaycastContext(pos, pos.add(this.getRotationVector().multiply(0.5D)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
  }

  private void rotateTowards(Vec3d target) {
    Vec3d pos = this.getPos();
    double d = target.x - pos.x;
    double e = target.y - pos.y;
    double f = target.z - pos.z;
    double g = Math.sqrt(d * d + f * f);
    this.setPitch(MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * MathHelper.DEGREES_PER_RADIAN))));
    this.setYaw(MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * MathHelper.DEGREES_PER_RADIAN) - 90.0F));
    this.prevPitch = this.getPitch();
    this.prevYaw = this.getYaw();
  }

  private void pullOwner() {
    this.owner.setVelocity(this.ownerVelocity);
    this.owner.velocityModified = true;
    this.owner.fallDistance = 0.0F;
  }

  @Override
  public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
    this.serverYaw = yaw;
    this.serverPitch = pitch;
    this.interpolationSteps = interpolationSteps;
  }

  public float getLength(float tickDelta) {
    if (this.targetPos == null) {
      return 0.0F;
    }
    this.length.update(this.age + tickDelta);
    float progress = this.length.getValue();
    float length = MathHelper.clamp(progress, 0.0F, 1.0F);
    return (float) (this.targetPos.subtract(this.getPos()).length() * length);
  }

  public Vec3d getEndPos(float length) {
    return this.getPos().add(this.getRotationVector().multiply(length));
  }

  @Override
  protected void initDataTracker() {
    RETRACTING.getId();
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
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }

  @Override
  public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);
    Vec3d spawnPos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
    this.targetPos = spawnPos.add(this.getRotationVector().multiply(16.0D));
  }
}
