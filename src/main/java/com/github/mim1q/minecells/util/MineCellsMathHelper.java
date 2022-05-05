package com.github.mim1q.minecells.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class MineCellsMathHelper {
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
