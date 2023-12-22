package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class ConditionalBarrierBlock extends HorizontalFacingBlock {
  public static final BooleanProperty OPEN = BooleanProperty.of("open");
  public static final VoxelShape SHAPE = createCuboidShape(0, 0, 6, 16, 16, 10);

  public ConditionalBarrierBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(OPEN, FACING);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return state.get(OPEN) ? VoxelShapes.empty() : ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), SHAPE);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return state.get(OPEN) || (context instanceof EntityShapeContext entityCtx && entityCtx.getEntity() instanceof SewersTentacleEntity)
      ? VoxelShapes.empty()
      : super.getOutlineShape(state, world, pos, context);
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (neighborState.getBlock() instanceof ConditionalBarrierBlock) {
      return state.with(OPEN, neighborState.get(OPEN));
    }
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }
}
