package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.registry.StatusEffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

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
        int interval = 20 / (amplifier + 1) + 15;
        if ((entity.age % interval == 0 )) {
            entity.damage(DamageSource.MAGIC, 1.0F);
        }

        if ((amplifier == 1 && entity.isInsideWaterOrBubbleColumn()) || amplifier >= 2) {
            List<Entity> entities = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(3.0D), e -> e instanceof LivingEntity);
            StatusEffectInstance effect = new StatusEffectInstance(StatusEffectRegistry.ELECTRIFIED, 60, amplifier - 1, false, false, true);
            for (Entity e : entities) {
                if (e instanceof LivingEntity && (e.distanceTo(entity) <= 3.0D)) {
                    ((LivingEntity)e).addStatusEffect(effect);
                }
            }
        }
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.damage(DamageSource.MAGIC, amplifier * 2.0F);
    }
}
