package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetupBlockEntity extends BlockEntity {

  private boolean done = false;
  public SetupBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.SETUP_BLOCK_ENTITY, pos, state);
  }

  public static void tick(World world, BlockPos pos, BlockState state, SetupBlockEntity blockEntity) {
    if (blockEntity.done || world == null || world.isClient()) return;
    System.out.println(blockEntity.done);
    if (state.getBlock() instanceof SetupBlock setupBlock) {
      setupBlock.setup(world, pos, state);
      blockEntity.done = true;
    }
  }
}
