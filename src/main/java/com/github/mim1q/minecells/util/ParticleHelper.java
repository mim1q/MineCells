package com.github.mim1q.minecells.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

public class ParticleHelper {

    public static void addParticle(ClientWorld world, ParticleEffect particle, Vec3d pos, Vec3d vel) {
        world.addParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
    }

    public static void addAura(ClientWorld world, Vec3d position, ParticleEffect particle, int amount, double radius, double speed) {
        for (int i = 0; i < amount; i++) {
            Vec3d offset = new Vec3d(
                    world.random.nextDouble() * 2.0d - 1.0d,
                    world.random.nextDouble() * 2.0d - 1.0d,
                    world.random.nextDouble() * 2.0d - 1.0d
            ).normalize();
            Vec3d velocity = offset.multiply(speed);
            offset = offset.multiply(radius);
            addParticle(
                    world,
                    particle,
                    position.add(offset),
                    velocity
            );
        }
    }
}
