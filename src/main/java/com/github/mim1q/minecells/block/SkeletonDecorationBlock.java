package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SkeletonDecorationBlock extends Block {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

  public static final VoxelShape SHAPE = VoxelShapes.union(
    VoxelShapes.cuboid(0.125D, 0.3125D, 0.3125D, 0.875D, 1.0625D, 0.4375D),
    VoxelShapes.cuboid(0.25D, 0.3125D, 0.25D, 0.75D, 1.0625D, 0.5D)
  );

  public SkeletonDecorationBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing());
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (neighborPos.equals(pos.up())) {
      if (neighborState.getBlock() == Blocks.CHAIN && neighborState.get(ChainBlock.AXIS) == Direction.Axis.Y) {
        return state;
      }
      return Blocks.AIR.getDefaultState();
    }
    return state;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), SHAPE);
  }
}
