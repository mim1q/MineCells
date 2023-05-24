package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class TeleporterBlockEntity extends BlockEntity {
  public TeleporterBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.TELEPORTER, pos, state);
  }
}
