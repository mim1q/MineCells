package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RunicVinePlantBlockEntity extends BlockEntity {
  public RunicVinePlantBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.RUNIC_VINE_PLANT, pos, state);
  }
}
