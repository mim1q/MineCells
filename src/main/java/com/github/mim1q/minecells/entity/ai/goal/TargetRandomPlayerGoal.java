package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TargetRandomPlayerGoal<E extends HostileEntity> extends Goal {

  private int ticks = 0;
  protected final E entity;
  protected List<PlayerEntity> targets = new ArrayList<>();
  protected PlayerEntity currentTarget = null;

  public TargetRandomPlayerGoal(E entity) {
    this.entity = entity;
    this.setControls(EnumSet.of(Control.TARGET));
  }

  @Override
  public boolean canStart() {
    this.targets = this.getTargetablePlayers();
    return !targets.isEmpty();
  }

  @Override
  public boolean shouldContinue() {
    return this.currentTarget != null && this.currentTarget.isAlive();
  }

  @Override
  public void start() {
    this.ticks = 0;
  }

  @Override
  public void tick() {
    if (this.ticks % 40 == 0) {
      this.targets = this.getTargetablePlayers();
      this.entity.setTarget(this.selectTarget());
    }
    this.ticks++;
  }

  @Override
  public void stop() {
    this.entity.setTarget(null);
  }

  @Override
  public boolean shouldRunEveryTick() {
    return true;
  }

  protected PlayerEntity selectTarget() {
    if (this.targets.isEmpty()) {
      return null;
    }
    return this.targets.get(this.entity.getRandom().nextInt(this.targets.size()));
  }

  protected List<PlayerEntity> getTargetablePlayers() {
    double range = this.entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 2.0D;
    return this.entity.world.getPlayers(TargetPredicate.DEFAULT, this.entity, Box.of(this.entity.getPos(), range, range, range).expand(2.0D));
  }
}
