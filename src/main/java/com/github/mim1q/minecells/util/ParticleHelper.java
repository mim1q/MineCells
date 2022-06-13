package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.MineCellsClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Box;
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

    public static void addInBox(ClientWorld world, ParticleEffect effect, Box box, int amount, Vec3d velScale) {
        for (int i = 0; i < amount; i++) {
            double x = world.random.nextDouble(box.minX, box.maxX);
            double y = world.random.nextDouble(box.minY, box.maxY);
            double z = world.random.nextDouble(box.minZ, box.maxZ);

            Vec3d pos = new Vec3d(x, y, z);
            Vec3d vel = (box.getCenter().subtract(pos).normalize()).multiply(velScale);

            addParticle(world, effect, new Vec3d(x, y, z), vel);
        }
    }

    public static ParticleTextureSheet getTranslucentParticleType() {
        return MineCellsClient.CLIENT_CONFIG.rendering.opaqueParticles
            ? ParticleTextureSheet.PARTICLE_SHEET_OPAQUE
            : ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
