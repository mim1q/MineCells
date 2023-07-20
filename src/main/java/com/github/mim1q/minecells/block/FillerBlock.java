package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.function.Predicate;

@SuppressWarnings("deprecation")
public abstract class FillerBlock extends Block {

  private final Predicate<Block> targetBlockPredicate;
  protected static final int MAX_DEPTH = 7;
  private final boolean usable;

  public FillerBlock(Settings settings, Predicate<Block> targetBlockPredicate, boolean usable) {
    super(settings);
    this.targetBlockPredicate = targetBlockPredicate;
    this.usable = usable;
  }

  public FillerBlock(Settings settings, Block targetBlock, boolean usable) {
    super(settings);
    this.targetBlockPredicate = (block) -> block == targetBlock;
    this.usable = usable;
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (!this.usable) {
      return ActionResult.PASS;
    }
    BlockPos targetPos = this.findTarget(world, pos,0);
    if (targetPos == null) {
      return ActionResult.FAIL;
    }
    BlockState targetState = world.getBlockState(targetPos);
    return targetState.getBlock().onUse(targetState, world, targetPos, player, hand, hit);
  }

  @Override
  public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    if (world.isClient()) {
      return;
    }
    destroyNeighbors(world, pos, this, this.targetBlockPredicate);
  }

  private BlockPos findTarget(World world, BlockPos pos, int depth) {
    if (depth >= MAX_DEPTH) {
      return null;
    }
    BlockPos.Mutable mutable = new BlockPos.Mutable();
    Direction[] dirs = Direction.values();
    for (Direction direction : dirs) {
      mutable.set(pos, direction);
      BlockState neighborState = world.getBlockState(mutable);
      if (targetBlockPredicate.test(neighborState.getBlock())) {
        return mutable;
      }
      BlockPos newTargetPos = findTarget(world, mutable, depth + 1);
      if (newTargetPos != null) {
        return newTargetPos;
      }
    }
    return null;
  }

  public static void destroyNeighbors(WorldAccess world, BlockPos pos, Block filler, Predicate<Block> targetPredicate) {
    for (BlockPos neighbor : getNeighbors(pos)) {
      Block block = world.getBlockState(neighbor).getBlock();
      if (block == filler || targetPredicate.test(block)) {
        world.breakBlock(neighbor, true);
        destroyNeighbors(world, neighbor, filler, targetPredicate);
      }
    }
  }

  private static ArrayList<BlockPos> getNeighbors(BlockPos pos) {
    ArrayList<BlockPos> result = new ArrayList<>();
    result.add(pos.up());
    result.add(pos.down());
    result.add(pos.north());
    result.add(pos.south());
    result.add(pos.east());
    result.add(pos.west());
    return result;
  }
}
