package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.spawnerrune.SpawnerRuneBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SpawnerRuneBlock extends Block implements BlockEntityProvider {
  public SpawnerRuneBlock(Settings settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new SpawnerRuneBlockEntity(pos, state);
  }
}
