package com.github.mim1q.minecells.particle.colored;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
    return Codec.INT.xmap(
      (color) -> new ColoredParticleEffect(type, color),
      (effect) -> effect.color
    );
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
    Identifier id = Registry.PARTICLE_TYPE.getId(this.type());
    return "" + id + this.color;
  }

  public int getColor() {
    return this.color;
  }
}