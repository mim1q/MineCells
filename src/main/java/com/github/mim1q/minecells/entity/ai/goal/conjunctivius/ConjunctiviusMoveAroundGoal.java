package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class ConjunctiviusMoveAroundGoal extends Goal {

  protected final ConjunctiviusEntity entity;
  protected double speed = 0.05D;
  private int cooldown = 0;
  private Vec3d targetPos;

  public ConjunctiviusMoveAroundGoal(ConjunctiviusEntity entity) {
    this.entity = entity;
    this.targetPos = this.entity.getPos();
    this.setControls(EnumSet.of(Control.MOVE));
  }

  @Override
  public boolean canStart() {
    return this.entity.isInFullStage()
      && this.entity.getSpawnPos() != null
      && this.entity.getRoomBox() != null;
  }

  @Override
  public boolean shouldContinue() {
    return this.canStart();
  }

  @Override
  public void start() {
    super.start();
    this.targetPos = this.entity.getSpawnPos();
  }

  @Override
  public void tick() {
    this.entity.moving = this.isMoving();
    if (this.cooldown == 0) {
      this.entity.setVelocity(this.entity.getVelocity().add(this.targetPos.subtract(this.entity.getPos()).normalize().multiply(this.speed)));
      if (this.targetPos.distanceTo(this.entity.getPos()) < 2.0D || this.entity.horizontalCollision || this.entity.verticalCollision) {
        this.targetPos = this.getRandomTargetPos();
        this.cooldown = this.getNextCooldown();
      }
    } else {
      this.cooldown--;
      if (this.cooldown == 0 && this.entity.getStage() < 7) {
        this.entity.playSound(MineCellsSounds.CONJUNCTIVIUS_MOVE, 1.0F, 1.0F);
      }
    }
  }

  @Override
  public void stop() {
    this.cooldown = 0;
    this.entity.moving = false;
    this.targetPos = this.entity.getSpawnPos();
  }

  public boolean isMoving() {
    return this.cooldown == 0;
  }

  private Vec3d getRandomTargetPos() {
    Vec3d center = this.entity.getSpawnPos();
    return center.add(
      -4.0D + this.entity.getRandom().nextDouble() * 8.0D,
      -5.0D + this.entity.getRandom().nextDouble() * 3.0D,
      -1.0D - this.entity.getRandom().nextDouble() * 2.0D
      );
  }

  protected int getNextCooldown() {
    return this.entity.getRandom().nextInt(40) + 40;
  }

  @Override
  public boolean shouldRunEveryTick() {
    return true;
  }
}
