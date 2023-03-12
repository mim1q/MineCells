package com.github.mim1q.minecells.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;

public class FrozenStatusEffect extends MineCellsStatusEffect {
  public FrozenStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0x22AAFF, false, MineCellsEffectFlags.FROZEN, false);
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    super.onApplied(entity, attributes, amplifier);
    if (entity instanceof MobEntity mob) {
      mob.getNavigation().stop();
    }
  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    super.onRemoved(entity, attributes, amplifier);
    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100 * (amplifier + 1), amplifier, false, false, true));
  }
}
