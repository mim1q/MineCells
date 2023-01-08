package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class DecorativeStatueBlock extends Block implements BlockEntityProvider {
  public DecorativeStatueBlock(Settings settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new DecorativeStatueBlockEntity(pos, state);
  }
}
