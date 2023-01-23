package com.github.mim1q.minecells.block.setupblocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeamPlacerBlock extends SetupBlock {
  public BeamPlacerBlock(Settings settings) {
    super(settings);
  }

  @Override
  public boolean setup(World world, BlockPos pos, BlockState state) {
    BlockState stateAbove = world.getBlockState(pos.up());
    for (int i = 0; i < 32; i++) {
      if (!tryPlace(world, pos.down(i), stateAbove)) {
        break;
      }
    }
    return true;
  }

  protected boolean tryPlace(World world, BlockPos pos, BlockState state) {
    BlockPos[] neighbors = { pos.south(), pos.east(), pos.north(), pos.west() };
    for (BlockPos neighbor : neighbors) {
      if (!world.getBlockState(neighbor).isSolidBlock(world, pos)) {
        world.setBlockState(pos, state);
        return true;
      }
    }
    return false;
  }
}
