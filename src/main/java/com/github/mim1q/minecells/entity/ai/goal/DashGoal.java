package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IDashEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DashGoal<E extends MineCellsEntity & IDashEntity> extends Goal {

  protected final E entity;
  protected LivingEntity target;
  protected Vec3d velocity;
  protected double distance;
  protected double distanceTravelled;
  protected int ticks = 0;
  protected final int releaseTick;
  protected final int restTick;
  protected final int lengthTicks;
  protected final float chance;
  protected final float speed;
  protected List<Entity> alreadyAttacked;

  public DashGoal(E entity, int releaseTick, int restTick, int lengthTicks, float chance, float speed) {
    this.entity = entity;
    this.releaseTick = releaseTick;
    this.restTick = restTick;
    this.lengthTicks = lengthTicks;
    this.chance = chance;
    this.speed = speed;
    this.alreadyAttacked = new ArrayList<>();

    this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  @Override
  public boolean canStart() {
    this.target = this.entity.getTarget();

    return this.target != null
      && this.entity.getDashCooldown() == 0
      && this.entity.canSee(this.target)
      && this.entity.getPos().distanceTo(this.target.getPos()) < 10.0F
      && this.entity.getRandom().nextFloat() < this.chance;
  }

  @Override
  public void start() {
    this.entity.setDashCharging(true);
    this.ticks = 0;
    this.distanceTravelled = 0;
    this.alreadyAttacked.clear();

    if (!this.entity.world.isClient() && this.entity.getDashChargeSoundEvent() != null) {
      this.entity.playSound(this.entity.getDashChargeSoundEvent(), 1.0F, 1.0F);
    }
  }

  @Override
  public void stop() {
    this.entity.setDashCharging(false);
    this.entity.setDashReleasing(false);
    this.entity.setDashCooldown(this.entity.getDashMaxCooldown());
  }

  @Override
  public boolean shouldContinue() {
    return this.target != null && this.target.isAlive() && this.ticks < this.lengthTicks;
  }

  @Override
  public void tick() {
    if (this.ticks == this.restTick || this.distanceTravelled > this.distance + 2.0D) {
      this.entity.setDashReleasing(false);
    } else if (this.ticks == this.releaseTick) {
      this.velocity = this.getVelocity();
      this.distance = this.target.getPos().distanceTo(this.entity.getPos());
      this.entity.setDashCharging(false);
      this.entity.setDashReleasing(true);
      if (this.entity.getDashReleaseSoundEvent() != null) {
        this.entity.playSound(this.entity.getDashReleaseSoundEvent(), 1.0F, 1.0F);
      }
    }
    if (this.entity.isDashReleasing()) {
      this.moveToTarget();
      this.attack();
    } else if (this.entity.isDashCharging()) {
      this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.01F);
      this.entity.move(MovementType.SELF, this.getVelocity().multiply(0.01F));
      this.entity.getLookControl().lookAt(this.target, 360.0F, 360.0F);
      this.entity.getNavigation().stop();
    }

    this.ticks++;
  }

  protected Vec3d getVelocity() {
    return this.target.getPos()
      .add(0.0D, this.target.getStandingEyeHeight(), 0.0D)
      .subtract(this.entity.getPos())
      .normalize()
      .multiply(this.speed);
  }

  protected void moveToTarget() {
    this.entity.move(MovementType.SELF, this.velocity);
    this.distanceTravelled += this.speed;
  }

  public void attack() {
    List<PlayerEntity> players = this.entity.world.getEntitiesByClass(PlayerEntity.class, this.entity.getBoundingBox().expand(0.33D), e -> !this.alreadyAttacked.contains(e));
    for (PlayerEntity player : players) {
      player.damage(DamageSource.mob(this.entity), this.entity.getDashDamage());
      this.alreadyAttacked.add(player);
    }
  }

  @Override
  public boolean shouldRunEveryTick() {
    return true;
  }
}
