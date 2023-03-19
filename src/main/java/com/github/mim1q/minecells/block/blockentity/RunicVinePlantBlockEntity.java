package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.RunicVinePlantBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.github.mim1q.minecells.block.RunicVinePlantBlock.ACTIVATED;

public class RunicVinePlantBlockEntity extends BlockEntity {
  private static final String REQUIRED_MESSAGE = "block.minecells.runic_vine_plant.message";

  private int usedTicks = 0;
  private int blocksAbove = 0;
  public final AnimationProperty wobble = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public RunicVinePlantBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.RUNIC_VINE_PLANT, pos, state);
  }

  private void tick(World world, BlockPos pos, BlockState state) {
    if (world.isClient()) {
      usedTicks = Math.max(0, usedTicks - 1);
      if (usedTicks >= 18) {
        wobble.setupTransitionTo(1.0F, 2.0F);
      } else {
        wobble.setupTransitionTo(0.0F, 5.0F);
      }
      return;
    }
    if (blocksAbove > 0 && blocksAbove < 16 && world.getTime() % 2 == 0) {
      BlockPos posAbove = pos.up(blocksAbove);
      BlockState stateAbove = world.getBlockState(posAbove);
      BlockState stateBelow = world.getBlockState(posAbove.down());
      if (
        (stateAbove.isAir() || stateAbove.isOf(MineCellsBlocks.RUNIC_VINE_STONE))
        && (stateBelow.isOf(MineCellsBlocks.RUNIC_VINE_PLANT) || stateBelow.isOf(MineCellsBlocks.RUNIC_VINE))
      ) {
        world.playSound(null, posAbove, SoundEvents.BLOCK_WET_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        Vec3d particlePos = Vec3d.ofCenter(posAbove);
        ((ServerWorld) world).spawnParticles(RunicVinePlantBlock.PARTICLE, particlePos.getX(), particlePos.getY(), particlePos.getZ(), 10, 0.25D, 0.5D, 0.25D, 0.01D);
        world.breakBlock(posAbove, false);
        world.setBlockState(posAbove, MineCellsBlocks.RUNIC_VINE.getDefaultState());
        blocksAbove++;
      } else {
        blocksAbove = 0;
      }
    }
  }

  public static void tick(World world, BlockPos pos, BlockState state, RunicVinePlantBlockEntity blockEntity) {
    blockEntity.tick(world, pos, state);
  }

  public ActionResult use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
    usedTicks = 20;
    world.playSound(null, pos, SoundEvents.BLOCK_WET_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    if (state.get(ACTIVATED)) {
      return ActionResult.SUCCESS;
    }
    if (player.getStackInHand(hand).isOf(MineCellsItems.VINE_RUNE)) {
      player.getStackInHand(hand).damage(1, player, (user) -> user.sendToolBreakStatus(hand));
      blocksAbove = 1;
      world.setBlockState(pos, state.with(ACTIVATED, true));
    } else {
      player.sendMessage(Text.translatable(REQUIRED_MESSAGE), true);
    }
    return ActionResult.SUCCESS;
  }
}
