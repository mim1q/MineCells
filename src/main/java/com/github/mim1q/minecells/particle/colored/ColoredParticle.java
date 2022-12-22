package com.github.mim1q.minecells.particle.colored;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

public abstract class ColoredParticle extends SpriteBillboardParticle {
  public ColoredParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
    super(world, x, y, z, velocityX, velocityY, velocityZ);
    int r = color >> 16;
    int g = color >> 8 & 0xFF;
    int b = color & 0xFF;
    this.red = r / 255.0F;
    this.green = g / 255.0F;
    this.blue = b / 255.0F;
  }

  @FunctionalInterface
  public interface ColoredParticleConstructor {
    ColoredParticle create(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color);
  }

  public static ParticleFactoryRegistry.PendingParticleFactory<ColoredParticleEffect> createFactory(ColoredParticleConstructor constructor) {
    return (spriteProvider) -> new Factory(spriteProvider, constructor);
  }

  public static class Factory implements ParticleFactory<ColoredParticleEffect> {
    private final SpriteProvider spriteProvider;
    private final ColoredParticleConstructor constructor;

    public Factory(SpriteProvider spriteProvider, ColoredParticleConstructor constructor) {
      this.spriteProvider = spriteProvider;
      this.constructor = constructor;
    }

    @Nullable
    @Override
    public Particle createParticle(ColoredParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      ColoredParticle particle = this.constructor.create(world, x, y, z, velocityX, velocityY, velocityZ, parameters.getColor());
      particle.setSpriteForAge(this.spriteProvider);
      return particle;
    }
  }
}
