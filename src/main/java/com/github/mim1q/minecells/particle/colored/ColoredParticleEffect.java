package com.github.mim1q.minecells.particle.colored;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public record ColoredParticleEffect(ParticleType<?> type, int color) implements ParticleEffect {
  @SuppressWarnings("deprecation")
  public static final ParticleEffect.Factory<ColoredParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
    public ColoredParticleEffect read(ParticleType<ColoredParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
      stringReader.expect(' ');
      return new ColoredParticleEffect(particleType, stringReader.readInt());
    }

    public ColoredParticleEffect read(ParticleType<ColoredParticleEffect> particleType, PacketByteBuf packetByteBuf) {
      return new ColoredParticleEffect(particleType, packetByteBuf.readInt());
    }
  };

  public static Codec<ColoredParticleEffect> createCodec(ParticleType<ColoredParticleEffect> type) {
    return RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.fieldOf("color").forGetter(ColoredParticleEffect::getColor)
    ).apply(instance, color -> new ColoredParticleEffect(type, color)));
//    return Codec.INT.xmap(
//      (color) -> new ColoredParticleEffect(type, color),
//      (effect) -> effect.color
//    );
  }

  @Override
  public ParticleType<?> getType() {
    return this.type;
  }

  @Override
  public void write(PacketByteBuf buf) {
    buf.writeInt(this.color);
  }

  @Override
  public String asString() {
    Identifier id = Registries.PARTICLE_TYPE.getId(this.type());
    return String.valueOf(id) + this.color;
  }

  public int getColor() {
    return this.color;
  }
}