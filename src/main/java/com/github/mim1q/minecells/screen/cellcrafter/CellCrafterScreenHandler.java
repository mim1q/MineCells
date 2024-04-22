package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsScreenHandlerTypes;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class CellCrafterScreenHandler extends ScreenHandler {
  public final CellCrafterBlockEntity entity;
  public final SyncedProperty<BlockPos> blockPos = createProperty(BlockPos.class, null);

  public CellCrafterScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, null);
  }

  public CellCrafterScreenHandler(int syncId, PlayerInventory playerInventory, CellCrafterBlockEntity entity) {
    super(MineCellsScreenHandlerTypes.CELL_FORGE_SCREEN_HANDLER, syncId);
    this.entity = entity;

    var xOffset = 8;
    var yOffset = 81;

    for (var i = 0; i < 27; ++i) {
      this.addSlot(new Slot(playerInventory, 9 + i, (i % 9) * 18 + xOffset, (i / 9) * 18 + yOffset));
    }

    for (var i = 0; i < 9; ++i) {
      this.addSlot(new Slot(playerInventory, i, i * 18 + xOffset, yOffset + 58));
    }

    if (entity != null) {
      this.blockPos.set(entity.getPos());
    }
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return true;
  }

  @Override
  public void onClosed(PlayerEntity player) {
    super.onClosed(player);
    if (!player.getWorld().isClient && this.entity != null) {
      this.entity.onScreenClosed();
    }
  }
}
