package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BiomeBannerBlockEntity extends MineCellsBlockEntity {
  public BiomeBannerBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.BIOME_BANNER_BLOCK_ENTITY, pos, state);
  }
}
