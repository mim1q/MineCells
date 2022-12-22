package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class SpeckleParticle extends SpriteBillboardParticle {
  public SpeckleParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
    super(world, x, y, z, velocityX, velocityY, velocityZ);
    int r = color >> 16;
    int g = color >> 8 & 0xFF;
    int b = color & 0xFF;
    this.red = r / 255.0F;
    this.green = g / 255.0F;
    this.blue = b / 255.0F;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
    this.velocityZ = velocityZ;
    this.scale = 0.15F + world.getRandom().nextFloat() * 0.2F;
    this.maxAge = 30;
  }

  @Override
  public void tick() {
    super.tick();
    float progress = this.age / (float) this.maxAge;
    this.alpha = 1.0F - progress;
    this.velocityMultiplier = 1.0F - progress;
  }

  public static SpeckleParticleEffect create(int color) {
    return new SpeckleParticleEffect(MineCellsParticles.SPECKLE, color);
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }

  public static class SpeckleParticleEffect implements ParticleEffect {
    @SuppressWarnings("deprecation")
    public static final ParticleEffect.Factory<SpeckleParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
      public SpeckleParticleEffect read(ParticleType<SpeckleParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
        stringReader.expect(' ');
        return new SpeckleParticleEffect(particleType, stringReader.readInt());
      }

      public SpeckleParticleEffect read(ParticleType<SpeckleParticleEffect> particleType, PacketByteBuf packetByteBuf) {
        return new SpeckleParticleEffect(particleType, packetByteBuf.readInt());
      }
    };

    public static Codec<SpeckleParticleEffect> createCodec(ParticleType<SpeckleParticleEffect> type) {
      return Codec.INT.xmap(
        (color) -> new SpeckleParticleEffect(type, color),
        (effect) -> effect.color
      );
    }

    private final ParticleType<?> type;
    private final int color;

    public SpeckleParticleEffect(ParticleType<?> type, int color) {
      this.type = type;
      this.color = color;
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
      Identifier id = Registry.PARTICLE_TYPE.getId(this.getType());
      return "" + id + this.color;
    }
  }

  public static class Factory implements ParticleFactory<SpeckleParticleEffect> {
    private final SpriteProvider spriteProvider;
    public Factory(SpriteProvider spriteProvider) {
      this.spriteProvider = spriteProvider;
    }

    @Nullable
    @Override
    public Particle createParticle(SpeckleParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      SpeckleParticle particle = new SpeckleParticle(world, x, y, z, velocityX, velocityY, velocityZ, parameters.color);
      particle.setSprite(spriteProvider);
      return particle;
    }
  }
}
