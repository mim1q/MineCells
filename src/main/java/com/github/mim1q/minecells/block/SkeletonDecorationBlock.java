package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SkeletonDecorationBlock extends Block {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private final boolean sitting;
  private final Block hangingBlock;

  public static final VoxelShape SHAPE = createCuboidShape(
    1.0D, 5.0D, 8.0D, 15.0D, 16.0D, 12.0D
  );

  public static final VoxelShape SITTING_SHAPE = createCuboidShape(
    1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 5.0D
  );

  public SkeletonDecorationBlock(Settings settings) {
    super(settings);
    this.sitting = false;
    this.hangingBlock = null;
  }

  public SkeletonDecorationBlock(Settings settings, Block hanging) {
    super(settings);
    this.sitting = true;
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
      if (this.hangingBlock != null && ctx.getWorld().getBlockState(ctx.getBlockPos().add(ctx.getSide().getOpposite().getVector())).getBlock() instanceof ChainBlock) {
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
    if (this.sitting) {
      return state;
    }
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

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    if (state.isOf(MineCellsBlocks.SKELETON) || state.isOf(MineCellsBlocks.HANGED_SKELETON)) {
      return;
    }

    float chance = 0.2F;
    if (state.isOf(MineCellsBlocks.ROTTING_CORPSE) || state.isOf(MineCellsBlocks.HANGED_ROTTING_CORPSE)) {
      chance = 0.6F;
    }
    if (random.nextFloat() <= chance) {
      Vec3d particlePos = Vec3d.ofCenter(pos).add(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
      double velY = random.nextFloat() * 0.05F;
      world.addParticle(MineCellsParticles.FLY, particlePos.x, particlePos.y, particlePos.z, 0.0D, velY, 0.0D);
    }
  }
}
