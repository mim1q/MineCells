package com.github.mim1q.minecells.block.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CellForgeInventory implements Inventory {
  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public ItemStack getStack(int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    return null;
  }

  @Override
  public ItemStack removeStack(int slot) {
    return null;
  }

  @Override
  public void setStack(int slot, ItemStack stack) {

  }

  @Override
  public void markDirty() {

  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return true;
  }

  @Override
  public void clear() {

  }
}
