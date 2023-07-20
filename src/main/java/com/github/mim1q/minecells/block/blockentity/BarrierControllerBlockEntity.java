package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.BarrierControllerBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BarrierControllerBlockEntity extends BlockEntity {
  private boolean wasOpen = false;
  public AnimationProperty openProgress = new AnimationProperty(0F, MathUtils::easeOutBounce);

  public BarrierControllerBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.BARRIER_CONTROLLER, pos, state);
  }

  public void tick(World world, BlockPos pos, BlockState state) {
    var open = state.get(BarrierControllerBlock.OPEN);
    if (world.isClient) {
      if (open != wasOpen) {
        wasOpen = open;
        openProgress.setupTransitionTo(
          open ? 1F : 0F,
          open ? 40F : 10F
        );
        world.playSound(
          pos.getX(), pos.getY(), pos.getZ(),
          open ? SoundEvents.BLOCK_WOODEN_DOOR_OPEN : SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,
          SoundCategory.BLOCKS,
          1.0F,
          1.0F,
          false
        );
      }
      return;
    }

    var block = state.getBlock();
    if (world.getTime() % 20 == 0 && block instanceof BarrierControllerBlock controller) {
      var shouldBeOpen = controller.shouldBeOpen(world, pos, state);
      world.setBlockState(pos, state.with(BarrierControllerBlock.OPEN, shouldBeOpen));
    }
  }
}
