package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.feature.JigsawFeature.JigsawFeatureConfig;
import com.github.mim1q.minecells.world.feature.WallPlantsFeature.WallPlantsFeatureConfig;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.Optional;

public class MineCellsFeatureConfigs {
  public static final TreeFeatureConfig PROMENADE_TREE_CONFIG = simpleTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 10, 5);

  public static final JigsawFeatureConfig CEILING_CAGES_CONFIG = new JigsawFeatureConfig(
    MineCells.createId("ceiling_decoration/cages"),
    MineCells.createId("decoration")
  );

  public static final JigsawFeatureConfig CEILING_CHAINS_CONFIG = new JigsawFeatureConfig(
    MineCells.createId("ceiling_decoration/chains"),
    MineCells.createId("decoration")
  );

  public static final JigsawFeatureConfig CEILING_BIG_CHAINS_CONFIG = new JigsawFeatureConfig(
    MineCells.createId("ceiling_decoration/big_chains"),
    MineCells.createId("decoration")
  );

  public static final WallPlantsFeatureConfig WILTED_LEAVES_CONFIG = new WallPlantsFeatureConfig(
    BlockStateProvider.of(MineCellsBlocks.WALL_WILTED_LEAVES.getDefaultState()),
    Optional.of(
      BlockStateProvider.of(MineCellsBlocks.WILTED_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true))
    ),
    UniformIntProvider.create(2, 4),
    0.66f,
    0.1f
  );

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
