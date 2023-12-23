package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

import java.util.Set;

public class PromenadeFoliagePlacer extends FoliagePlacer {
  private static final Set<BlockPos> OFFSETS = Set.of(
    new BlockPos(0, 0, 0),
    new BlockPos(1, 0, 0),
    new BlockPos(-1, 0, 0),
    new BlockPos(0, 0, 1),
    new BlockPos(0, 0, -1)
  );

  public static final Codec<PromenadeFoliagePlacer> CODEC = RecordCodecBuilder.create(
    (instance) -> fillFoliagePlacerFields(instance).apply(instance, PromenadeFoliagePlacer::new)
  );

  public PromenadeFoliagePlacer(IntProvider radius, IntProvider offset) {
    super(radius, offset);
  }

  @Override
  protected FoliagePlacerType<?> getType() {
    return MineCellsPlacerTypes.PROMENADE_FOLIAGE_PLACER;
  }

  @Override
  protected void generate(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
    var blockPos = treeNode.getCenter().up(offset);
    var bl = treeNode.isGiantTrunk();

    if (random.nextBoolean()) {
      this.generateSquare(world, placer, random, config, blockPos, radius, -2, bl);
    }
    this.generateSquare(world, placer, random, config, blockPos, radius + 2, -1, bl);
    this.generateSquare(world, placer, random, config, blockPos, radius + 3, 0, bl);
    this.generateSquare(world, placer, random, config, blockPos, radius + 2, 1, bl);
    if (random.nextBoolean()) {
      this.generateSquare(world, placer, random, config, blockPos, radius, 2, bl);
    }

    var trunk = config.trunkProvider.get(random, blockPos);
    var mutablePos = new BlockPos.Mutable();
    for (var offsetPos : OFFSETS) {
      mutablePos.set(blockPos, offsetPos);
      placer.placeBlock(mutablePos, trunk);
    }
  }

  @Override
  public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
    return 4;
  }

  @Override
  protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
    var dist = dx + dz;
    if (dist > radius * 2 - 2) {
      return true;
    }
    if (dist > radius * 2 - 3) {
      return random.nextBoolean();
    }
    return false;
  }
}
