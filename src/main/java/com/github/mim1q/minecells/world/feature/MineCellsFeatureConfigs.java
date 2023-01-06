package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.feature.tree.BigPromenadeTreeTrunkPlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class MineCellsFeatureConfigs {
  public static final TreeFeatureConfig PROMENADE_TREE_CONFIG = simpleTree(
    new PromenadeTreeTrunkPlacer(30, 20, 0)
  );
  public static final TreeFeatureConfig BIG_PROMENADE_TREE_CONFIG = simpleTree(
    new BigPromenadeTreeTrunkPlacer(32, 24, 24)
  );

  private static TreeFeatureConfig simpleTree(TrunkPlacer trunkPlacer) {
    return new TreeFeatureConfig.Builder(
      BlockStateProvider.of(MineCellsBlocks.PUTRID_LOG.getDefaultState()),
      trunkPlacer,
      BlockStateProvider.of(MineCellsBlocks.WILTED_LEAVES.getDefaultState()),
      new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
      new TwoLayersFeatureSize(1, 0, 1)
    ).ignoreVines().build();
  }
}
