package com.github.mim1q.minecells.mixin.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {
  @Invoker("onStatusEffectRemoved")
  void invokeOnStatusEffectRemoved(StatusEffectInstance effect);
}
