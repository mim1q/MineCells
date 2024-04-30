package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;

public class DropParticle extends ColoredParticle {
  public DropParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
    super(world, x, y, z, velocityX, velocityY, velocityZ, color);
    this.gravityStrength = 1.0f;
    this.collidesWithWorld = true;
    this.maxAge = 40;
  }

  @Override
  public void tick() {
    super.tick();
    if (onGround) {
      for (int i = 0; i < 4; i++) {
        world.addParticle(
          MineCellsParticles.SMALL_DROP.get(this.color),
          x,
          y,
          z,
          (random.nextDouble() - 0.5) * 0.2,
          0.1D,
          (random.nextDouble() - 0.5) * 0.2
        );
        markDead();
      }
    }
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }
}
