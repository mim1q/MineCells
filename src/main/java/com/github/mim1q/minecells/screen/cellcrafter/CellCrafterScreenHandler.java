package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.registry.MineCellsScreenHandlerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CellCrafterScreenHandler extends ScreenHandler {
  public CellCrafterScreenHandler(int syncId, PlayerInventory playerInventory) {
    super(MineCellsScreenHandlerTypes.CELL_FORGE_SCREEN_HANDLER, syncId);

    var xOffset = 8;
    var yOffset = 81;

    for (var i = 0; i < 27; ++i) {
      this.addSlot(new Slot(playerInventory, 9 + i, (i % 9) * 18 + xOffset, (i / 9) * 18 + yOffset));
    }

    for (var i = 0; i < 9; ++i) {
      this.addSlot(new Slot(playerInventory, i, i * 18 + xOffset, yOffset + 58));
    }
  }


  @Override
  public ItemStack quickMove(PlayerEntity player, int slot) {
    return null;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return true;
  }
}
