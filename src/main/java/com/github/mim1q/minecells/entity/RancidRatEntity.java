package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.LeapGoal;
import com.github.mim1q.minecells.entity.interfaces.ILeapEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class RancidRatEntity extends MineCellsEntity implements ILeapEntity {

  public final AnimationProperty torsoRotation = new AnimationProperty(0.0F);

  private static final TrackedData<Boolean> LEAP_CHARGING = DataTracker.registerData(RancidRatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> LEAP_RELEASING = DataTracker.registerData(RancidRatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Integer> LEAP_COOLDOWN = DataTracker.registerData(RancidRatEntity.class, TrackedDataHandlerRegistry.INTEGER);

  public RancidRatEntity(EntityType<RancidRatEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(LEAP_CHARGING, false);
    this.dataTracker.startTracking(LEAP_RELEASING, false);
    this.dataTracker.startTracking(LEAP_COOLDOWN, this.getLeapMaxCooldown());
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2F, false));
    this.goalSelector.add(0, new LeapGoal<>(this, 10, 20, 0.5F));
  }

  @Override
  public void tick() {
    super.tick();
    this.decrementCooldown(LEAP_COOLDOWN);
  }

  public static DefaultAttributeContainer.Builder createRancidRatAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
  }

  @Override
  public boolean isLeapCharging() {
    return this.dataTracker.get(LEAP_CHARGING);
  }

  @Override
  public void setLeapCharging(boolean charging) {
    this.dataTracker.set(LEAP_CHARGING, charging);
  }

  @Override
  public boolean isLeapReleasing() {
    return this.dataTracker.get(LEAP_RELEASING);
  }

  @Override
  public void setLeapReleasing(boolean releasing) {
    this.dataTracker.set(LEAP_RELEASING, releasing);
  }

  @Override
  public int getLeapCooldown() {
    return this.dataTracker.get(LEAP_COOLDOWN);
  }

  @Override
  public void setLeapCooldown(int ticks) {
    this.dataTracker.set(LEAP_COOLDOWN, ticks);
  }

  @Override
  public int getLeapMaxCooldown() {
    return 20 + this.random.nextInt(20);
  }

  @Override
  public float getLeapDamage() {
    return 15.0F;
  }

  @Override
  public double getLeapRange() {
    return 5.0D;
  }

  @Override
  public SoundEvent getLeapChargeSoundEvent() {
    return MineCellsSounds.RANCID_RAT_CHARGE;
  }

  @Override
  public SoundEvent getLeapReleaseSoundEvent() {
    return MineCellsSounds.LEAPING_ZOMBIE_RELEASE;
  }
}
