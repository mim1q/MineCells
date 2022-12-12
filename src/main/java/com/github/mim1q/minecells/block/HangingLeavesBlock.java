package com.github.mim1q.minecells.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class HangingLeavesBlock extends HorizontalFacingBlock {
  public static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 6.0D, 2.0D, 14.0D, 16.0D, 14.0D);

  public HangingLeavesBlock(Settings settings) {
    super(settings.offsetType(Block.OffsetType.XZ));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(HorizontalFacingBlock.FACING);
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (neighborPos.equals(pos.up())) {
      boolean stay = neighborState.getBlock() instanceof LeavesBlock || neighborState.isSideSolidFullSquare(world, neighborPos, Direction.DOWN);
      return stay ? state : Blocks.AIR.getDefaultState();
    }
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide() != Direction.DOWN) {
      return null;
    }
    return this.getDefaultState().with(
      HorizontalFacingBlock.FACING,
      ctx.getPlayerFacing()
    );
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }
}
