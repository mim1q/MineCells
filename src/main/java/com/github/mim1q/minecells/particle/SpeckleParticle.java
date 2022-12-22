package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;

public class SpeckleParticle extends ColoredParticle {
  public SpeckleParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
    super(world, x, y, z, velocityX, velocityY, velocityZ, color);
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

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }
}
