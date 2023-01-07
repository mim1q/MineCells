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

public class FlagPoleBlock extends Block {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final BooleanProperty CONNECTING = BooleanProperty.of("connecting");

  public static final VoxelShape SHAPE = Block.createCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 4.0D, 16.0D);

  public FlagPoleBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.getDefaultState().with(FACING, Direction.SOUTH).with(CONNECTING, false));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING, CONNECTING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide().getAxis().isVertical()) {
      return null;
    }
    var connecting = false;
    var state = ctx.getWorld().getBlockState(ctx.getBlockPos().add(ctx.getSide().getOpposite().getVector()));
    if (state.isSideSolidFullSquare(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide().getOpposite())) {
      connecting = true;
    }
    return getDefaultState().with(FACING, ctx.getSide()).with(CONNECTING, connecting);
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), SHAPE);
  }
}
