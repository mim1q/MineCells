package com.github.mim1q.minecells.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class FlyParticle extends SpriteBillboardParticle {
  protected FlyParticle(ClientWorld world, double x, double y, double z, double vy) {
    super(world, x, y, z);
    this.maxAge = 30 + world.getRandom().nextInt(20);
    this.velocityY = vy;
    this.scale = 0.15F + world.getRandom().nextFloat() * 0.15F;
  }

  @Override
  public void tick() {
    super.tick();
    if (this.world.getRandom().nextFloat() < 0.25F) {
      randomizeVelocity();
    }
    if (this.age < 5) {
      this.setAlpha(this.age / 5.0F);
    }
    if (this.age > this.maxAge - 5) {
      this.setAlpha((this.maxAge - this.age) / 5.0F);
    }
  }

  private void randomizeVelocity() {
    this.velocityX = this.world.getRandom().nextFloat() * 0.1F - 0.05F;
    this.velocityZ = this.world.getRandom().nextFloat() * 0.1F - 0.05F;
  }

  @Override
  protected float getMaxU() {
    float diff = super.getMaxU() - super.getMinU();
    return this.getMinU() + diff * 0.5F;
  }

  @Override
  protected float getMaxV() {
    float diff = super.getMaxV() - super.getMinV();
    float multiplier = this.age % 2 == 0 ? 0.5F : 1.0F;
    return super.getMinV() + diff * multiplier;
  }

  @Override
  protected float getMinV() {
    float diff = super.getMaxV() - super.getMinV();
    float multiplier = this.age % 2 == 0 ? 0.0F : 0.5F;
    return super.getMinV() + diff * multiplier;
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }

  public static class Factory implements ParticleFactory<DefaultParticleType> {
    private final SpriteProvider spriteProvider;

    public Factory(SpriteProvider spriteProvider) {
      this.spriteProvider = spriteProvider;
    }

    @Nullable
    @Override
    public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      FlyParticle particle = new FlyParticle(world, x, y, z, velocityY);
      particle.setSprite(this.spriteProvider);
      return particle;
    }
  }
}
