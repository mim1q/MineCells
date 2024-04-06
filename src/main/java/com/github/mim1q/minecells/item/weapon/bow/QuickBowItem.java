package com.github.mim1q.minecells.item.weapon.bow;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class QuickBowItem extends CustomBowItem {
  public QuickBowItem(Settings settings) {
    super(settings, CustomArrowType.QUICK);
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    super.usageTick(world, user, stack, remainingUseTicks);
    if (world.isClient) return;

    if (remainingUseTicks == 1) {
      shoot(world, user, stack);
    }
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) { }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return this.getDrawTime(stack) + 2;
  }

  @Override
  public float getFovMultiplier(PlayerEntity player, ItemStack stack) {
    if (player.isUsingItem()) {
      return 0.9f;
    }
    return 1.0f;
  }
}
