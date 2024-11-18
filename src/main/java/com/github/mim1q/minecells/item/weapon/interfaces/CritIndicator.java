package com.github.mim1q.minecells.item.weapon.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CritIndicator {
  boolean shouldShowCritIndicator(@Nullable PlayerEntity player, @Nullable LivingEntity target, ItemStack stack);
}
