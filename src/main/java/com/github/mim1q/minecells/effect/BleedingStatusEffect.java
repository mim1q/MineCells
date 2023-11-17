package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class BleedingStatusEffect extends MineCellsStatusEffect {

  public BleedingStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0xFF0000, true, MineCellsEffectFlags.BLEEDING, false);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.damage(MineCellsDamageSource.BLEEDING.get(entity.getWorld(), null), 1.0F);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    int interval = getInterval(amplifier);
    return duration % interval == 0;
  }

  private int getInterval(int amplifier) {
    return switch (amplifier) {
      case 0 -> 25;
      case 1 -> 20;
      case 2 -> 15;
      default -> 10;
    };
  }

  public static void apply(LivingEntity entity, int duration) {
    StatusEffectInstance effect = entity.getStatusEffect(MineCellsStatusEffects.BLEEDING);
    int newLevel = 0;
    if (effect != null) {
      int level = effect.getAmplifier();
      if (level == 2) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration, 1, false, true, true));
      }
      newLevel = effect.getAmplifier() == 0 ? 1 : 2;
    }
    entity.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.BLEEDING, duration, newLevel, false, false, true));
  }
}
