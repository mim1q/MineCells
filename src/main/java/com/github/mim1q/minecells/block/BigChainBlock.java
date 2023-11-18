package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class BigChainBlock extends ChainBlock {

  public static BooleanProperty HANGING = BooleanProperty.of("hanging");

  public BigChainBlock(Settings settings) {
    super(settings);
    setDefaultState(getDefaultState().with(HANGING, false));
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    return getHangingState(super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos), world, pos);
  }

  protected BlockState getHangingState(BlockState state, WorldAccess world, BlockPos pos) {
    BlockState stateBelow = world.getBlockState(pos.down());
    boolean chainBelow = stateBelow.getBlock() instanceof BigChainBlock && stateBelow.get(AXIS) == Axis.Y;
    boolean cageBelow = stateBelow.getBlock() instanceof CageBlock && stateBelow.get(CageBlock.FLIPPED);
    boolean vertical = state.get(AXIS) == Axis.Y;
    boolean solidBelow = stateBelow.isSideSolidFullSquare(world, pos.down(), Direction.UP);
    boolean hanging = vertical && !(chainBelow || solidBelow || cageBelow);
    return state.with(HANGING, hanging);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    BlockState state = super.getPlacementState(ctx);
    if (state == null) {
      return null;
    }
    return getHangingState(state, ctx.getWorld(), ctx.getBlockPos());
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(HANGING);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return state.get(AXIS) == Direction.Axis.Y
      ? VoxelShapes.empty()
      : super.getCollisionShape(state, world, pos, context);
  }

  public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
    return switch (state.get(AXIS)) {
      case X -> createCuboidShape(0.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);
      case Z -> createCuboidShape(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 16.0D);
      default -> createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    };
  }
}
