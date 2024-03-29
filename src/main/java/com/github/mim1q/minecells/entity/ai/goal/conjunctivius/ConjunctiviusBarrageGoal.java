package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.function.Supplier;

public abstract class ConjunctiviusBarrageGoal extends ConjunctiviusMoveAroundGoal {
  protected int ticks = 0;
  private Entity target;
  private final float chance;
  private final int interval;
  private final int length;
  private final int barrageCooldown;

  public ConjunctiviusBarrageGoal(ConjunctiviusEntity entity, double speed, float chance, int interval, int length, int cooldown) {
    super(entity);
    this.speed = speed;
    this.chance = chance;
    this.interval = interval;
    this.length = length;
    this.barrageCooldown = cooldown;
  }

  @Override
  public boolean canStart() {
    this.target = entity.getTarget();
    return super.canStart()
      && this.entity.barrageCooldown <= 0
      && this.target != null
      && this.entity.moving
      && this.entity.canAttack()
      && this.entity.getRandom().nextFloat() < this.chance;
  }

  @Override
  public boolean shouldContinue() {
    this.target = entity.getTarget();
    return this.target != null && this.ticks < this.length + 60 && this.entity.canAttack();
  }

  @Override
  public void start() {
    super.start();
    this.entity.getDataTracker().set(ConjunctiviusEntity.BARRAGE_ACTIVE, true);
    this.entity.playSound(MineCellsSounds.CHARGE, 2.0F, 1.0F);
  }

  @Override
  public void tick() {
    if (this.entity.getWorld().isClient) return;
    if (this.ticks > 60) {
      super.tick();
      if (this.ticks % 6 == 0) {
        var serverWorld = ((ServerWorld) this.entity.getWorld());
        serverWorld.getServer().getPlayerManager().sendToAround(
          null, entity.getX(), entity.getY(), entity.getZ(), 32.0D, entity.getWorld().getRegistryKey(),
          new PlaySoundS2CPacket(RegistryEntry.of(MineCellsSounds.CONJUNCTIVIUS_SHOT), SoundCategory.HOSTILE, entity.getX(), entity.getY(), entity.getZ(), 0.25F, 1.0F, 0)
        );
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
    this.entity.barrageCooldown = this.barrageCooldown;
    this.entity.getDataTracker().set(ConjunctiviusEntity.BARRAGE_ACTIVE, false);
    super.stop();
  }

  public static class Targeted extends ConjunctiviusBarrageGoal {

    public Targeted(ConjunctiviusEntity entity, double speed, float chance, int length, int cooldown) {
      super(entity, speed, chance, 8, length, cooldown);
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
          ConjunctiviusProjectileEntity.spawn(entity.getWorld(), entity.getPos().add(0.0D, 2.5D, 0.0D), targetPos, this.entity);
        }
      }
    }
  }

  public static class Around extends ConjunctiviusBarrageGoal {
    private final Supplier<Integer> count;

    public Around(ConjunctiviusEntity entity, double speed, float chance, int length, int cooldown, Supplier<Integer> count) {
      super(entity, speed, chance, 10, length, cooldown);
      this.count = count;
    }

    @Override
    protected void shoot(ConjunctiviusEntity entity, Entity target) {
      if (target != null) {
        for (int i = 0; i < this.count.get(); i++) {
          Vec3d targetPos = target.getPos().add(
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D,
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D + 3.0D,
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D
          );
          ConjunctiviusProjectileEntity.spawn(entity.getWorld(), entity.getPos().add(0.0D, 2.5D, 0.0D), targetPos, this.entity);
        }
      }
    }
  }
}
