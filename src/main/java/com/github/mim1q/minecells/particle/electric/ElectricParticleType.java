package com.github.mim1q.minecells.particle.electric;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("deprecation")
public class ElectricParticleType extends ParticleType<ElectricParticleEffect> {
  protected ElectricParticleType(boolean alwaysShow, ParticleEffect.Factory<ElectricParticleEffect> parametersFactory) {
    super(alwaysShow, parametersFactory);
  }

  @Override
  public Codec<ElectricParticleEffect> getCodec() {
    return ElectricParticleEffect.CODEC;
  }

  public ElectricParticleEffect get(Vec3d direction, int length, int color, float size) {
    return new ElectricParticleEffect(direction, length, color, size, true);
  }

  public static ElectricParticleType create() {
    return new ElectricParticleType(true, ElectricParticleEffect.PARAMETERS_FACTORY);
  }
}
