package com.github.mim1q.minecells.item.weapon.shield;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CustomShieldItem extends Item {
  private static final int MAX_USE_DURATION = 60 * 60 * 20;

  public CustomShieldItem(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BLOCK;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return MAX_USE_DURATION;
  }
}
