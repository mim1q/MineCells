package com.github.mim1q.minecells.particle.colored;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public record ColoredParticleEffect(ParticleType<?> type, int color) implements ParticleEffect {
  public static MapCodec<ColoredParticleEffect> createCodec(ParticleType<ColoredParticleEffect> type) {
    return RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.INT.fieldOf("color").forGetter(ColoredParticleEffect::getColor)
    ).apply(instance, color -> new ColoredParticleEffect(type, color)));
//    return Codec.INT.xmap(
//      (color) -> new ColoredParticleEffect(type, color),
//      (effect) -> effect.color
//    );
  }

  public static PacketCodec<RegistryByteBuf, ColoredParticleEffect> createPacketCodec(ParticleType<ColoredParticleEffect> type) {
    return PacketCodecs.registryCodec(createCodec(type).codec());
  }

  @Override
  public ParticleType<?> getType() {
    return this.type;
  }

  public int getColor() {
    return this.color;
  }
}