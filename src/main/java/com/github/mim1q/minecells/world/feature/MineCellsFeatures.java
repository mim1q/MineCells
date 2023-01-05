package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class MineCellsFeatures {
  private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
    return Registry.register(Registry.FEATURE, MineCells.createId(name), feature);
  }
}
