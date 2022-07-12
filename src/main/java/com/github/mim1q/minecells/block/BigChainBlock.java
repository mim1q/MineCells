package com.github.mim1q.minecells.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BigChainBlock extends ChainBlock {

  public BigChainBlock(Settings settings) {
    super(settings);
  }

  public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
    return switch (state.get(AXIS)) {
      case X -> createCuboidShape(0.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);
      case Z -> createCuboidShape(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 16.0D);
      default -> createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    };
  }
}
