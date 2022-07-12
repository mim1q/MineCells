package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.util.ParticleUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class ProtectorParticle extends SpriteBillboardParticle {

  protected final float maxSize;

  protected ProtectorParticle(ClientWorld clientWorld, double d, double e, double f) {
    super(clientWorld, d, e, f);
    //this.setVelocity(0.0D, 0.0D, 0.0D);
    this.setMaxAge(clientWorld.random.nextBetween(10, 30));
    this.angle = clientWorld.random.nextFloat() * MathHelper.PI * 2.0F;
    this.prevAngle = this.angle;
    this.maxSize = this.getMaxAge() * 0.05F;
  }

  @Override
  public void tick() {
    super.tick();
    this.setAlpha(1.0F - ((float) this.age / this.getMaxAge()));
  }

  @Override
  public float getSize(float tickDelta) {
    return this.maxSize * (((float) this.age + tickDelta) / this.getMaxAge());
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleUtils.getTranslucentParticleType();
  }

  @Environment(EnvType.CLIENT)
  public static class Factory implements ParticleFactory<DefaultParticleType> {
    private final SpriteProvider spriteProvider;

    public Factory(SpriteProvider spriteProvider) {
      this.spriteProvider = spriteProvider;
    }

    public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
      ProtectorParticle protectorParticle = new ProtectorParticle(clientWorld, d, e, f);
      protectorParticle.setVelocity(g, h, i);
      protectorParticle.setSprite(this.spriteProvider);
      return protectorParticle;
    }
  }
}
