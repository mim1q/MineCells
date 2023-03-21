package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.setupblocks.SetupBlock;
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
    if (world.getTime() % 20 == 0 && state.getBlock() instanceof SetupBlock setupBlock) {
      blockEntity.done = setupBlock.setup(world, pos, state);
    }
  }

  @Override
  public void markDirty() {
    if (this.world != null) {
      world.markDirty(this.getPos());
    }
  }
}
