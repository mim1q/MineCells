package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpawnerRuneBlock extends BlockWithEntity {
  public SpawnerRuneBlock(Settings settings) {
    super(settings);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    if (!newState.isAir() && !world.isClient() && state.hasBlockEntity()) {
      var blockEntity = world.getBlockEntity(pos, MineCellsBlockEntities.SPAWNER_RUNE);
      var entity = MineCellsEntities.SPAWNER_RUNE.create(world);
      if (entity != null && blockEntity.isPresent()) {
        entity.setPosition(Vec3d.ofBottomCenter(pos));
        world.spawnEntity(entity);
        entity.controller.setDataId(world, pos, blockEntity.get().controller.getDataId());
      }
    }
    super.onStateReplaced(state, world, pos, newState, moved);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean canReplace(BlockState state, ItemPlacementContext context) {
    return true;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new SpawnerRuneBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return checkType(
      type,
      MineCellsBlockEntities.SPAWNER_RUNE,
      (entityWorld, pos, entityState, entity) -> entity.tick(entityWorld, pos, entityState)
    );
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    if (
      context instanceof EntityShapeContext entityCtx
      && entityCtx.getEntity() instanceof PlayerEntity player
      && !player.isCreative()
    ) {
      return VoxelShapes.empty();
    }
    return super.getOutlineShape(state, world, pos, context);
  }
}
