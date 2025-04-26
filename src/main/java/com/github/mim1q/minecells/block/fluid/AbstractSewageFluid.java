package com.github.mim1q.minecells.block.fluid;

import com.github.mim1q.minecells.registry.MineCellsFluids;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
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

  @Override
  protected void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
    super.randomDisplayTick(world, pos, state, random);

    if (!world.getFluidState(pos.up()).isEmpty()) return;
    if (random.nextFloat() > 0.05) return;
    int color = state.isOf(MineCellsFluids.FLOWING_ANCIENT_SEWAGE) || state.isOf(MineCellsFluids.STILL_ANCIENT_SEWAGE)
      ? 0xebc331
      : 0x68dc47;
    world.addParticle(
      MineCellsParticles.RISING_BUBBLE.get(color),
      pos.getX() + random.nextDouble(),
      pos.getY() + state.getLevel() * 0.125 - random.nextDouble() * 0.2,
      pos.getZ() + random.nextDouble(),
      0.0, 0.05 + random.nextDouble() * 0.02, 0.0
    );
  }
}
