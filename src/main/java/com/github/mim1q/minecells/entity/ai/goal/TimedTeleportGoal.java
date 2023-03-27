package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimedTeleportGoal<E extends HostileEntity> extends TimedActionGoal<E> {
  private Entity target;

  public TimedTeleportGoal(E entity, TimedTeleportSettings settings, Predicate<E> predicate) {
    super(entity, settings, predicate);
    if (settings.shouldStandStill) {
      this.setControls(EnumSet.of(Control.MOVE));
    }
  }

  public TimedTeleportGoal(E entity, Consumer<TimedTeleportSettings> settingsConsumer, Predicate<E> predicate) {
    this(entity, TimedActionSettings.edit(new TimedTeleportSettings(), settingsConsumer), predicate);
  }

  @Override
  public boolean canStart() {
    this.target = this.entity.getTarget();

    return this.target != null && this.target.isAlive() && this.target.isAttackable()
      && (this.entity.distanceTo(this.target) > 10.0D || !this.entity.canSee(this.target))
      && super.canStart();
  }

  @Override
  public void start() {
    super.start();
  }

  @Override
  protected void runAction() {
    this.entity.teleport(this.target.getX(), this.target.getY(), this.target.getZ());
  }

  @Override
  protected void playReleaseSound() {
    super.playReleaseSound();
  }

  public static class TimedTeleportSettings extends TimedActionSettings {
    public boolean shouldStandStill = false;
  }
}
