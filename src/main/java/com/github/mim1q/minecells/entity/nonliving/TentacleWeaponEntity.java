package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TentacleWeaponEntity extends Entity {
  private float serverYaw;
  private float serverPitch;
  private int interpolationSteps;
  private Vec3d targetPos;
  private PlayerEntity owner;

  public TentacleWeaponEntity(EntityType<TentacleWeaponEntity> type, World world) {
    super(type, world);
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
    entity.targetPos = entity.getPos().add(entity.getRotationVector().multiply(10.0D));
    return entity;
  }

  @Override
  public void tick() {
    super.tick();
    if (world.isClient) {
      this.tickClient();
    } else {
      if (this.owner == null || !this.hasVehicle()) {
        this.discard();
        return;
      }
      this.tickServer();
    }

    if (this.age > 200) {
      this.discard();
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
    this.setPosition(owner.getPos().add(0.0D, 1.5D, 0.0D));
    if (this.targetPos != null) {
      this.rotateTowards(this.targetPos);
      Vec3d pos = this.getEndPos(this.getLength(0.0F));
      if (!world.getBlockState(new BlockPos(pos)).getCollisionShape(world, new BlockPos(pos)).isEmpty()) {
        this.owner.teleport(pos.x, pos.y, pos.z);
        this.discard();
      }
    }
  }

  private void rotateTowards(Vec3d target) {
    Vec3d pos = this.getPos();
    double d = target.x - pos.x;
    double e = target.y - pos.y;
    double f = target.z - pos.z;
    double g = Math.sqrt(d * d + f * f);
    this.setPitch(MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * MathHelper.DEGREES_PER_RADIAN))));
    this.setYaw(MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * MathHelper.DEGREES_PER_RADIAN) - 90.0F));
    this.setHeadYaw(this.getYaw());
    this.prevPitch = this.getPitch();
    this.prevYaw = this.getYaw();
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
    float progress = MathHelper.clamp((this.age + tickDelta) / 10.0F, 0.0F, 1.0F);
    float lengthPercent = MathUtils.easeInOutQuad(0.0F, 1.0F, MathHelper.clamp(progress, 0.0F, 1.0F));
    return (float) (this.targetPos.subtract(this.getPos()).length() * lengthPercent);
  }

  public Vec3d getEndPos(float length) {
    return this.getPos().add(this.getRotationVector().multiply(length));
  }

  @Override
  protected void initDataTracker() {

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
    this.targetPos = spawnPos.add(this.getRotationVector().multiply(10.0D));
  }
}
