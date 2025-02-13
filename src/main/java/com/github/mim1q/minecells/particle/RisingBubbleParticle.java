package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;

public class RisingBubbleParticle extends ColoredParticle {
  public RisingBubbleParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
    super(world, x, y, z, velocityX, velocityY, velocityZ, color);
    this.velocityY = velocityY;
    this.velocityMultiplier = 1f;
    maxAge = 60 + random.nextInt(40);
    setAlpha(0.2f + random.nextFloat() * 0.5f);
  }

  @Override
  public void tick() {
    this.prevPosX = this.x;
    this.prevPosY = this.y;
    this.prevPosZ = this.z;

    if (this.age++ >= this.maxAge || this.velocityY <= 0.008D) {
      this.markDead();
      var count = random.nextInt(3) + 1;
      for (int i = 0; i < count; i++) {
        world.addParticle(
          MineCellsParticles.SMALL_DROP.get(this.color),
          x, y, z, (random.nextDouble() - 0.5) * 0.3, 0.1D, (random.nextDouble() - 0.5) * 0.3
        );
      }
    } else {
      this.y += this.velocityY;
      this.velocityY -= 0.001D;
    }
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }
}
