package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.mixin.entity.MobEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;

public class FrozenStatusEffect extends MineCellsStatusEffect {
  private final boolean slow;

  public FrozenStatusEffect(MineCellsEffectFlags flag, int color, boolean slow) {
    super(StatusEffectCategory.HARMFUL, color, false, flag, false);
    this.slow = slow;
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
    if (slow) {
      entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100 * (amplifier + 1), amplifier, false, false, true));
    }
    if (entity instanceof MobEntity mob) {
      ((MobEntityAccessor)mob).getGoalSelector().getRunningGoals().forEach(PrioritizedGoal::stop);
    }
  }
}
