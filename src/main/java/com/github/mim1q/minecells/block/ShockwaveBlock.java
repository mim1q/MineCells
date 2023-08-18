package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ShockwaveBlock extends Block {
  public ShockwaveBlock(Settings settings) {
    super(settings);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    super.scheduledTick(state, world, pos, random);
    world.breakBlock(pos, false);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    var downState = world.getBlockState(pos.down());
    return world.getBlockState(pos).isReplaceable() && downState.isSideSolidFullSquare(world, pos.down(), Direction.UP);
  }

  @Override
  protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state) { }

  @Override
  public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
    super.afterBreak(world, player, pos, state, blockEntity, tool);
  }
}
