package com.github.mim1q.minecells.client.gui.screen;

import com.github.mim1q.minecells.block.inventory.CellForgeBlueprintInventory;
import com.github.mim1q.minecells.block.inventory.CellForgeInventory;
import com.github.mim1q.minecells.registry.MineCellsScreenHandlerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CellForgeScreenHandler extends ScreenHandler {
  private final CellForgeInventory inventory;
  private final CellForgeBlueprintInventory blueprintInventory;
  private final PlayerEntity player;

  public CellForgeScreenHandler(int id, PlayerInventory playerInventory, PlayerEntity player) {
    super(MineCellsScreenHandlerTypes.CELL_FORGE, id);
    this.inventory = new CellForgeInventory();
    this.blueprintInventory = new CellForgeBlueprintInventory(player);
    this.player = player;

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        this.addSlot(new Slot(blueprintInventory, j + i * 3, 9 + j * 18, 18 + i * 18));
      }
    }

    for (int i = 0; i < 6; i++) {
      this.addSlot(new Slot(inventory, i, 54 + i * 18, 87));
    }

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 18 + j * 18, 119 + i * 18));
      }
    }

    for (int i = 0; i < 9; i++) {
      this.addSlot(new Slot(playerInventory, i, 18 + i * 18, 177));
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
