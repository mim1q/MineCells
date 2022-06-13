package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.util.ParticleHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class ExplosionParticle extends SpriteBillboardParticle {

    float targetRadius;
    float currentRadius;

    protected ExplosionParticle(ClientWorld clientWorld, double x, double y, double z, float r) {
        super(clientWorld, x, y, z, 0.0D, 0.0D, 0.0D);
        this.setVelocity(0.0D, 0.0D, 0.0D);
        this.setMaxAge(5);
        this.targetRadius = r;
        this.currentRadius = 0.0F;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.currentRadius;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age < 3) {
            this.currentRadius = MathHelper.clampedLerp(0.0F, this.targetRadius, this.age / 2.0F);
        } else {
            this.currentRadius = MathHelper.clampedLerp(this.targetRadius, 0.0F, (this.age - 2) / 6.0F);
        }
        this.setAlpha(this.currentRadius / this.targetRadius);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleHelper.getTranslucentParticleType();
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ExplosionParticle explosionParticle = new ExplosionParticle(clientWorld, d, e, f, (float)g);
            explosionParticle.setSprite(this.spriteProvider);
            return explosionParticle;
        }
    }
}
