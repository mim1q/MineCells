package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimedShootGoal<E extends HostileEntity> extends TimedActionGoal<E> {
  private Entity target;
  private final BiFunction<Vec3d, Vec3d, Entity> projectileCreator;

  public TimedShootGoal(E entity, TimedShootSettings settings, Predicate<E> predicate) {
    super(entity, settings, predicate);
    this.projectileCreator = settings.projectileCreator;
    this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  public TimedShootGoal(E entity, Consumer<TimedShootSettings> settingsConsumer, Predicate<E> predicate) {
    this(entity, TimedActionSettings.edit(new TimedShootSettings(), settingsConsumer), predicate);
  }

  @Override
  public boolean canStart() {
    this.target = this.entity.getTarget();
    if (target == null || !target.isAlive()) {
      return false;
    }
    return super.canStart();
  }

  @Override
  public void tick() {
    this.entity.getLookControl().lookAt(this.target);
    super.tick();
  }

  @Override
  protected void runAction() {
    if (this.target != null) {
      Entity projectile = projectileCreator.apply(
        this.entity.getPos(),
        this.target.getPos().add(0.0D, this.target.getHeight() * 0.5D, 0.0D)
      );
      this.entity.world.spawnEntity(projectile);
    }
  }

  public static class TimedShootSettings extends TimedActionSettings {
    public BiFunction<Vec3d, Vec3d, Entity> projectileCreator = null;
  }
}
