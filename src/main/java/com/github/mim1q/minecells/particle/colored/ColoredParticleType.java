package com.github.mim1q.minecells.particle.colored;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;

public class ColoredParticleType extends ParticleType<ColoredParticleEffect> {
  protected ColoredParticleType() {
    super(true, ColoredParticleEffect.PARAMETERS_FACTORY);
  }

  @Override
  public Codec<ColoredParticleEffect> getCodec() {
    return ColoredParticleEffect.createCodec(this);
  }

  public static ColoredParticleType create() {
    return new ColoredParticleType();
  }
}
