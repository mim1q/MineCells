package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ReturnStoneBlockEntity extends BlockEntity {
  public ReturnStoneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.RETURN_STONE, pos, state);
  }
}
