package com.github.mim1q.minecells.client.gui.screen;

import com.github.mim1q.minecells.block.inventory.CellForgeInventory;
import com.github.mim1q.minecells.registry.MineCellsScreenHandlerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CellForgeScreenHandler extends ScreenHandler {
  private final CellForgeInventory inventory;
  private final PlayerEntity player;

  public CellForgeScreenHandler(int id, PlayerInventory playerInventory, PlayerEntity player) {
    super(MineCellsScreenHandlerTypes.CELL_FORGE, id);
    this.inventory = new CellForgeInventory();
    this.player = player;
    addSlot(new Slot(inventory, 0, 20, 35));

    for (int i = 0; i < 2; ++i) {
      for (int j = 0; j < 3; ++j) {
        addSlot(new Slot(inventory, 1 + j + i * 3, 42 + j * 18, 35 + i * 18));
      }
    }

    addSlot(new Slot(inventory, 7, 136, 45));

    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }
  }

  public CellForgeScreenHandler(int i, PlayerInventory playerInventory) {
    this(i, playerInventory, playerInventory.player);
  }

  @Override
  public ItemStack transferSlot(PlayerEntity player, int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return true;
  }
}
