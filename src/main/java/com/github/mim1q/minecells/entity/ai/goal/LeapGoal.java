package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.ILeapEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class LeapGoal<E extends MineCellsEntity & ILeapEntity> extends Goal {

  protected E entity;
  protected LivingEntity target;
  protected int ticks = 0;
  protected final int actionTick;
  protected final int lengthTicks;
  protected final float chance;
  List<UUID> alreadyAttacked;

  public LeapGoal(E entity, int actionTick, int lengthTicks, float chance) {
    this.actionTick = actionTick;
    this.lengthTicks = lengthTicks;
    this.chance = chance;
    this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    this.entity = entity;
    this.alreadyAttacked = new ArrayList<>();
  }

  @Override
  public boolean canStart() {
    LivingEntity target = this.entity.getTarget();
    if (target == null)
      return false;

    return this.entity.getLeapCooldown() == 0
      && this.entity.canSee(target)
      && this.entity.getY() >= this.entity.getTarget().getY()
      && this.entity.getRandom().nextFloat() < this.chance
      && this.entity.distanceTo(target) <= this.entity.getLeapRange();
  }

  @Override
  public void start() {
    this.target = this.entity.getTarget();
    this.entity.setLeapCharging(true);
    this.ticks = 0;
    this.alreadyAttacked.clear();

    if (!this.entity.world.isClient() && this.entity.getLeapChargeSoundEvent() != null) {
      this.entity.playSound(this.entity.getLeapChargeSoundEvent(), 0.5F, 1.0F);
    }
  }

  @Override
  public boolean shouldContinue() {
    return (this.ticks < this.lengthTicks || !this.entity.isOnGround()) && this.target.isAlive();
  }

  @Override
  public void stop() {
    this.entity.setLeapCharging(false);
    this.entity.setLeapReleasing(false);
    this.entity.setLeapCooldown(this.entity.getLeapMaxCooldown());
  }

  @Override
  public void tick() {
    if (this.target != null) {

      if (this.ticks < this.actionTick) {
        this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.01D);
        this.entity.getLookControl().lookAt(this.target);
      } else if (this.ticks == this.actionTick) {
        this.leap();
      } else if (!this.entity.isOnGround()) {
        this.attack();
      }

      this.ticks++;
    }
  }

  public void leap() {
    this.entity.setLeapCharging(false);
    this.entity.setLeapReleasing(true);

    Vec3d diff = this.target.getPos().add(this.entity.getPos().multiply(-1.0D));
    Vec3d vel = diff.multiply(0.3D, 0.05D, 0.3D).add(0.0D, 0.3D, 0.0D);
    if (vel.length() > 3.0D) {
      vel = vel.normalize().multiply(3.0D);
    }
    this.entity.setVelocity(vel);
    if (!this.entity.world.isClient() && this.entity.getLeapReleaseSoundEvent() != null) {
      this.entity.playSound(this.entity.getLeapReleaseSoundEvent(), 0.5f, 1.0F);
    }
  }

  public void attack() {
    List<PlayerEntity> players = this.entity.world.getEntitiesByClass(PlayerEntity.class, this.entity.getBoundingBox().expand(0.33D), e -> !this.alreadyAttacked.contains(e.getUuid()));
    for (PlayerEntity player : players) {
      player.damage(DamageSource.mob(this.entity), this.entity.getLeapDamage());
      this.alreadyAttacked.add(player.getUuid());
    }
  }
}
