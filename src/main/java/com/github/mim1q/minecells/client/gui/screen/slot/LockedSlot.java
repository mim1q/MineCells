package com.github.mim1q.minecells.client.gui.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class LockedSlot extends Slot {
  public final int originalX;
  public final int originalY;
  public boolean enabled = true;

  public LockedSlot(Inventory inventory, int index, int x, int y) {
    super(inventory, index, x, y);
    this.originalX = x;
    this.originalY = y;
  }

  @Override
  public boolean canInsert(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
