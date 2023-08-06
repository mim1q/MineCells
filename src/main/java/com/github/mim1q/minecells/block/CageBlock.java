package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CageBlock extends Block {
  public static final BooleanProperty FLIPPED = BooleanProperty.of("flipped");
  private final boolean broken;

  private static final VoxelShape SIDES_SHAPE = VoxelShapes.union(
    Block.createCuboidShape(1, 0, 1, 15, 16, 2),
    Block.createCuboidShape(1, 0, 14, 15, 16, 15),
    Block.createCuboidShape(1, 0, 2, 2, 16, 14),
    Block.createCuboidShape(14, 0, 2, 15, 16, 14)
  );

  private static final VoxelShape BOTTOM_SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0, 0, 0, 16, 2, 16),
    SIDES_SHAPE
  );

  private static final VoxelShape TOP_SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0, 14, 0, 16, 16, 16),
    SIDES_SHAPE
  );

  public CageBlock(Settings settings, boolean broken) {
    super(settings);
    this.broken = broken;
    setDefaultState(getDefaultState().with(FLIPPED, false));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FLIPPED);
  }

  @Override
  public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    super.onBroken(world, pos, state);
    if (broken) {
      return;
    }
    BlockPos newPos = state.get(FLIPPED) ? pos.down() : pos.up();
    BlockState newState = world.getBlockState(newPos);
    if (newState.getBlock() instanceof CageBlock && newState.get(FLIPPED) != state.get(FLIPPED)) {
      world.breakBlock(newPos, true);
    }
  }

  @Override
  public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    if (!broken) {
      BlockPos newPos = state.get(FLIPPED) ? pos.down() : pos.up();
      world.setBlockState(newPos, state.with(FLIPPED, !state.get(FLIPPED)));
    }
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getPlayer() == null) {
      return getDefaultState();
    }
    boolean flipped = ctx.getSide() == Direction.DOWN;
    BlockPos newPos = flipped ? ctx.getBlockPos().down() : ctx.getBlockPos().up();
    BlockState secondState = ctx.getWorld().getBlockState(newPos);
    BlockState newState = getDefaultState().with(FLIPPED, flipped);
    if (broken || secondState.isAir()) {
      return newState;
    }
    return null;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return state.get(FLIPPED) ? TOP_SHAPE : BOTTOM_SHAPE;
  }

  @Override
  public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }
}
