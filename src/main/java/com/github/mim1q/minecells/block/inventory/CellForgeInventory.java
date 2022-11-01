package com.github.mim1q.minecells.block.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class CellForgeInventory implements Inventory {
  private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);

  public CellForgeInventory() { }

  @Override
  public int size() {
    return 6;
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
    return stacks.get(slot).split(amount);
  }

  @Override
  public ItemStack removeStack(int slot) {
    return stacks.get(slot);
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    stacks.set(slot, stack);
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
    stacks.clear();
  }

  @Override
  public void onClose(PlayerEntity player) {
    for (int i = 0; i < size(); i++) {
      if (!getStack(i).isEmpty()) {
        player.dropItem(getStack(i), false);
      }
    }
  }
}
