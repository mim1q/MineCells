package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class DecorativeStatueBlockEntity extends MineCellsBlockEntity {
  public DecorativeStatueBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.DECORATIVE_STATUE_BLOCK_ENTITY, pos, state);
  }
}
