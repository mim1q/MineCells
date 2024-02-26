package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class JumpBackGoal extends TimedActionGoal<MobEntity> {
  public JumpBackGoal(MobEntity entity, Consumer<TimedActionSettings> settings, Predicate<MobEntity> predicate) {
    super(entity, settings, predicate);
  }

  @Override protected void runAction() {
    if (this.entity.getTarget() == null) return;

    entity.lookAtEntity(this.entity.getTarget(), 360.0F, 360.0F);

    entity.setVelocity(
      entity.getTarget().getPos().subtract(entity.getPos())
        .multiply(1D, 0D, 1D)
        .normalize()
        .multiply(-0.75D)
        .add(0.0, 0.33, 0.0)
    );
  }
}
