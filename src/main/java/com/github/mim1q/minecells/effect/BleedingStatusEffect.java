package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedingStatusEffect extends MineCellsStatusEffect {
  public BleedingStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0xFF0000, true, MineCellsEffectFlags.BLEEDING);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.damage(MineCellsDamageSource.BLEEDING, 0.75f * (amplifier + 1));
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    int interval = duration > 200 ? 10
      : duration > 100 ? 20
      : 30;
    return duration % interval == 0;
  }
}
