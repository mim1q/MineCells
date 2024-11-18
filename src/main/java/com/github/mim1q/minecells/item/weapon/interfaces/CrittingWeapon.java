package com.github.mim1q.minecells.item.weapon.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CrittingWeapon extends CritIndicator {
  default float getExtraDamage(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker) {
    return 0.0f;
  }

  boolean canCrit(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker);

  default float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return 4.0F;
  }

  default boolean shouldPlayCritSound(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return true;
  }

  @Override
  default boolean shouldShowCritIndicator(PlayerEntity player, LivingEntity target, ItemStack stack) {
    return canCrit(stack, target, player) && shouldPlayCritSound(stack, target, player);
  }
}
