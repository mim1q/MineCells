package com.github.mim1q.minecells.block.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public abstract class FillerBlock extends Block {

  private final Block targetBlock;
  protected final int MAX_DEPTH = 7;
  private final Set<BlockPos> checkedBlocks = new HashSet<>();
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
    this.checkedBlocks.clear();
    this.useNeighborsIfApplicable(state, world, pos, player, hand, hit, 0);
    return ActionResult.SUCCESS;
  }

  @Override
  public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    if (world.isClient()) {
      return;
    }
    destroyNeighbors(world, pos, this, this.targetBlock);
  }

  private void useNeighborsIfApplicable(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, int depth) {
    if (depth >= this.MAX_DEPTH || this.checkedBlocks.contains(pos)) {
      return;
    }
    this.checkedBlocks.add(pos);
    Block block = world.getBlockState(pos).getBlock();
    if (block == this.targetBlock) {
      this.targetBlock.onUse(state, world, pos, player, hand, hit);
      return;
    }
    if (block == this) {
      for (BlockPos neighbor : getNeighbors(pos)) {
        this.useNeighborsIfApplicable(state, world, neighbor, player, hand, hit, depth + 1);
      }
    }
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
