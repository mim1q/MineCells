package com.github.mim1q.minecells.world.densityfunction;

import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class RingDensityFunction implements DensityFunction {
  public static final CodecHolder<RingDensityFunction> CODEC_HOLDER = CodecHolder.of(
    RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.optionalFieldOf("grid_size", 1024).forGetter(it -> it.gridSize),
      Codec.INT.fieldOf("min_radius").forGetter(it -> it.minRadius),
      Codec.INT.optionalFieldOf("max_radius", 2048).forGetter(it -> it.maxRadius),
      Codec.INT.optionalFieldOf("blend_radius", 16).forGetter(it -> it.blendRadius),
      Codec.DOUBLE.optionalFieldOf("from_value", 0.0).forGetter(it -> it.fromValue),
      Codec.DOUBLE.optionalFieldOf("to_value", 1.0).forGetter(it -> it.toValue)
    ).apply(instance, RingDensityFunction::new))
  );

  private final int gridSize;
  private final int minRadius;
  private final int minRadiusSquared;
  private final int maxRadius;
  private final int maxRadiusSquared;
  private final int blendRadius;
  private final double fromValue;
  private final double toValue;

  public RingDensityFunction(int gridSize, int minRadius, int maxRadius, int blendRadius, double fromValue, double toValue) {
    this.gridSize = gridSize;
    this.minRadius = minRadius;
    this.maxRadius = maxRadius;
    this.blendRadius = blendRadius;
    this.minRadiusSquared = minRadius * minRadius;
    this.maxRadiusSquared = maxRadius * maxRadius;
    this.fromValue = fromValue;
    this.toValue = toValue;
  }

  @Override
  public double sample(NoisePos pos) {
    var centerX = MathUtils.getClosestMultiple(pos.blockX(), gridSize);
    var centerZ = MathUtils.getClosestMultiple(pos.blockZ(), gridSize);
    var distX = pos.blockX() - centerX;
    var distZ = pos.blockZ() - centerZ;
    var distSquared = distX * distX + distZ * distZ;
    if (distSquared < minRadiusSquared || distSquared > maxRadiusSquared) {
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
