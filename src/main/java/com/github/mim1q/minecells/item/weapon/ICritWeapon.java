package com.github.mim1q.minecells.item.weapon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICritWeapon {
  boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker);
  default float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return 4.0F;
  }
}
