package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IDashEntity;
import net.minecraft.util.math.Vec3d;

public class GroundDashGoal<E extends MineCellsEntity & IDashEntity> extends DashGoal<E> {

  public GroundDashGoal(E entity, int releaseTick, int restTick, int lengthTicks, float chance, float speed) {
    super(entity, releaseTick, restTick, lengthTicks, chance, speed);
  }

  @Override
  public boolean canStart() {
    return super.canStart() && this.entity.getY() >= this.target.getY();
  }

  @Override
  protected Vec3d getVelocity() {
    return super.getVelocity().multiply(1.0D, 0.0D, 1.0D);
  }
}
