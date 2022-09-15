package com.github.mim1q.minecells.block.fluid;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsFluids;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class SewageFluid extends AbstractSewageFluid {

  @Override
  public Fluid getFlowing() {
    return MineCellsFluids.FLOWING_SEWAGE;
  }

  @Override
  public Fluid getStill() {
    return MineCellsFluids.STILL_SEWAGE;
  }

  @Override
  public Item getBucketItem() {
    return MineCellsItems.SEWAGE_BUCKET;
  }

  @Override
  protected BlockState toBlockState(FluidState state) {
    return MineCellsBlocks.SEWAGE.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
  }

  public static class Flowing extends SewageFluid {
    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
      super.appendProperties(builder);
      builder.add(LEVEL);
    }

    @Override
    public int getLevel(FluidState state) {
      return state.get(LEVEL);
    }

    @Override
    public boolean isStill(FluidState state) {
      return false;
    }
  }

  public static class Still extends SewageFluid {
    @Override
    public int getLevel(FluidState state) {
      return 8;
    }

    @Override
    public boolean isStill(FluidState state) {
      return true;
    }
  }
}
