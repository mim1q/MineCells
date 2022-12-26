package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class MetalBarsBlock extends Block {
  public static final VoxelShape CENTER_SHAPE = createCuboidShape(
    0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D
  );

  public static final VoxelShape SHAPE = createCuboidShape(
    0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 3.0D
  );

  public static final BooleanProperty CENTER = BooleanProperty.of("center");
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

  public MetalBarsBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING, CENTER);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide() == Direction.DOWN || ctx.getSide() == Direction.UP) {
      return this.getDefaultState().with(CENTER, true).with(FACING, ctx.getPlayerLookDirection());
    }
    return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), state.get(CENTER) ? CENTER_SHAPE : SHAPE);
  }
}
