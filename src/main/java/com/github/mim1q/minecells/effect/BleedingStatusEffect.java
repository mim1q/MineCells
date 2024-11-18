package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

public class BleedingStatusEffect extends MineCellsStatusEffect {

  public BleedingStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0xFF0000, true, MineCellsEffectFlags.BLEEDING, false);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.damage(MineCellsDamageSource.BLEEDING.get(entity.getWorld(), null), 0.5F);
    if (entity.getWorld() instanceof ServerWorld serverWorld) {
      serverWorld.spawnParticles(
        MineCellsParticles.DROP.get(0xFF0000),
        entity.getX(),
        entity.getY() + entity.getHeight() / 2f,
        entity.getZ(),
        2,
        entity.getWidth() / 4f,
        entity.getHeight() / 4f,
        entity.getWidth() / 4f,
        0.1D
      );
    }
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    int interval = getInterval(amplifier);
    return duration % interval == 0;
  }

  private int getInterval(int amplifier) {
    return Math.max(5, 20 - amplifier * 4);
  }

  public static void apply(LivingEntity entity, int duration) {
    var effect = entity.getStatusEffect(MineCellsStatusEffects.BLEEDING);
    int newLevel = 0;
    if (effect != null) {
      newLevel = effect.getAmplifier() + 1;
    }
    if (newLevel >= 6) {
      entity.damage(MineCellsDamageSource.BLEEDING.get(entity.getWorld(), null), 12.0F);
      entity.removeStatusEffect(MineCellsStatusEffects.BLEEDING);
      return;
    }

    entity.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.BLEEDING, duration, newLevel, false, false, true));
  }
}
