package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class MineCellsStatusEffect extends StatusEffect {

  private final boolean shouldApplyUpdateEffect;
  private final MineCellsEffectFlags flag;

  public MineCellsStatusEffect(StatusEffectCategory statusEffectCategory, int color, boolean applyUpdateEffect, MineCellsEffectFlags flag) {
    super(statusEffectCategory, color);
    this.shouldApplyUpdateEffect = applyUpdateEffect;
    this.flag = flag;
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return this.shouldApplyUpdateEffect;
  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    if (flag != null) {
      ((LivingEntityAccessor) entity).setMineCellsFlag(flag, false);
    }
    super.onRemoved(entity, attributes, amplifier);
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    if (flag != null) {
      ((LivingEntityAccessor) entity).setMineCellsFlag(flag, true);
    }
    super.onApplied(entity, attributes, amplifier);
  }
}
