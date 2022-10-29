package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedingStatusEffect extends MineCellsStatusEffect {

  public BleedingStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0xFF0000, true, MineCellsEffectFlags.BLEEDING, false);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.damage(MineCellsDamageSource.BLEEDING, 1.0f);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    int interval = getInterval(amplifier);
    return duration % interval == 0;
  }

  private int getInterval(int amplifier) {
    if (amplifier == 0) return 60;
    if (amplifier == 1) return 40;
    if (amplifier == 2) return 20;
    return 10;
  }
}
