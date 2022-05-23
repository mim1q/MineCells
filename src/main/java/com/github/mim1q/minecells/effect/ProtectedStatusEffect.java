package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ProtectedStatusEffect extends StatusEffect {
    public ProtectedStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x99BBFF);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.setInvulnerable(true);
        ((LivingEntityAccessor)entity).setIsProtected(true);
        super.onApplied(entity, attributes, amplifier);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.setInvulnerable(false);
        ((LivingEntityAccessor)entity).setIsProtected(false);
        super.onRemoved(entity, attributes, amplifier);
    }
}
