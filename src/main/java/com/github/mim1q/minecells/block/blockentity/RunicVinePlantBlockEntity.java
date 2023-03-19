package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RunicVinePlantBlockEntity extends BlockEntity {
  public int usedTicks = 0;
  public AnimationProperty wobble = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public RunicVinePlantBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.RUNIC_VINE_PLANT, pos, state);
  }

  public static void tick(World world, BlockPos pos, BlockState state, RunicVinePlantBlockEntity blockEntity) {
    blockEntity.usedTicks = Math.max(0, blockEntity.usedTicks - 1);
    if (blockEntity.usedTicks >= 18) {
      blockEntity.wobble.setupTransitionTo(1.0F, 2.0F);
    } else {
      blockEntity.wobble.setupTransitionTo(0.0F, 5.0F);
    }
  }
}
