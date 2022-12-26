package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.ColoredTorchBlockEntity;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class ColoredTorchBlock extends BlockWithEntity {
  public static final VoxelShape SHAPE = VoxelShapes.union(
    createCuboidShape(7.0D, 3.0D, 1.0D, 9.0D, 11.0D, 3.0D),
    createCuboidShape(6.0D, 11.0D, 0.0D, 10.0D, 14.0D, 4.0D)
  );
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private final String flameType;

  public ColoredTorchBlock(Settings settings, String flameType) {
    super(settings);
    this.flameType = flameType;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide() == Direction.UP || ctx.getSide() == Direction.DOWN) {
      return null;
    }
    return getDefaultState().with(FACING, ctx.getSide());
  }

  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new ColoredTorchBlockEntity(pos, state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.SOUTH, state.get(FACING), SHAPE);
  }

  public String getFlameType() {
    return this.flameType;
  }
}
