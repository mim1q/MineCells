package com.github.mim1q.minecells.block.blockentity;

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
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public abstract class FillerBlock extends Block {

  private final Block targetBlock;
  protected static final int MAX_DEPTH = 7;
  private final boolean usable;

  public FillerBlock(Settings settings, Block targetBlock, boolean usable) {
    super(settings);
    this.targetBlock = targetBlock;
    this.usable = usable;
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (!this.usable) {
      return ActionResult.PASS;
    }
    if (world.isClient()) {
      return ActionResult.SUCCESS;
    }
    BlockPos targetPos = this.findTarget(world, pos,0);
    if (targetPos == null) {
      return ActionResult.FAIL;
    }
    BlockState targetState = world.getBlockState(targetPos);
    return targetBlock.onUse(targetState, world, targetPos, player, hand, hit);
  }

  @Override
  public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    if (world.isClient()) {
      return;
    }
    destroyNeighbors(world, pos, this, this.targetBlock);
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
      if (neighborState.getBlock() == this.targetBlock) {
        return mutable;
      }
      BlockPos newTargetPos = findTarget(world, mutable, depth + 1);
      if (newTargetPos != null) {
        return newTargetPos;
      }
    }

    return null;
  }

  public static void destroyNeighbors(WorldAccess world, BlockPos pos, Block filler, Block target) {
    for (BlockPos neighbor : getNeighbors(pos)) {
      Block block = world.getBlockState(neighbor).getBlock();
      if (block == filler || block == target) {
        world.breakBlock(neighbor, true);
        destroyNeighbors(world, neighbor, filler, target);
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
