package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.block.*;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.TestableWorld;

import java.util.List;
import java.util.function.BiConsumer;

public interface PromenadeTreeHelper {
  BlockState TRUNK_BLOCK = MineCellsBlocks.PUTRID_WOOD.log.getDefaultState();

  default void placeBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos origin, Direction direction, boolean decorations) {
    if (decorations && random.nextFloat() < 0.025F) {
      placeFlag(replacer, origin, direction);
      return;
    }
    boolean big = random.nextFloat() < 0.75f;
    Vec3i offset = direction.getVector();
    origin = origin.add(offset);
    replacer.accept(origin, TRUNK_BLOCK);
    if (big) {
      origin = origin.add(offset).up();
      replacer.accept(origin, TRUNK_BLOCK);
    }
    if (random.nextFloat() < 0.5F) {
      int length = 3 + random.nextInt(8);
      if (decorations && canPlaceChain(world, origin.down(), length + 2)) {
        Block chain = random.nextFloat() < 0.33F ? MineCellsBlocks.BIG_CHAIN : Blocks.CHAIN;
        placeChain(replacer, random, origin.down(), length, chain.getDefaultState());
        if (big) {
          BlockPos pos = origin.add(direction.getOpposite().getVector());
          BlockState state = chain.getDefaultState().with(ChainBlock.AXIS, direction.getAxis());
          replacer.accept(pos, state);
        }
      }
    }
  }

  default void placeRoot(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, BlockPos origin, int height) {
    origin = origin.up(height);
    for (int i = height; i > -3; i--) {
      if (!world.testBlockState(origin, state -> state.isIn(MineCellsBlockTags.TREE_ROOT_REPLACEABLE))) {
        continue;
      }
      BlockPos[] positions = { origin.north(), origin.south(), origin.east(), origin.west() };
      boolean shouldPlace = false;
      for (BlockPos pos : positions) {
        if (world.testBlockState(pos, state -> state.getCollisionShape((BlockView) world, pos).isEmpty())) {
          shouldPlace = true;
        }
      }
      if (!shouldPlace) {
        return;
      }
      replacer.accept(origin, TRUNK_BLOCK);
      origin = origin.down();
    }
  }

  default void placeFlag(BiConsumer<BlockPos, BlockState> replacer, BlockPos origin, Direction direction) {
    var pole = MineCellsBlocks.FLAG_POLE.getDefaultState().with(FlagPoleBlock.FACING, direction);
    var offset = direction.getVector();
    origin = origin.add(offset);
    replacer.accept(origin, pole.with(FlagPoleBlock.CONNECTING, true));
    origin = origin.add(offset);
    replacer.accept(origin, pole.with(FlagPoleBlock.CONNECTING, false));
    replacer.accept(origin.down(), MineCellsBlocks.BIOME_BANNER.getDefaultState()
      .with(BiomeBannerBlock.PATTERN, BiomeBannerBlock.BannerPattern.PROMENADE)
      .with(BiomeBannerBlock.FACING, direction.rotateYClockwise())
      .with(BiomeBannerBlock.CENTERED, true)
    );
  }

  default boolean canPlaceChain(TestableWorld world, BlockPos origin, int length) {
    for (int i = 0; i < length; i++) {
      boolean obstructed = world.testBlockState(
        origin.down(i),
        (state) -> !state.isAir()
      );
      if (obstructed) {
        return false;
      }
    }
    return true;
  }

  default void placeChain(BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos origin, int length, BlockState chain) {
    for (int i = 0; i < length; i++) {
      replacer.accept(origin.down(i), chain);
    }
    if (random.nextFloat() < 0.5F) {
      placeChainDecoration(replacer, random, origin.down(length));
    }
  }

  default void placeChainDecoration(BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos origin) {
    if (random.nextFloat() < 0.25F) {
      // Place corpse
      List<Block> corpses = List.of(MineCellsBlocks.HANGED_CORPSE, MineCellsBlocks.HANGED_SKELETON, MineCellsBlocks.HANGED_ROTTING_CORPSE);
      Block corpse = corpses.get(random.nextInt(3));
      Direction direction = Properties.HORIZONTAL_FACING.getValues().stream().toList().get(random.nextInt(4));
      replacer.accept(origin, corpse.getDefaultState().with(SkeletonDecorationBlock.FACING, direction));
      return;
    }
    // Place cage
    boolean broken = random.nextBoolean();
    if (broken) {
      replacer.accept(origin, MineCellsBlocks.BROKEN_CAGE.getDefaultState().with(CageBlock.FLIPPED, true));
      return;
    }
    replacer.accept(origin, MineCellsBlocks.CAGE.getDefaultState().with(CageBlock.FLIPPED, true));
    replacer.accept(origin.down(), MineCellsBlocks.CAGE.getDefaultState());
  }
}
