package com.github.mim1q.minecells.particle.electric;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
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

  @SuppressWarnings("deprecation")
  public static final ParticleEffect.Factory<ElectricParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
    public ElectricParticleEffect read(
      ParticleType<ElectricParticleEffect> particleType,
      StringReader stringReader
    ) throws CommandSyntaxException {
      stringReader.expect(' '); var x = stringReader.readDouble();
      stringReader.expect(' '); var y = stringReader.readDouble();
      stringReader.expect(' '); var z = stringReader.readDouble();
      stringReader.expect(' '); var length = stringReader.readInt();
      stringReader.expect(' '); var color = stringReader.readInt();
      stringReader.expect(' '); var size = stringReader.readFloat();
      stringReader.expect(' '); var isMainBranch = stringReader.readBoolean();
      return new ElectricParticleEffect(new Vec3d(x, y, z), length, color, size, isMainBranch);
    }

    public ElectricParticleEffect read(ParticleType<ElectricParticleEffect> particleType, PacketByteBuf packetByteBuf) {
      var x = packetByteBuf.readDouble();
      var y = packetByteBuf.readDouble();
      var z = packetByteBuf.readDouble();
      var length = packetByteBuf.readInt();
      var color = packetByteBuf.readInt();
      var size = packetByteBuf.readFloat();
      var isMainBranch = packetByteBuf.readBoolean();
      return new ElectricParticleEffect(new Vec3d(x, y, z), length, color, size, isMainBranch);
    }
  };

  public static final Codec<ElectricParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Vec3d.CODEC.fieldOf("direction").forGetter(ElectricParticleEffect::direction),
    Codec.INT.fieldOf("length").forGetter(ElectricParticleEffect::length),
    Codec.INT.fieldOf("color").forGetter(ElectricParticleEffect::color),
    Codec.FLOAT.fieldOf("size").forGetter(ElectricParticleEffect::size),
    Codec.BOOL.fieldOf("isMainBranch").forGetter(ElectricParticleEffect::isMainBranch)
  ).apply(instance, ElectricParticleEffect::new));

  @Override public ParticleType<?> getType() {
    return MineCellsParticles.ELECTRICITY;
  }

  @Override public void write(PacketByteBuf buf) {
    buf.writeDouble(this.direction.x);
    buf.writeDouble(this.direction.y);
    buf.writeDouble(this.direction.z);
    buf.writeDouble(this.length);
    buf.writeInt(this.color);
    buf.writeFloat(this.size);
    buf.writeBoolean(this.isMainBranch);
  }

  @Override
  public String asString() {
    return String.format(
      "Electric: direction %f %f %f, length: %d, color: 0x%08X, size: %f, isMainBranch: %b",
      this.direction.x,
      this.direction.y,
      this.direction.z,
      this.length,
      this.color,
      this.size,
      this.isMainBranch
    );
  }
}
