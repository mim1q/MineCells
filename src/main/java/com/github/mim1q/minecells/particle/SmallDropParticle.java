package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;

public class SmallDropParticle extends ColoredParticle {
  public SmallDropParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
    super(world, x, y, z, velocityX, velocityY, velocityZ, color);
    this.gravityStrength = 0.7f;
  }

  @Override
  public void tick() {
    super.tick();
    if (onGround) {
      markDead();
    }
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }
}
