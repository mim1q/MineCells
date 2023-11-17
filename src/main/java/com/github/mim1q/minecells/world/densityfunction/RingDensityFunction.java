package com.github.mim1q.minecells.world.densityfunction;

import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record RingDensityFunction(
  int gridSize,
  int minRadius,
  int maxRadius,
  int blendRadius,
  double fromValue,
  double toValue,
  int offsetX,
  int offsetZ,
  boolean offsetGrid
) implements DensityFunction {
  public static final CodecHolder<RingDensityFunction> CODEC_HOLDER = CodecHolder.of(
    RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.optionalFieldOf("grid_size", 1024).forGetter(it -> it.gridSize),
      Codec.INT.fieldOf("min_radius").forGetter(it -> it.minRadius),
      Codec.INT.optionalFieldOf("max_radius", 2048).forGetter(it -> it.maxRadius),
      Codec.INT.optionalFieldOf("blend_radius", 16).forGetter(it -> it.blendRadius),
      Codec.DOUBLE.optionalFieldOf("from_value", 0.0).forGetter(it -> it.fromValue),
      Codec.DOUBLE.optionalFieldOf("to_value", 1.0).forGetter(it -> it.toValue),
      Codec.INT.optionalFieldOf("offset_x", 0).forGetter(it -> it.offsetX),
      Codec.INT.optionalFieldOf("offset_z", 0).forGetter(it -> it.offsetZ),
      Codec.BOOL.optionalFieldOf("offset_grid", false).forGetter(it -> it.offsetGrid)
    ).apply(instance, RingDensityFunction::new))
  );

  @Override
  public double sample(NoisePos pos) {
    var centerX = MathUtils.getClosestMultiple(pos.blockX() - (offsetGrid ? offsetX : 0), gridSize);
    var centerZ = MathUtils.getClosestMultiple(pos.blockZ() - (offsetGrid ? offsetZ : 0), gridSize);
    var distX = pos.blockX() - centerX - (offsetGrid ? 0 : offsetX);
    var distZ = pos.blockZ() - centerZ - (offsetGrid ? 0 : offsetZ);
    var distSquared = distX * distX + distZ * distZ;
    if (distSquared < minRadius * minRadius || distSquared > maxRadius * maxRadius) {
      return fromValue;
    }
    if (blendRadius == 0) {
      return toValue;
    }
    var dist = Math.sqrt(distSquared);
    var distToEdge = Math.min(dist - minRadius, maxRadius - dist);
    var delta = Math.min(distToEdge / (double) blendRadius, 1.0);
    return MathHelper.lerp(delta, fromValue, toValue);
  }

  @Override
  public void fill(double[] densities, EachApplier applier) {
    applier.fill(densities, this);
  }

  @Override
  public DensityFunction apply(DensityFunctionVisitor visitor) {
    return visitor.apply(this);
  }

  @Override
  public double minValue() {
    return Math.min(fromValue, toValue);
  }

  @Override
  public double maxValue() {
    return Math.max(fromValue, toValue);
  }

  @Override
  public CodecHolder<? extends DensityFunction> getCodecHolder() {
    return CODEC_HOLDER;
  }
}
