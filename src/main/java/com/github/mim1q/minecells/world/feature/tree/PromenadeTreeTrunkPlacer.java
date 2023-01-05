package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.block.CageBlock;
import com.github.mim1q.minecells.block.SkeletonDecorationBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class PromenadeTreeTrunkPlacer extends StraightTrunkPlacer {
  public static final BlockState TRUNK_BLOCK = MineCellsBlocks.PUTRID_LOG.getDefaultState();

  public static final Codec<PromenadeTreeTrunkPlacer> CODEC = RecordCodecBuilder.create(
    (instance) -> fillTrunkPlacerFields(instance).apply(instance, PromenadeTreeTrunkPlacer::new)
  );

  public PromenadeTreeTrunkPlacer(int baseHeight, int firstRandom, int secondRandom) {
    super(baseHeight, firstRandom, secondRandom);
  }

  @Override
  public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
    for (int i = -3; i < height; i++) {
      replacer.accept(startPos.up(i), TRUNK_BLOCK);
    }
    for (Direction dir : Properties.HORIZONTAL_FACING.getValues()) {
      // Generate trunk base
      BlockPos basePos = startPos.add(dir.getVector());
      int baseHeight = random.nextInt(3);
      if (baseHeight > 0) {
        for (int i = -3; i < baseHeight; i++) {
          replacer.accept(basePos.up(i), TRUNK_BLOCK);
        }
      }

      if (random.nextFloat() < 0.25) {
        continue;
      }
      // Genearate branch
      int h = random.nextBetween(6, height - 3);
      placeBranch(world, replacer, random, startPos.up(h), dir);
      if (h < height / 2) {
        h = random.nextBetween(height / 2, height - 3);
        placeBranch(world, replacer, random, startPos.up(h), dir);
      }
    }
    return ImmutableList.of();
  }

  public void placeBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos origin, Direction direction) {
    boolean big = random.nextFloat() < 0.75f;
    Vec3i offset = direction.getVector();
    origin = origin.add(offset);
    replacer.accept(origin, TRUNK_BLOCK);
    if (big) {
      origin = origin.add(offset).up();
      replacer.accept(origin, TRUNK_BLOCK);
    }
    if (random.nextFloat() < 0.33f) {
      Block chain = random.nextFloat() < 0.25F ? MineCellsBlocks.BIG_CHAIN : Blocks.CHAIN;
      int length = 3 + random.nextInt(3);
      if (canPlaceChain(world, origin.down(), length + 2)) {
        placeChain(replacer, random, origin.down(), length, chain.getDefaultState());
        if (big) {
          BlockPos pos = origin.add(direction.getOpposite().getVector());
          BlockState state = chain.getDefaultState().with(ChainBlock.AXIS, direction.getAxis());
          replacer.accept(pos, state);
        }
      }
    }
  }

  public boolean canPlaceChain(TestableWorld world, BlockPos origin, int length) {
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

  public void placeChain(BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos origin, int length, BlockState chain) {
    for (int i = 0; i < length; i++) {
      replacer.accept(origin.down(i), chain);
    }
    if (random.nextFloat() < 0.5F) {
      placeChainDecoration(replacer, random, origin.down(length));
    }
  }

  public void placeChainDecoration(BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos origin) {
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

  @Override
  protected TrunkPlacerType<?> getType() {
    return MineCellsPlacerTypes.PROMENADE_TRUNK_PLACER;
  }
}
