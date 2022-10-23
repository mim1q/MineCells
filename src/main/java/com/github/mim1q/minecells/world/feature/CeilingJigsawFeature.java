package com.github.mim1q.minecells.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.Vec3i;

public class CeilingJigsawFeature extends JigsawFeature {
  public CeilingJigsawFeature(Codec<JigsawFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public Vec3i getOffset(Vec3i start) {
    return new Vec3i(0, -start.getY(), 0);
  }
}
