package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public abstract class ConjunctiviusBarrageGoal extends ConjunctiviusMoveAroundGoal {

  protected int ticks = 0;
  private Entity target;
  private final float chance;
  private final int interval;

  public ConjunctiviusBarrageGoal(ConjunctiviusEntity entity, double speed, float chance, int interval) {
    super(entity);
    this.speed = speed;
    this.chance = chance;
    this.interval = interval;
  }

  @Override
  public boolean canStart() {
    this.target = entity.getTarget();
    return super.canStart()
      && this.entity.barrageCooldown == 0
      && this.target != null
      && this.entity.moving
      && this.entity.canAttack()
      && this.entity.getRandom().nextFloat() < this.chance;
  }

  @Override
  public boolean shouldContinue() {
    this.target = entity.getTarget();
    return this.target != null && this.ticks < 200 && this.entity.canAttack();
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
      if (this.ticks % 6 == 0) {
        this.entity.playSound(SoundRegistry.CONJUNCTIVIUS_SHOT, 2.0F, 1.0F + this.entity.getRandom().nextFloat() * 0.1F);
      }
      if (this.ticks % this.interval == 0) {
        this.shoot(this.entity, this.target);
      }
    }
    this.ticks++;
  }

  protected abstract void shoot(ConjunctiviusEntity entity, Entity target);

  @Override
  protected int getNextCooldown() {
    return 10;
  }

  @Override
  public void stop() {
    this.ticks = 0;
    this.entity.barrageCooldown = this.entity.stageAdjustedCooldown(200);
    this.entity.getDataTracker().set(ConjunctiviusEntity.BARRAGE_ACTIVE, false);
    super.stop();
  }

  public static class Targeted extends ConjunctiviusBarrageGoal {

    public Targeted(ConjunctiviusEntity entity, double speed, float chance) {
      super(entity, speed, chance, 4);
    }

    @Override
    protected void shoot(ConjunctiviusEntity entity, Entity target) {
      if (target != null) {
        for (int i = 0; i < 2; i++) {
          Vec3d targetPos = target.getPos().add(
            (entity.getRandom().nextDouble() - 0.5D) * 2.0D,
            (entity.getRandom().nextDouble() - 0.5D) * 2.0D + 2.0D,
            (entity.getRandom().nextDouble() - 0.5D) * 2.0D
          );
          ConjunctiviusProjectileEntity.spawn(entity.world, entity.getPos().add(0.0D, 2.5D, 0.0D), targetPos, this.entity);
        }
      }
    }
  }

  public static class Around extends ConjunctiviusBarrageGoal {

    public Around(ConjunctiviusEntity entity, double speed, float chance) {
      super(entity, speed, chance, 1);
    }

    @Override
    protected void shoot(ConjunctiviusEntity entity, Entity target) {
      if (target != null) {
        for (int i = 0; i < 5; i++) {
          Vec3d targetPos = target.getPos().add(
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D,
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D + 3.0D,
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D
          );
          ConjunctiviusProjectileEntity.spawn(entity.world, entity.getPos().add(0.0D, 2.5D, 0.0D), targetPos, this.entity);
        }
      }
    }
  }
}
