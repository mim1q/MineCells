package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SkeletonDecorationBlock extends Block {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private final boolean sitting;
  private final Block hangingBlock;

  public static final VoxelShape SHAPE = createCuboidShape(
    1.0D, 5.0D, 8.0D, 15.0D, 17.0D, 12.0D
  );

  public static final VoxelShape SITTING_SHAPE = createCuboidShape(
    1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 5.0D
  );

  public SkeletonDecorationBlock(Settings settings) {
    this(settings, false, null);
  }

  public SkeletonDecorationBlock(Settings settings, boolean sitting, Block hanging) {
    super(settings);
    this.sitting = sitting;
    this.hangingBlock = hanging;
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
    if (ctx.getSide() == Direction.DOWN) {
      if (ctx.getWorld().getBlockState(ctx.getBlockPos().add(ctx.getSide().getOpposite().getVector())).getBlock() instanceof ChainBlock) {
        return this.hangingBlock.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
      }
    } else if (ctx.getSide() == Direction.UP) {
      return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }
    return Blocks.AIR.getDefaultState();
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
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
    return ModelUtils.rotateShape(Direction.SOUTH, state.get(FACING), this.sitting ? SITTING_SHAPE : SHAPE);
  }
}
