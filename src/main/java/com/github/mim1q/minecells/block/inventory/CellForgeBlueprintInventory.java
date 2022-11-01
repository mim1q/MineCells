package com.github.mim1q.minecells.block.inventory;

import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class CellForgeBlueprintInventory implements Inventory {
  private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size(), Items.STONE.getDefaultStack());

  public CellForgeBlueprintInventory(PlayerEntity player) {
    if (!player.getWorld().isClient()) {
      System.out.println(player.getServer().getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE));
    }
  }

  @Override
  public int size() {
    return 3 * 9;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public ItemStack getStack(int slot) {
    return stacks.get(slot);
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeStack(int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setStack(int slot, ItemStack stack) {

  }

  @Override
  public void markDirty() {

  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return false;
  }

  @Override
  public void clear() {

  }
}
