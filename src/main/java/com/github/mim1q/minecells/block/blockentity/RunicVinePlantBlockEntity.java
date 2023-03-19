package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RunicVinePlantBlockEntity extends BlockEntity {
  private int usedTicks = 0;
  private int blocksAbove = 0;
  public AnimationProperty wobble = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public RunicVinePlantBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.RUNIC_VINE_PLANT, pos, state);
  }

  private void tick(World world, BlockPos pos, BlockState state) {
    if (world.isClient()) {
      usedTicks = Math.max(0, usedTicks - 1);
      if (usedTicks >= 18) {
        wobble.setupTransitionTo(1.0F, 2.0F);
      } else {
        wobble.setupTransitionTo(0.0F, 5.0F);
      }
      return;
    }
    if (blocksAbove > 0 && blocksAbove < 16 && world.getTime() % 2 == 0) {
      BlockPos posAbove = pos.up(blocksAbove);
      BlockState stateAbove = world.getBlockState(posAbove);
      if (stateAbove.isAir() || stateAbove.isOf(MineCellsBlocks.HARDSTONE)) {
        world.playSound(null, posAbove, SoundEvents.BLOCK_WET_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.setBlockState(posAbove, MineCellsBlocks.RUNIC_VINE.getDefaultState());
        blocksAbove++;
      } else {
        blocksAbove = 0;
      }
    }
  }

  public static void tick(World world, BlockPos pos, BlockState state, RunicVinePlantBlockEntity blockEntity) {
    blockEntity.tick(world, pos, state);
  }

  public void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
    usedTicks = 20;
    blocksAbove = 1;
  }
}
