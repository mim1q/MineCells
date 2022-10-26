package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CursedStatusEffect extends StatusEffect {
  public CursedStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0x000000);
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    ((LivingEntityAccessor) entity).setMineCellsFlag(MineCellsEffectFlags.CURSED, true);
  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    ((LivingEntityAccessor) entity).setMineCellsFlag(MineCellsEffectFlags.CURSED, false);
  }
}
