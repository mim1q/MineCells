package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PromenadeTreeTrunkPlacer extends StraightTrunkPlacer implements PromenadeTreeHelper {
  public static final Codec<PromenadeTreeTrunkPlacer> CODEC = RecordCodecBuilder.create(
    (instance) -> fillTrunkPlacerFields(instance).apply(instance, PromenadeTreeTrunkPlacer::new)
  );

  public PromenadeTreeTrunkPlacer(int baseHeight, int firstRandom, int secondRandom) {
    super(baseHeight, firstRandom, secondRandom);
  }

  @Override
  public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
    height = height + random.nextInt(10);
    List<FoliagePlacer.TreeNode> nodes = new ArrayList<>();
    boolean broken = random.nextFloat() < 0.1F;
    if (broken) {
      height = height * 2 / 3;
    }
    for (int i = 1; i < height; i++) {
      if (!world.testBlockState(startPos.up(i), AbstractBlockState::isReplaceable)) {
        return nodes;
      }
    }

    for (int i = 0; i < height; i++) {
      replacer.accept(startPos.up(i), TRUNK_BLOCK);
    }
    for (Direction dir : Properties.HORIZONTAL_FACING.getValues()) {
      // Generate trunk base
      BlockPos basePos = startPos.add(dir.getVector());
      int baseHeight = random.nextInt(5);
      if (baseHeight > 0) {
        placeRoot(world, replacer, basePos.down(), baseHeight);
      }

      if (random.nextFloat() < 0.25) {
        continue;
      }
      // Generate branch
      int h = random.nextBetween(6, height - 3);
      placeBranch(world, replacer, random, startPos.up(h), dir, !config.ignoreVines);
      int minH = h + 3;
      while (h < height - 8) {
        h = random.nextBetween(minH, height - 3);
        placeBranch(world, replacer, random, startPos.up(h), dir, !config.ignoreVines);
        if (!broken && h > height - 10) {
          generateLeaves(world, replacer, random, startPos.up(h).add(dir.getVector().multiply(3)), 3 + random.nextInt(2));
        }
        minH = h + 3;
      }
    }
    if (random.nextFloat() < 0.5F) {
      nodes.add(new FoliagePlacer.TreeNode(startPos.up(), 0, false));
    }
    if (!broken) {
      generateLeaves(world, replacer, random, startPos.up(height - 2), 5 + random.nextInt(3));
      if (random.nextFloat() > 0.8F) {
        generateLeaves(
          world,
          replacer,
          random,
          startPos.add(random.nextInt(4) - 2, height - 5 - random.nextInt(3), random.nextInt(4) - 2),
          4 + random.nextInt(4)
        );
      }
      if (random.nextFloat() > 0.5F) {
        generateLeaves(
          world,
          replacer,
          random,
          startPos.add(random.nextInt(2) - 1, height - 9 - random.nextInt(4), random.nextInt(2) - 1),
          2 + random.nextInt(3)
        );
      }

      var dirs = Direction.Type.HORIZONTAL.getShuffled(random);
      if (random.nextFloat() < 0.75F) {
        generateLongBranch(world, replacer, random, startPos.up(height - 6 - random.nextInt(10)), dirs.get(0), 4 + random.nextInt(2));
      }
      if (random.nextFloat() < 0.5F) {
        generateLongBranch(world, replacer, random, startPos.up(height - 8 - random.nextInt(10)), dirs.get(1), 3 + random.nextInt(1));
      }
    }
    return nodes;
  }

  public void generateLongBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, Direction dir, int length) {
    BlockPos pos = startPos;
    for (int i = 0; i < length; i++) {
      pos = pos.add(dir.getVector()).up();
      replacer.accept(pos, TRUNK_BLOCK);
    }
    generateLeaves(world, replacer, random, pos, 4 + random.nextInt(2));
  }

  public void generateLeaves(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, int radius) {
    for (int i = 1; i <= 3; i++) {
      if (radius > 5) {
        replacer.accept(startPos.east(i), TRUNK_BLOCK);
        replacer.accept(startPos.west(i), TRUNK_BLOCK);
        replacer.accept(startPos.north(i), TRUNK_BLOCK);
        replacer.accept(startPos.south(i), TRUNK_BLOCK);
      }
    }
    BlockStatePredicate isAir = BlockStatePredicate.forBlock(Blocks.AIR);
    for (int y = -2; y <= radius / 2; y++) {
      for (int x = -radius; x <= radius; x++) {
        for (int z = -radius; z <= radius; z++) {
          BlockPos pos = startPos.add(x, y, z);
          BlockPos distPos = startPos.add(x, y * 2, z);
          double distance = (distPos.getManhattanDistance(startPos) + Math.sqrt(distPos.getSquaredDistance(startPos))) / 2.0;
          if (distance >= radius) {
            continue;
          }
          if (distance >= radius - 1 && random.nextFloat() < 0.5F) {
            continue;
          }
          if (world.testBlockState(pos, isAir)) {
            replacer.accept(pos, MineCellsBlocks.RED_WILTED_LEAVES.leaves.getDefaultState());
          }
        }
      }
    }
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return MineCellsPlacerTypes.PROMENADE_TRUNK;
  }
}
