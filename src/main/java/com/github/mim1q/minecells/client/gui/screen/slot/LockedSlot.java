package com.github.mim1q.minecells.client.gui.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class LockedSlot extends Slot {
  public LockedSlot(Inventory inventory, int index, int x, int y) {
    super(inventory, index, x, y);
  }

  @Override
  public boolean canInsert(ItemStack stack) {
    return false;
  }
}
