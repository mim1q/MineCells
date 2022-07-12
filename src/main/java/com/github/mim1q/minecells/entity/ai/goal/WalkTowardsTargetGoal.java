package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class WalkTowardsTargetGoal extends MeleeAttackGoal {

  protected double minDistance;

  public WalkTowardsTargetGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle, double minDistance) {
    super(mob, speed, pauseWhenMobIdle);
    this.minDistance = minDistance;
  }

  public WalkTowardsTargetGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
    this(mob, speed, pauseWhenMobIdle, 0.0F);
  }

  @Override
  public boolean canStart() {
    return super.canStart() && this.mob.distanceTo(this.mob.getTarget()) >= minDistance;
  }

  @Override
  public boolean shouldContinue() {
    return super.shouldContinue() && this.mob.distanceTo(this.mob.getTarget()) >= minDistance;
  }

  @Override
  public void attack(LivingEntity target, double squaredDistance) {
  }
}