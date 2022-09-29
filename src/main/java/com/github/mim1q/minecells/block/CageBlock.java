package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CageBlock extends Block {
  public static final BooleanProperty FLIPPED = BooleanProperty.of("flipped");
  private final boolean broken;

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
      world.breakBlock(newPos, false);
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
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }
}
