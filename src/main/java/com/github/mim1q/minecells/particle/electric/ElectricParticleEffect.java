package com.github.mim1q.minecells.particle.electric;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;

public record ElectricParticleEffect(
  Vec3d direction,
  int length,
  int color,
  float size,
  boolean isMainBranch
) implements ParticleEffect {
  public static final MapCodec<ElectricParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    Vec3d.CODEC.fieldOf("direction").forGetter(ElectricParticleEffect::direction),
    Codec.INT.fieldOf("length").forGetter(ElectricParticleEffect::length),
    Codec.INT.fieldOf("color").forGetter(ElectricParticleEffect::color),
    Codec.FLOAT.fieldOf("size").forGetter(ElectricParticleEffect::size),
    Codec.BOOL.fieldOf("isMainBranch").forGetter(ElectricParticleEffect::isMainBranch)
  ).apply(instance, ElectricParticleEffect::new));

  public static final PacketCodec<RegistryByteBuf, ElectricParticleEffect> PACKET_CODEC = PacketCodecs.registryCodec(CODEC.codec());

  @Override
  public ParticleType<?> getType() {
    return MineCellsParticles.ELECTRICITY;
  }
}
