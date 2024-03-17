package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.github.mim1q.minecells.world.feature.tree.PromenadeFoliagePlacer.PromenadeLeafNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer.TreeNode;
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
  public List<TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
    height = height + random.nextInt(10);
    List<TreeNode> nodes = new ArrayList<>();
    boolean broken = random.nextFloat() < 0.1F;

    var leafBlock = config.foliageProvider.get(random, startPos.up(height));

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
          nodes.add(new PromenadeLeafNode(startPos.up(h + 1).add(dir.getVector().multiply(3)), 2, true, leafBlock));
        }
        minH = h + 3;
      }
    }
    if (!broken) {
      nodes.add(new PromenadeLeafNode(startPos.up(height), 2, true, leafBlock));


      var dirs = Direction.Type.HORIZONTAL.getShuffled(random);
      if (random.nextFloat() < 0.75F) {
        var branchHeight = 3 + random.nextInt(2);
        var pos = startPos.up(height - 4 - random.nextInt(10));
        generateLongBranch(world, replacer, random, pos, dirs.get(0), branchHeight);
        nodes.add(new PromenadeLeafNode(pos.add(dirs.get(0).getVector().multiply(branchHeight).up(branchHeight + 1)), 2, true, leafBlock));
      }
      if (random.nextFloat() < 0.5F) {
        var branchHeight = 3 + random.nextInt(2);
        var pos = startPos.up(height - 4 - random.nextInt(10));
        generateLongBranch(world, replacer, random, pos, dirs.get(1), branchHeight);
        nodes.add(new PromenadeLeafNode(pos.add(dirs.get(1).getVector().multiply(branchHeight).up(branchHeight + 1)), 2, true, leafBlock));
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
    replacer.accept(pos.up(2), TRUNK_BLOCK);
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return MineCellsPlacerTypes.PROMENADE_TRUNK;
  }
}
