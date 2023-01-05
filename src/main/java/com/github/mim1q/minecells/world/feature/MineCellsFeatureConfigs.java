package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class MineCellsFeatureConfigs {
  public static final TreeFeatureConfig PROMENADE_TREE_CONFIG = simpleTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 20, 15);

  private static TreeFeatureConfig simpleTree(Block trunk, Block leaves, int height, int heightRandom) {
    return new TreeFeatureConfig.Builder(
      BlockStateProvider.of(trunk.getDefaultState()),
      new PromenadeTreeTrunkPlacer(height, heightRandom, 0),
      BlockStateProvider.of(leaves.getDefaultState()),
      new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
      new TwoLayersFeatureSize(1, 0, 1)
    ).ignoreVines().build();
  }
}
