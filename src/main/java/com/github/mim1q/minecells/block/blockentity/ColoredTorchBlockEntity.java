package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ColoredTorchBlockEntity extends BlockEntity {
  public ColoredTorchBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.COLORED_TORCH_BLOCK_ENTITY, pos, state);
  }
}
