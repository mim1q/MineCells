package com.github.mim1q.minecells.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ProtectedStatusEffect extends MineCellsStatusEffect {
  public ProtectedStatusEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0x99BBFF, false, MineCellsEffectFlags.PROTECTED);
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    entity.setInvulnerable(true);
    super.onApplied(entity, attributes, amplifier);
  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    entity.setInvulnerable(false);
      super.onRemoved(entity, attributes, amplifier);
  }
}
