package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InterdimensionalRuneItem extends Item {
  public InterdimensionalRuneItem(Settings settings) {
    super(settings);
  }

  @Override
  public ItemStack getDefaultStack() {
    ItemStack stack = super.getDefaultStack();
    stack.setDamage(getMaxDamage());
    return stack;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    PlayerEntityAccessor userAccesor = (PlayerEntityAccessor) user;
    if (!world.isClient && userAccesor.getCells() > 0) {
      userAccesor.setCells(userAccesor.getCells() - 1);
      stack.setDamage(stack.getDamage() - 1);
      if (stack.getDamage() == 0) {
        ItemStack newStack = ItemUsage.exchangeStack(stack, user, new ItemStack(MineCellsItems.CHARGED_INTERDIMENSIONAL_RUNE));
        return TypedActionResult.success(newStack, true);
      }
      return TypedActionResult.success(stack, true);
    }
    return TypedActionResult.pass(stack);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    final int maxCells = stack.getMaxDamage();
    final int cells = maxCells - stack.getDamage();
    tooltip.add(Text.translatable("item.minecells.interdimensional_rune.tooltip"));
    tooltip.add(Text.translatable("item.minecells.interdimensional_rune.tooltip2", cells, maxCells));
  }
}