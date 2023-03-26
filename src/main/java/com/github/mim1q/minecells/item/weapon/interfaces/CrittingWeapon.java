package com.github.mim1q.minecells.item.weapon.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CrittingWeapon {
  boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker);
  default float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return 4.0F;
  }
  default boolean shouldPlayCritSound(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return true;
  }
}
