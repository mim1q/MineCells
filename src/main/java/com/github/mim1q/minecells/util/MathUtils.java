package com.github.mim1q.minecells.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class MathUtils {
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

    public static Vec3d lerp(Vec3d v0, Vec3d v1, float delta) {
        double x = MathHelper.lerp(delta, v0.x, v1.x);
        double y = MathHelper.lerp(delta, v0.y, v1.y);
        double z = MathHelper.lerp(delta, v0.z, v1.z);

        return new Vec3d(x, y, z);
    }

    public static float easeInOutQuad(float a, float b, float delta) {
        delta = delta < 0.5F
            ? 2.0F * delta * delta
            : 1.0F - (-2.0F * delta + 2.0F) * (-2.0F * delta + 2.0F) * 0.5F;
        return MathHelper.lerp(delta, a, b);
    }

    public static float radians(float degrees) {
        return degrees * RADIANS_PER_DEGREE;
    }
}
