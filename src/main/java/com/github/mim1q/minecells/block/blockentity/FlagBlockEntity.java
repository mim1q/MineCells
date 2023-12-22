package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class FlagBlockEntity extends MineCellsBlockEntity {
  public FlagBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.FLAG_BLOCK_ENTITY, pos, state);
  }
}
