package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.CellCrafterBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CellCrafterBlockEntity extends MineCellsBlockEntity implements NamedScreenHandlerFactory {
  public CellCrafterBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.CELL_CRAFTER, pos, state);
  }

  @Override
  public Text getDisplayName() {
    return Text.translatable(CellCrafterBlock.CELL_FORGE_TITLE_KEY);
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
    return new CellCrafterScreenHandler(syncId, playerInventory);
  }
}
