package com.github.mim1q.minecells.util;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

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

    public static Vec3d vectorRotateY(Vec3d vector, float theta) {
        double z = vector.z * MathHelper.sin(theta) + vector.x * MathHelper.cos(theta);
        double x = vector.z * MathHelper.cos(theta) - vector.x * MathHelper.sin(theta);
        return new Vec3d(x, vector.y, z);
    }

    public static Vec3f vectorRotateY(Vec3f vector, float theta) {
        float z = vector.getZ() * MathHelper.sin(theta) + vector.getX() * MathHelper.cos(theta);
        float x = vector.getZ() * MathHelper.cos(theta) - vector.getX() * MathHelper.sin(theta);
        return new Vec3f(x, vector.getY(), z);
    }
}
