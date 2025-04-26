package com.github.mim1q.minecells.particle.electric;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;

public class ElectricParticleType extends ParticleType<ElectricParticleEffect> {
  protected ElectricParticleType(boolean alwaysShow) {
    super(alwaysShow);
  }

  public ElectricParticleEffect get(Vec3d direction, int length, int color, float size) {
    return new ElectricParticleEffect(direction, length, color, size, true);
  }

  public static ElectricParticleType create() {
    return new ElectricParticleType(true);
  }

  @Override
  public MapCodec<ElectricParticleEffect> getCodec() {
    return ElectricParticleEffect.CODEC;
  }

  @Override
  public PacketCodec<? super RegistryByteBuf, ElectricParticleEffect> getPacketCodec() {
    return ElectricParticleEffect.PACKET_CODEC;
  }
}
