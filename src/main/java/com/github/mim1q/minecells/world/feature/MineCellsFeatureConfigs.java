package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.feature.tree.PromenadeShrubTrunkPlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class MineCellsFeatureConfigs {
  public static final TreeFeatureConfig PROMENADE_TREE_CONFIG = simpleTree(
    new PromenadeTreeTrunkPlacer(30, 20, 0)
  ).build();
  public static final TreeFeatureConfig PROMENADE_TREE_SAPLING_CONFIG = simpleTree(
    new PromenadeTreeTrunkPlacer(20, 15, 0)
  ).ignoreVines().build();
  public static final TreeFeatureConfig PROMENADE_SHRUB_CONFIG = shrub(
    new PromenadeShrubTrunkPlacer(1, 0, 0)
  ).build();

  private static TreeFeatureConfig.Builder simpleTree(TrunkPlacer trunkPlacer) {
    return new TreeFeatureConfig.Builder(
      BlockStateProvider.of(MineCellsBlocks.PUTRID_WOOD.log.getDefaultState()),
      trunkPlacer,
      BlockStateProvider.of(MineCellsBlocks.WILTED_LEAVES.leaves.getDefaultState()),
      new BushFoliagePlacer(UniformIntProvider.create(1, 2), ConstantIntProvider.create(0), 2),
      new TwoLayersFeatureSize(1, 0, 1)
    );
  }

  private static TreeFeatureConfig.Builder shrub(TrunkPlacer trunkPlacer) {
    return new TreeFeatureConfig.Builder(
      BlockStateProvider.of(MineCellsBlocks.PUTRID_WOOD.log.getDefaultState()),
      trunkPlacer,
      BlockStateProvider.of(MineCellsBlocks.WILTED_LEAVES.leaves.getDefaultState().with(LeavesBlock.PERSISTENT, true)),
      new BushFoliagePlacer(UniformIntProvider.create(1, 2), ConstantIntProvider.create(0), 2),
      new TwoLayersFeatureSize(1, 0, 1)
    );
  }
}
