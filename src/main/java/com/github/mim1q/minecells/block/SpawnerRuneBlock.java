package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.spawnerrune.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpawnerRuneBlock extends BlockWithEntity implements BlockEntityProvider {
  public SpawnerRuneBlock(Settings settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new SpawnerRuneBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return world.isClient ? null : checkType(type, MineCellsBlockEntities.SPAWNER_RUNE_BLOCK_ENTITY, SpawnerRuneBlockEntity::tick);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
}
