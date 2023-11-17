package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class MineCellsFeatures {
  public static final JigsawFeature JIGSAW_FEATURE = register(
    "jigsaw",
    new JigsawFeature(JigsawFeature.JigsawFeatureConfig.CODEC)
  );

  private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
    return Registry.register(Registries.FEATURE, MineCells.createId(name), feature);
  }

  public static void init() { }
}
