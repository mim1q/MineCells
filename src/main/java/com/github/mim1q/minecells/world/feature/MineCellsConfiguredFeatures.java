package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.feature.JigsawFeature.JigsawFeatureConfig;
import com.github.mim1q.minecells.world.feature.WallPlantsFeature.WallPlantsFeatureConfig;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;

public class MineCellsConfiguredFeatures {
  public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> PROMENADE_TREE = createConfiguredFeature(
    MineCells.createId("promenade_tree"),
    new ConfiguredFeature<>(Feature.TREE, MineCellsFeatureConfigs.PROMENADE_TREE_CONFIG)
  );

  public static final RegistryEntry<ConfiguredFeature<JigsawFeatureConfig, ?>> CEILING_CAGES = createConfiguredFeature(
    MineCells.createId("ceiling_cages"),
    new ConfiguredFeature<>(
      MineCellsFeatures.JIGSAW_FEATURE,
      MineCellsFeatureConfigs.CEILING_CAGES_CONFIG
    )
  );

  public static final RegistryEntry<ConfiguredFeature<JigsawFeatureConfig, ?>> CEILING_CHAINS = createConfiguredFeature(
    MineCells.createId("ceiling_chains"),
    new ConfiguredFeature<>(
      MineCellsFeatures.JIGSAW_FEATURE,
      MineCellsFeatureConfigs.CEILING_CHAINS_CONFIG
    )
  );

  public static final RegistryEntry<ConfiguredFeature<JigsawFeatureConfig, ?>> CEILING_BIG_CHAINS = createConfiguredFeature(
    MineCells.createId("ceiling_big_chains"),
    new ConfiguredFeature<>(
      MineCellsFeatures.JIGSAW_FEATURE,
      MineCellsFeatureConfigs.CEILING_BIG_CHAINS_CONFIG
    )
  );

  public static final RegistryEntry<ConfiguredFeature<WallPlantsFeatureConfig, ?>> WILTED_LEAVES = createConfiguredFeature(
    MineCells.createId("wilted_leaves"),
    new ConfiguredFeature<>(
      MineCellsFeatures.WALL_PLANTS_FEATURE,
      MineCellsFeatureConfigs.WILTED_LEAVES_CONFIG
    )
  );

  private static <FC extends FeatureConfig, F extends Feature<FC>>
  RegistryEntry<ConfiguredFeature<FC, ?>> createConfiguredFeature(Identifier id, ConfiguredFeature<FC, F> feature) {
    return BuiltinRegistries.addCasted(BuiltinRegistries.CONFIGURED_FEATURE, id.toString(), feature);
  }
}
