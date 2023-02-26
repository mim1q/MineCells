package com.github.mim1q.minecells.world;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

public class PromenadeDimensionEffects extends DimensionEffects {
  public PromenadeDimensionEffects() {
    super(110.0F, true, SkyType.NORMAL, false, true);
  }

  @Override
  public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
    return color;
  }

  @Override
  public float[] getFogColorOverride(float skyAngle, float tickDelta) {
    return null;
  }

  @Override
  public boolean useThickFog(int camX, int camY) {
    return true;
  }
}
