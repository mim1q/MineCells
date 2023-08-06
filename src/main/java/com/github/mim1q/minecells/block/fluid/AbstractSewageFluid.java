package com.github.mim1q.minecells.block.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AbstractSewageFluid extends FlowableFluid {

  @Override
  public boolean matchesType(Fluid fluid) {
    return fluid == this.getStill() || fluid == this.getFlowing();
  }

  @Override
  protected boolean isInfinite(World world) {
    return false;
  }

  @Override
  protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
    final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
    Block.dropStacks(state, world, pos, blockEntity);
  }

  protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
    return false;
  }

  @Override
  protected int getFlowSpeed(WorldView worldView) {
    return 4;
  }

  @Override
  protected int getLevelDecreasePerBlock(WorldView worldView) {
    return 1;
  }

  @Override
  public int getTickRate(WorldView worldView) {
    return 5;
  }

  @Override
  protected float getBlastResistance() {
    return 100.0F;
  }
}
