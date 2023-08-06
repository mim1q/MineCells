package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class ElectrifiedStatusEffect extends StatusEffect {
  public ElectrifiedStatusEffect() {
    super(StatusEffectCategory.HARMFUL, 0xCCF4FF);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    if ((amplifier == 1 && entity.isInsideWaterOrBubbleColumn()) || amplifier >= 2 && entity.age % 5 == 0) {
      List<Entity> entities = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(3.0D), e -> e instanceof LivingEntity);
      StatusEffectInstance effect = new StatusEffectInstance(MineCellsStatusEffects.ELECTRIFIED, 20, amplifier - 1, false, false, true);
      for (Entity e : entities) {
        if (e instanceof LivingEntity && (e.distanceTo(entity) <= 3.0D)) {
          ((LivingEntity) e).addStatusEffect(effect);
        }
      }
    }
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    float damage = 1.0F + amplifier * 2.0F;
    if (entity.isInsideWaterOrBubbleColumn()) {
      damage *= 1.25F;
    }
    entity.damage(entity.getDamageSources().magic(), damage);
    entity.getWorld().playSound(null, entity.getBlockPos(), MineCellsSounds.SHOCK, SoundCategory.NEUTRAL, 1.0F, 0.9F + entity.getRandom().nextFloat() * 0.2F);
  }
}
