package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ShootGoal<E extends MineCellsEntity & IShootEntity> extends Goal {

  protected E entity;
  protected LivingEntity target;
  protected int ticks = 0;
  protected final int actionTick;
  protected final int lengthTicks;
  protected final float chance;

  public ShootGoal(E entity, int actionTick, int lengthTicks, float chance) {
    this.actionTick = actionTick;
    this.lengthTicks = lengthTicks;
    this.chance = chance;
    this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    this.entity = entity;
  }

  @Override
  public boolean canStart() {
    LivingEntity target = this.entity.getTarget();
    if (target == null)
      return false;

    return this.entity.getShootCooldown() == 0
      && this.entity.canSee(target)
      && this.entity.getRandom().nextFloat() < this.chance;
  }

  @Override
  public void start() {
    this.target = this.entity.getTarget();
    this.entity.setShootCharging(true);
    this.ticks = 0;

    if (!this.entity.getWorld().isClient() && this.entity.getShootChargeSoundEvent() != null) {
      this.entity.playSound(this.entity.getShootChargeSoundEvent(), 1.0F, 1.0f);
    }
  }

  @Override
  public boolean shouldContinue() {
    return (this.ticks < this.lengthTicks && this.target.isAlive() && this.target != null);
  }

  @Override
  public void stop() {
    this.entity.setShootCharging(false);
    this.entity.setShootReleasing(false);
    this.entity.setShootCooldown(this.entity.getShootMaxCooldown());
  }

  @Override
  public void tick() {
    if (this.target != null) {
      this.entity.getLookControl().lookAt(target);
      if (this.ticks == this.actionTick && !this.entity.getWorld().isClient()) {
        if (this.entity.getShootReleaseSoundEvent() != null) {
          this.entity.playSound(this.entity.getShootReleaseSoundEvent(), 1.0F, 1.0f);
        }
        this.shoot(this.target);
      }
    }
    this.ticks++;
  }

  public void shoot(LivingEntity target) {
    this.entity.setShootCharging(false);
    this.entity.setShootReleasing(true);
  }
}
