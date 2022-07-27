package com.github.mim1q.minecells.block.fluid;

import com.github.mim1q.minecells.registry.BlockRegistry;
import com.github.mim1q.minecells.registry.FluidRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class AncientSewageFluid extends AbstractSewageFluid {
  @Override
  public Fluid getFlowing() {
    return FluidRegistry.FLOWING_ANCIENT_SEWAGE;
  }

  @Override
  public Fluid getStill() {
    return FluidRegistry.STILL_ANCIENT_SEWAGE;
  }

  @Override
  public Item getBucketItem() {
    return null;
  }

  @Override
  protected BlockState toBlockState(FluidState state) {
    return BlockRegistry.ANCIENT_SEWAGE.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
  }

  public static class Flowing extends AncientSewageFluid {
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

  public static class Still extends AncientSewageFluid {
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
