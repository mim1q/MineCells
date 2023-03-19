package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class RunicVineBlock extends Block {
  public static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
  public static final BooleanProperty TOP = BooleanProperty.of("top");

  public RunicVineBlock(Settings settings) {
    super(settings);
    setDefaultState(getDefaultState().with(TOP, true));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(TOP);
    super.appendProperties(builder);
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (neighborState.isOf(MineCellsBlocks.RUNIC_VINE) || neighborState.isOf(MineCellsBlocks.RUNIC_VINE_PLANT)) {
      return state.with(TOP, !(direction == Direction.UP));
    }
    if (direction == Direction.DOWN || (direction == Direction.UP && !state.get(TOP))) {
      return Blocks.AIR.getDefaultState();
    }
    return state;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return VoxelShapes.empty();
  }
}
