package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class FallingLeafParticle extends ColoredParticle {
  private final float rotationSpeed;

  public FallingLeafParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
    super(world, x, y + 5, z, velocityX, velocityY, velocityZ, color);
    this.rotationSpeed = world.random.nextFloat() * 0.4F - 0.2F;
    setAlpha(0f);
    if (y < 60 || y > 130) {
      markDead();
      this.maxAge = 0;
      return;
    }
    this.velocityX = 0.05F + 0.1F * world.random.nextFloat();
    this.velocityY = -0.15F - 0.1F * world.random.nextFloat();
    this.velocityZ = 0.05F + 0.1F * world.random.nextFloat();
    this.scale = 0.25F + world.random.nextFloat() * 0.25F;
    this.maxAge = 50 + world.random.nextInt(100);
    this.collidesWithWorld = true;
  }

  @Override
  public void setSpriteForAge(SpriteProvider spriteProvider) {
    this.setSprite(spriteProvider.getSprite(world.random));
  }

  @Override
  public float getSize(float tickDelta) {
    if (age <= 10) {
      setAlpha(age / 10f);
    }
    var left = maxAge - age;
    if (left <= 10) {
      setAlpha(left / 10f);
    }
    prevAngle = angle;
    if (!onGround) {
      angle = (age + tickDelta) * rotationSpeed;
    }
    return super.getSize(tickDelta);
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }
}
