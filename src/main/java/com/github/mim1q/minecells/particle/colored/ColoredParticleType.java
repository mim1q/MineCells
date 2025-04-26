package com.github.mim1q.minecells.particle.colored;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class ColoredParticleType extends ParticleType<ColoredParticleEffect> {
  protected ColoredParticleType() {
    super(true);
  }

  @Override
  public MapCodec<ColoredParticleEffect> getCodec() {
    return ColoredParticleEffect.createCodec(this);
  }

  @Override
  public PacketCodec<? super RegistryByteBuf, ColoredParticleEffect> getPacketCodec() {
    return ColoredParticleEffect.createPacketCodec(this);
  }

  public static ColoredParticleType create() {
    return new ColoredParticleType();
  }

  public ParticleEffect get(int color) {
    return new ColoredParticleEffect(this, color);
  }
}
