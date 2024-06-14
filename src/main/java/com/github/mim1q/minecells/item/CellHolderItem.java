package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.util.TextUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Math.min;

public class CellHolderItem extends Item {
  private static final String EMPTY_KEY = "item.minecells.cell_holder.empty";
  private static final String CELL_COUNT_KEY = "item.minecells.cell_holder.cell_count";
  private static final String FULL_KEY = "item.minecells.cell_holder.full";

  public CellHolderItem(Settings settings) {
    super(settings);

  }

  @Override
  public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
    var maxToDeposit = clickType == ClickType.LEFT ? 64 : 1;

    if (otherStack.isOf(MineCellsItems.MONSTER_CELL)) {
      var count = min(maxToDeposit, otherStack.getCount());
      otherStack.decrement(count);
      setCellCount(stack, getCellCount(stack) + count);
      return true;
    } else if (otherStack.isEmpty() && clickType == ClickType.RIGHT) {
      var maxToWithdraw = min(64, getCellCount(stack));
      if (maxToWithdraw == 0) return false;

      var newStack = new ItemStack(MineCellsItems.MONSTER_CELL, maxToWithdraw);
      setCellCount(stack, getCellCount(stack) - maxToWithdraw);
      cursorStackReference.set(newStack);

      return true;
    }

    return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
  }

  @Override
  public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
    return super.onStackClicked(stack, slot, clickType, player);
  }

  @Override public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);

    var count = getCellCount(stack);
    if (count == 0) {
      TextUtils.addDescription(tooltip, EMPTY_KEY);
    } else {
      tooltip.add(Text.translatable(CELL_COUNT_KEY, count));
      tooltip.add(Text.translatable(FULL_KEY));
    }
  }

  public static int getCellCount(ItemStack stack) {
    var nbt = stack.getNbt();
    if (nbt == null) return 0;
    return nbt.getInt("Cells");
  }

  public static void setCellCount(ItemStack stack, int count) {
    stack.getOrCreateNbt().putInt("Cells", count);
  }


}
