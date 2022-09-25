package com.github.mim1q.minecells.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class CageBlock extends Block {

  public static final BooleanProperty BROKEN = BooleanProperty.of("broken");
  public static final BooleanProperty FLIPPED = BooleanProperty.of("flipped");

  public CageBlock() {
    super(FabricBlockSettings.copyOf(Blocks.CHAIN).hardness(1.0F));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(BROKEN, FLIPPED);
  }

  @Override
  public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    super.onBroken(world, pos, state);
    if (state.get(BROKEN)) {
      return;
    }
    BlockPos newPos = state.get(FLIPPED) ? pos.down() : pos.up();
    BlockState newState = world.getBlockState(newPos);
    if (newState.getBlock() instanceof CageBlock && newState.get(FLIPPED) != state.get(FLIPPED)) {
      world.breakBlock(newPos, false);
    }
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }
}
