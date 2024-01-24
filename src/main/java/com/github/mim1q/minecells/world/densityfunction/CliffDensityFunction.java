package com.github.mim1q.minecells.world.densityfunction;

import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;

import java.util.Optional;

public record CliffDensityFunction(
  int gridSize,
  boolean zAxis,
  int width,
  int offset,
  int fromY,
  int toY,
  double taperWidth,
  Optional<DensityFunction> offsetNoise
) implements DensityFunction {

  public static final CodecHolder<CliffDensityFunction> CODEC_HOLDER = CodecHolder.of(
    RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.optionalFieldOf("grid_size", 1024).forGetter(it -> it.gridSize),
      Codec.BOOL.optionalFieldOf("z_axis", false).forGetter(it -> it.zAxis),
      Codec.INT.fieldOf("width").forGetter(it -> it.width),
      Codec.INT.fieldOf("offset").forGetter(it -> it.offset),
      Codec.INT.fieldOf("from_y").forGetter(it -> it.fromY),
      Codec.INT.fieldOf("to_y").forGetter(it -> it.toY),
      Codec.DOUBLE.fieldOf("taper_width").forGetter(it -> it.taperWidth),
      DensityFunction.CODEC.optionalFieldOf("offset_noise").forGetter(it -> it.offsetNoise)
    ).apply(instance, CliffDensityFunction::new))
  );

  @Override
  public double sample(NoisePos pos) {
    var coord = (zAxis ? pos.blockZ() : pos.blockX()) - offset;
    var y = pos.blockY();
    var center = MathUtils.getClosestMultiple(coord, gridSize);
    var dist = Math.abs(coord - center);
    var maxDist = width / 2.0;
    if (dist > maxDist) {
      return 0.0;
    }
    if (offsetNoise.isPresent()) {
      var sample = offsetNoise.get().sample(pos);
      dist += (int) (taperWidth / 2 * sample);
    }
    if (y < fromY || y > toY) {
      return 1.0;
    }
    var heightFraction = (y - fromY) / (double) (toY - fromY);
    heightFraction = 2.0 * (heightFraction - 0.5);
    heightFraction = 1.0 - Math.abs(heightFraction);

    if (heightFraction > 0.5) {
      heightFraction = 1.0;
    } else {
      heightFraction = 2.0 * heightFraction;
    }

    var distanceToEdge = maxDist - dist;
    var randomOffset = 1.0;
    var maxDistanceToEdge = taperWidth * heightFraction * randomOffset;

    return MathUtils.easeInQuad(0f, 1f, (float) MathHelper.clamp((distanceToEdge / maxDistanceToEdge) * randomOffset, 0.0, 1.0));
  }

  @Override
  public void fill(double[] densities, EachApplier applier) {
    applier.fill(densities, this);
  }

  @Override
  public DensityFunction apply(DensityFunctionVisitor visitor) {
    var offsetNoise = this.offsetNoise.map(it -> it.apply(visitor));
    return visitor.apply(new CliffDensityFunction(
      gridSize,
      zAxis,
      width,
      offset,
      fromY,
      toY,
      taperWidth,
      offsetNoise
    ));
  }

  @Override
  public double minValue() {
    return 0.0;
  }

  @Override
  public double maxValue() {
    return 1.0;
  }

  @Override
  public CodecHolder<? extends DensityFunction> getCodecHolder() {
    return CODEC_HOLDER;
  }
}
