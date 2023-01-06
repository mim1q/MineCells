package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class MineCellsConfiguredFeatures {
  public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> PROMENADE_TREE = createConfiguredFeature(
    MineCells.createId("promenade_tree"),
    new ConfiguredFeature<>(Feature.TREE, MineCellsFeatureConfigs.PROMENADE_TREE_CONFIG)
  );

  public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BIG_PROMENADE_TREE = createConfiguredFeature(
    MineCells.createId("big_promenade_tree"),
    new ConfiguredFeature<>(Feature.TREE, MineCellsFeatureConfigs.BIG_PROMENADE_TREE_CONFIG)
  );

  private static <FC extends FeatureConfig, F extends Feature<FC>>
  RegistryEntry<ConfiguredFeature<FC, ?>> createConfiguredFeature(Identifier id, ConfiguredFeature<FC, F> feature) {
    return BuiltinRegistries.addCasted(BuiltinRegistries.CONFIGURED_FEATURE, id.toString(), feature);
  }
}
