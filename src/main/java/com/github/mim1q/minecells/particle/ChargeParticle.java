package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class ChargeParticle extends SpriteBillboardParticle {

    protected final float maxSize;

    protected ChargeParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
        this.setMaxAge(clientWorld.random.nextBetween(10, 25));
        this.angle = clientWorld.random.nextFloat() * MathHelper.PI * 2.0F;
        this.prevAngle = this.angle;
        this.maxSize = this.getMaxAge() * 0.1F;
        this.setAlpha(0.0F);
    }

    @Override
    public void tick() {
        this.setAlpha(0.1F + ((float)this.age * 3.0F) / (float)this.getMaxAge());
        super.tick();
    }

    @Override
    public float getSize(float tickDelta) {
        return Math.max(this.maxSize - (this.age + tickDelta) * 0.2F, 0.0F);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleUtils.getTranslucentParticleType();
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ChargeParticle chargeParticle = new ChargeParticle(world, x, y, z);
            chargeParticle.setSprite(spriteProvider);
            return chargeParticle;
        }
    }
}
