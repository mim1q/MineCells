package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class WallLeavesBlock extends Block {
  public static final DirectionProperty DIRECTION = Properties.FACING;
  public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 12, 16, 16, 16);
  public static final VoxelShape SHAPE_TOP = Block.createCuboidShape(0, 12, 0, 16, 16, 16);
  public static final VoxelShape SHAPE_BOTTOM = Block.createCuboidShape(0, 0, 0, 16, 4, 16);

  public WallLeavesBlock(Settings settings) {
    super(settings.offsetType(blockState -> {
        Direction dir = blockState.get(DIRECTION);
        if (dir == Direction.DOWN) {
          return OffsetType.XZ;
        }
        return OffsetType.XYZ;
      })
    );
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(DIRECTION);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    Direction dir = state.get(DIRECTION);
    if (dir == Direction.DOWN) {
      return SHAPE_TOP;
    }
    if (dir == Direction.UP) {
      return SHAPE_BOTTOM;
    }
    return ModelUtils.rotateShape(Direction.NORTH, dir, SHAPE);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    BlockPos pos = ctx.getBlockPos().add(ctx.getSide().getOpposite().getVector());
    BlockState state = ctx.getWorld().getBlockState(pos);
    if (!(state.isSideSolidFullSquare(ctx.getWorld(), pos, ctx.getSide()) || state.isIn(BlockTags.LEAVES))) {
      return null;
    }
    return this.getDefaultState().with(DIRECTION, ctx.getSide());
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (direction == state.get(DIRECTION).getOpposite()) {
      boolean stay = neighborState.getBlock() instanceof LeavesBlock || neighborState.isSideSolidFullSquare(world, neighborPos, direction);
      return stay ? state : Blocks.AIR.getDefaultState();
    }
    return state;
  }

  @Override
  public float getMaxHorizontalModelOffset() {
    return 0.125F;
  }
}
