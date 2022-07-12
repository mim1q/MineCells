package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShootGoal;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.MagicOrbEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class InquisitorEntity extends MineCellsEntity implements IShootEntity {

  // Animation data
  @Environment(EnvType.CLIENT)
  public float offset = 0.0F;
  @Environment(EnvType.CLIENT)
  public float targetOffset = 0.0F;

  private static final TrackedData<Integer> SHOOT_COOLDOWN = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.INTEGER);
  private static final TrackedData<Boolean> SHOOT_CHARGING = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> SHOOT_RELEASING = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  public InquisitorEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.ignoreCameraFrustum = true;
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(SHOOT_COOLDOWN, 0);
    this.dataTracker.startTracking(SHOOT_CHARGING, false);
    this.dataTracker.startTracking(SHOOT_RELEASING, false);
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    this.goalSelector.add(0, new InquisitorShootGoal(this, 10, 20));
  }

  @Override
  public void tick() {
    super.tick();
    this.decrementCooldown(SHOOT_COOLDOWN);
  }

  @Override
  public boolean isShootReleasing() {
    return this.dataTracker.get(SHOOT_RELEASING);
  }

  @Override
  public void setShootReleasing(boolean releasing) {
    this.dataTracker.set(SHOOT_RELEASING, releasing);
  }

  @Override
  public boolean isShootCharging() {
    return this.dataTracker.get(SHOOT_CHARGING);
  }

  @Override
  public void setShootCharging(boolean charging) {
    this.dataTracker.set(SHOOT_CHARGING, charging);
  }

  @Override
  public int getShootCooldown() {
    return this.dataTracker.get(SHOOT_COOLDOWN);
  }

  @Override
  public void setShootCooldown(int ticks) {
    this.dataTracker.set(SHOOT_COOLDOWN, ticks);
  }

  @Override
  public int getShootMaxCooldown() {
    return 20 + this.random.nextInt(20);
  }

  @Override
  public SoundEvent getShootChargeSoundEvent() {
    return SoundRegistry.INQUISITOR_CHARGE;
  }

  @Override
  public SoundEvent getShootReleaseSoundEvent() {
    return SoundRegistry.INQUISITOR_RELEASE;
  }


  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("shootCooldown", this.getShootCooldown());
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.setShootCooldown(nbt.getInt("shootCooldown"));
  }

  public static DefaultAttributeContainer.Builder createInquisitorAttributes() {
    return createLivingAttributes()
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 2.0D);
  }

  public static class InquisitorShootGoal extends ShootGoal<InquisitorEntity> {

    public InquisitorShootGoal(InquisitorEntity entity, int actionTick, int lengthTicks) {
      super(entity, actionTick, lengthTicks, 0.3F);
      this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public void start() {
      super.start();
      if (this.target != null) {
        this.entity.getLookControl().lookAt(this.target);
      }
    }

    @Override
    public void tick() {
      super.tick();
      this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.0d);
    }

    @Override
    public void shoot(LivingEntity target) {
      Vec3d targetPos = target.getPos().add(0.0D, 1.3D, 0.0D);
      Vec3d entityPos = this.entity.getPos().add(0.0D, 2.25D, 0.0D);

      Vec3d vel = targetPos.subtract(entityPos).normalize().multiply(1.25D);

      MagicOrbEntity orb = new MagicOrbEntity(EntityRegistry.MAGIC_ORB, this.entity.world);
      orb.setPosition(entityPos);
      orb.setVelocity(vel);
      orb.setOwner(this.entity);

      this.entity.world.spawnEntity(orb);
    }
  }
}
