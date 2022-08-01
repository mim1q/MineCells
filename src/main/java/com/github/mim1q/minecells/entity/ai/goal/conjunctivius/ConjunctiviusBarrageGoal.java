package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ConjunctiviusBarrageGoal extends ConjunctiviusMoveAroundGoal {

  private int ticks = 0;
  private Entity target;

  public ConjunctiviusBarrageGoal(ConjunctiviusEntity entity) {
    super(entity);
    this.speed = 0.15D;
  }

  @Override
  public boolean canStart() {
    this.target = entity.getTarget();
    return super.canStart()
      && this.entity.barrageCooldown == 0
      && this.target != null
      && this.entity.moving
      && this.entity.isInFullStage()
      && this.entity.getRandom().nextFloat() < 1.0F;
  }

  @Override
  public boolean shouldContinue() {
    this.target = entity.getTarget();
    return this.target != null && this.ticks < 260;
  }

  @Override
  public void start() {
    super.start();
    this.entity.getDataTracker().set(ConjunctiviusEntity.BARRAGE_ACTIVE, true);
  }

  @Override
  public void tick() {
    if (this.ticks > 60) {
      super.tick();
      if (this.ticks % 2 == 0) {
        this.shoot(this.entity, this.target);
      }
    }
    this.ticks++;
  }

  private void shoot(ConjunctiviusEntity entity, Entity target) {
    if (target != null) {
      Vec3d offset = new Vec3d(0.0D, 2.0D, 2.0D).rotateY(MathUtils.radians(entity.getSpawnRot()));
      Vec3d targetPos = target.getPos().add(
        (entity.getRandom().nextDouble() - 0.5D) * 5.0D,
        (entity.getRandom().nextDouble() - 0.5D) * 5.0D,
        (entity.getRandom().nextDouble() - 0.5D) * 5.0D
      );
      ConjunctiviusProjectileEntity.spawn(entity.world, entity.getPos().add(offset), targetPos);
    }
  }

  @Override
  protected int getNextCooldown() {
    return 10;
  }

  @Override
  public void stop() {
    this.ticks = 0;
    this.entity.barrageCooldown = 500;
    this.entity.getDataTracker().set(ConjunctiviusEntity.BARRAGE_ACTIVE, false);
    super.stop();
  }
}
