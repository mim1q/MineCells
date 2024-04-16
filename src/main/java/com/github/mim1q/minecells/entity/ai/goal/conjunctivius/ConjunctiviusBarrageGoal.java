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

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ConjunctiviusBarrageGoal extends ConjunctiviusMoveAroundGoal {
  protected int ticks = 0;
  private Entity target;
  protected final BarrageSettings settings;

  public ConjunctiviusBarrageGoal(ConjunctiviusEntity entity, Consumer<BarrageSettings> settings) {
    super(entity);

    var settingsObj = new BarrageSettings();
    settings.accept(settingsObj);
    this.settings = settingsObj;

    this.speed = this.settings.speed;
  }

  @Override
  public boolean canStart() {
    this.target = entity.getTarget();
    return super.canStart()
      && this.entity.barrageCooldown <= 0
      && this.target != null
      && this.entity.moving
      && this.entity.canAttack()
      && this.entity.getRandom().nextFloat() < settings.chance;
  }

  @Override
  public boolean shouldContinue() {
    this.target = entity.getTarget();
    return this.target != null && this.ticks < settings.length + 60 && this.entity.canAttack();
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
      if (this.ticks % settings.interval == 0) {
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
    this.entity.barrageCooldown = settings.cooldown;
    this.entity.getDataTracker().set(ConjunctiviusEntity.BARRAGE_ACTIVE, false);
    super.stop();
  }

  public static class Targeted extends ConjunctiviusBarrageGoal {

    public Targeted(ConjunctiviusEntity entity, Consumer<BarrageSettings> settings) {
      super(entity, settings);
    }

    @Override
    protected void shoot(ConjunctiviusEntity entity, Entity target) {
      if (target != null) {
        Vec3d targetPos = target.getPos().add(
          (entity.getRandom().nextDouble() - 0.5D) * 2.0D,
          (entity.getRandom().nextDouble() - 0.5D) * 2.0D + 2.0D,
          (entity.getRandom().nextDouble() - 0.5D) * 2.0D
        );
        ConjunctiviusProjectileEntity.spawn(entity.getWorld(), entity.getPos().add(0.0D, 2.5D, 0.0D), targetPos, this.entity);
      }
    }
  }

  public static class Around extends ConjunctiviusBarrageGoal {
    public Around(ConjunctiviusEntity entity, Consumer<BarrageSettings> settings) {
      super(entity, settings);
    }

    @Override
    protected void shoot(ConjunctiviusEntity entity, Entity target) {
      if (target != null) {
        for (int i = 0; i < settings.count.get(); i++) {
          Vec3d targetPos = entity.getPos().add(
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D,
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D + 2.5D,
            (entity.getRandom().nextDouble() - 0.5D) * 10.0D
          );
          ConjunctiviusProjectileEntity.spawn(entity.getWorld(), entity.getPos().add(0.0D, 2.5D, 0.0D), targetPos, this.entity);
        }
      }
    }
  }

  public static class BarrageSettings {
    public float chance = 0.5f;
    public float speed = 0.05f;
    public int interval = 8;
    public int length = 40;
    public int cooldown = 200;
    public Supplier<Integer> count = () -> 1;
  }
}
