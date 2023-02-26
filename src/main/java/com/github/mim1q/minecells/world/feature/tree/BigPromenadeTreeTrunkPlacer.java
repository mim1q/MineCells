package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class BigPromenadeTreeTrunkPlacer extends StraightTrunkPlacer implements PromenadeTreeHelper {
  public static final Codec<BigPromenadeTreeTrunkPlacer> CODEC = RecordCodecBuilder.create(
    (instance) -> fillTrunkPlacerFields(instance).apply(instance, BigPromenadeTreeTrunkPlacer::new)
  );

  public BigPromenadeTreeTrunkPlacer(int baseHeight, int firstRandom, int secondRandom) {
    super(baseHeight, firstRandom, secondRandom);
  }

  @Override
  public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
    generateColumn(world, replacer, random, height, startPos, Direction.NORTH);
    generateColumn(world, replacer, random, height, startPos.add(1, 0, 0), Direction.EAST);
    generateColumn(world, replacer, random, height, startPos.add(1, 0, 1), Direction.SOUTH);
    generateColumn(world, replacer, random, height, startPos.add(0, 0, 1), Direction.WEST);
    if (random.nextFloat() < 0.5F) {
      return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(), 0, true));
    }
    return ImmutableList.of();
  }

  public void generateColumn(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos origin, Direction direction) {
    int additionalHeight = random.nextInt(16);
    for (int i = -3; i < height + additionalHeight; i++) {
      replacer.accept(origin.up(i), TRUNK_BLOCK);
    }
    generateTreeSide(world, replacer, random, height + additionalHeight, origin, direction);
    generateTreeSide(world, replacer, random, height + additionalHeight, origin, direction.rotateYCounterclockwise());
  }

  public void generateTreeSide(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos origin, Direction direction) {
    // Generate root
    int rootHeight = 1 + random.nextInt(10);
    for (int i = -4; i < rootHeight; i++) {
      replacer.accept(origin.add(direction.getVector()).up(i), TRUNK_BLOCK);
    }
    // Genearate branches
    if (random.nextFloat() < 0.25) {
      return;
    }
    int h = random.nextBetween(8, height - 3);
    placeBranch(world, replacer, random, origin.up(h), direction);
    if (h < height / 3) {
      h = random.nextBetween(height / 3, height - 3);
      placeBranch(world, replacer, random, origin.up(h), direction);
    }
    if (h < height * 2 / 3) {
      h = random.nextBetween(height * 2 / 3, height - 3);
      placeBranch(world, replacer, random, origin.up(h), direction);
    }
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return MineCellsPlacerTypes.PROMENADE_BIG_TRUNK;
  }
}