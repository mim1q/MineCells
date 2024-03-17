package com.github.mim1q.minecells.block.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MineCellsBlockEntity extends BlockEntity {
  public MineCellsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void markDirty() {
    if (getWorld() != null) {
      getWorld().markDirty(this.getPos());
    }
  }

  protected void sync() {
    if (world != null) {
      world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }
  }
}
