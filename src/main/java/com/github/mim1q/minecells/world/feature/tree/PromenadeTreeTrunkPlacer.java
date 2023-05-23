package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
      if (!world.testBlockState(startPos.up(i), state -> state.getMaterial().isReplaceable())) {
        return nodes;
      }
    }

    for (int i = -3; i < height; i++) {
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
      placeBranch(world, replacer, random, startPos.up(h), dir);
      int minH = h + 3;
      while (h < height - 8) {
        h = random.nextBetween(minH, height - 3);
        placeBranch(world, replacer, random, startPos.up(h), dir);
        if (!broken && h > height - 10) {
          generateLeaves(world, replacer, random, startPos.up(h + 2).add(dir.getVector().multiply(3)), 3);
        }
        minH = h + 3;
      }
    }
    if (random.nextFloat() < 0.5F) {
      nodes.add(new FoliagePlacer.TreeNode(startPos.up(), 0, false));
    }
    if (!broken) {
      int additionalRadius = random.nextInt(2);
      generateLeaves(world, replacer, random, startPos.up(height), 3 + additionalRadius);
      generateLeaves(world, replacer, random, startPos.up(height - 6), 3 + additionalRadius);
      generateLeaves(world, replacer, random, startPos.up(height - 10), 1 + additionalRadius);

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
    generateLeaves(world, replacer, random, pos.up(2), 4);
  }

  public void generateLeaves(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, int radius) {
    BlockStatePredicate isAir = BlockStatePredicate.forBlock(Blocks.AIR);
    for (int y = -radius; y <= radius; y++) {
      for (int x = -radius; x <= radius; x++) {
        for (int z = -radius; z <= radius; z++) {
          BlockPos pos = startPos.add(x, y, z);
          double distance = pos.getSquaredDistance(startPos);
          if (distance > radius * radius) {
            continue;
          }
          double chance = (distance) / (radius * radius);
          if (random.nextFloat() < chance) {
            continue;
          }
          if (world.testBlockState(pos, isAir)) {
            replacer.accept(pos, MineCellsBlocks.RED_WILTED_LEAVES.leaves.getDefaultState().with(Properties.PERSISTENT, true));
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
