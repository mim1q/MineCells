package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
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
    super.onStateReplaced(state, world, pos, newState, moved);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new SpawnerRuneBlockEntity(pos, state);
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
