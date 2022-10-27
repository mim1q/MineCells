package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedingStatusEffect extends StatusEffect {
  public BleedingStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0xFF0000);
    this.isInstant();
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.damage(MineCellsDamageSource.BLEEDING, 0.5f * (amplifier + 1));
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    int interval = duration > 200 ? 10
      : duration > 100 ? 20
      : 30;
    return duration % interval == 0;
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    ((LivingEntityAccessor) entity).setMineCellsFlag(MineCellsEffectFlags.BLEEDING, true);
    super.onApplied(entity, attributes, amplifier);
  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    ((LivingEntityAccessor) entity).setMineCellsFlag(MineCellsEffectFlags.BLEEDING, false);
    super.onRemoved(entity, attributes, amplifier);
  }
}
