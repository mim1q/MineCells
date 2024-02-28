package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.JumpBackGoal;
import com.github.mim1q.minecells.entity.ai.goal.ShootGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GrenadierEntity extends MineCellsEntity implements IShootEntity {

  public final AnimationProperty additionalRotation = new AnimationProperty(0.0F);

  private static final TrackedData<Integer> SHOOT_COOLDOWN = DataTracker.registerData(GrenadierEntity.class, TrackedDataHandlerRegistry.INTEGER);
  private static final TrackedData<Boolean> SHOOT_CHARGING = DataTracker.registerData(GrenadierEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> SHOOT_RELEASING = DataTracker.registerData(GrenadierEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  private int jumpbackCooldown = 0;

  public GrenadierEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();

    this.dataTracker.startTracking(SHOOT_COOLDOWN, 50);
    this.dataTracker.startTracking(SHOOT_CHARGING, false);
    this.dataTracker.startTracking(SHOOT_RELEASING, false);
  }

  @Override
  public void initGoals() {
    this.goalSelector.add(4, new LookAroundGoal(this));
    this.goalSelector.add(3, new WanderAroundGoal(this, 1.0D));
    this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D));

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

    this.goalSelector.add(1, new GrenadierShootGoal(this, 10, 20));
    this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 16.0F));
    this.goalSelector.add(2, new WalkTowardsTargetGoal(this, 1.0D, true, 6.0D));

    this.goalSelector.add(1, new JumpBackGoal(this, s -> {
      s.defaultCooldown = 20;
      s.actionTick = 10;
      s.length = 20;
      s.chance = 0.3F;
      s.cooldownGetter = () -> jumpbackCooldown;
      s.cooldownSetter = ticks -> jumpbackCooldown = ticks;
    }, it -> it.getTarget() != null && it.squaredDistanceTo(it.getTarget()) <= 25.0D));
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient()) {
      clientTick();
    } else {
      serverTick();
    }
  }

  protected void clientTick() {
    if (isShootCharging()) {
      additionalRotation.setupTransitionTo(240.0F, 15);
    } else if (isShootReleasing()) {
      additionalRotation.setupTransitionTo(-30.0F, 10);
    } else {
      additionalRotation.setupTransitionTo(0.0F, 20);
    }
  }

  protected void serverTick() {
    this.decrementCooldown(SHOOT_COOLDOWN);
    --jumpbackCooldown;
  }

  @Override
  public int getSafeFallDistance() {
    return 3;
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
    return 20 + this.random.nextInt(40);
  }

  @Override
  public SoundEvent getShootChargeSoundEvent() {
    return MineCellsSounds.GRENADIER_CHARGE;
  }

  @Override
  public SoundEvent getShootReleaseSoundEvent() {
    return null;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return MineCellsSounds.LEAPING_ZOMBIE_DEATH;
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

  public static DefaultAttributeContainer.Builder createGrenadierAttributes() {
    return createLivingAttributes()
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D)
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 4.0D);
  }

  public static class GrenadierShootGoal extends ShootGoal<GrenadierEntity> {

    public GrenadierShootGoal(GrenadierEntity entity, int actionTick, int lengthTicks) {
      super(entity, actionTick, lengthTicks, 0.3F);
    }

    @Override
    public void shoot(LivingEntity target) {
      super.shoot(target);

      Vec3d targetPos = target.getPos().add(this.entity.random.nextDouble() * 2.0D - 1.0D, 0.0D, this.entity.random.nextDouble() * 2.0D - 1.0D);
      Vec3d entityPos = this.entity.getPos();

      Vec3d delta = targetPos.subtract(entityPos).multiply(0.035D).add(0.0D, 0.5D, 0.0D);

      GrenadeEntity grenade = new GrenadeEntity(MineCellsEntities.GRENADE, this.entity.getWorld());
      grenade.setPosition(entityPos.add(0.0D, 1.5D, 0.0D));
      grenade.shoot(delta);

      this.entity.getWorld().spawnEntity(grenade);
    }
  }
}
