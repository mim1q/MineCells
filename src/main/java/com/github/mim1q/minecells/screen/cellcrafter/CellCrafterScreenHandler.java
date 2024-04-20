package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.registry.MineCellsScreenHandlerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class CellCrafterScreenHandler extends ScreenHandler {
  public CellCrafterScreenHandler(int syncId, PlayerInventory playerInventory) {
    super(MineCellsScreenHandlerTypes.CELL_FORGE_SCREEN_HANDLER, syncId);
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
