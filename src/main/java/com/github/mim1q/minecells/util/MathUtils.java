package com.github.mim1q.minecells.util;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

public class MathUtils {
  public static Vec3d vectorRotateY(Vec3d vector, float theta) {
    double z = vector.z * MathHelper.sin(theta) + vector.x * MathHelper.cos(theta);
    double x = vector.z * MathHelper.cos(theta) - vector.x * MathHelper.sin(theta);
    return new Vec3d(x, vector.y, z);
  }

  public static Vector3f vectorRotateY(Vector3f vector, float theta) {
    float z = vector.z() * MathHelper.sin(theta) + vector.x() * MathHelper.cos(theta);
    float x = vector.z() * MathHelper.cos(theta) - vector.x() * MathHelper.sin(theta);
    return new Vector3f(x, vector.y(), z);
  }

  public static Vec3d lerp(Vec3d v0, Vec3d v1, float delta) {
    double x = MathHelper.lerp(delta, v0.x, v1.x);
    double y = MathHelper.lerp(delta, v0.y, v1.y);
    double z = MathHelper.lerp(delta, v0.z, v1.z);

    return new Vec3d(x, y, z);
  }

  public static float lerp(float a, float b, float delta) {
    return MathHelper.lerp(delta, a, b);
  }

  public static float easeInOutQuad(float a, float b, float delta) {
    delta = delta < 0.5F
      ? 2.0F * delta * delta
      : 1.0F - (-2.0F * delta + 2.0F) * (-2.0F * delta + 2.0F) * 0.5F;
    return MathHelper.lerp(delta, a, b);
  }

  // https://easings.net/#easeOutBounce
  public static float easeOutBounce(float a, float b, float delta) {
    var n1 = 7.5625F;
    var d1 = 2.75F;

    if (delta < 1F / d1) {
      return lerp(a, b, n1 * delta * delta);
    } else if (delta < 2F / d1) {
      return lerp(a, b, n1 * (delta -= 1.5F / d1) * delta + 0.75F);
    } else if (delta < 2.5F / d1) {
      return lerp(a, b, n1 * (delta -= 2.25F / d1) * delta + 0.9375F);
    }
    return lerp(a, b, n1 * (delta -= 2.625F / d1) * delta + 0.984375F);
  }

  // https://easings.net/#easeOutBack
  public static float easeOutBack(float a, float b, float delta) {
    var c1 = 1.70158F;
    var c3 = c1 + 1;

    return lerp(a, b, 1 + c3 * (float) Math.pow(delta - 1, 3) + c1 * (float) Math.pow(delta - 1, 2));
  }

  public static float easeOutQuad(float a, float b, float delta) {
    return lerp(a, b, 1 - (1 - delta) * (1 - delta));
  }

  public static float radians(float degrees) {
    return degrees * RADIANS_PER_DEGREE;
  }

  public static Vec3i getClosestMultiplePosition(Vec3i pos, int multiple) {
    int x = pos.getX();
    int z = pos.getZ();

    x = Math.round(x / (float) multiple) * multiple;
    z = Math.round(z / (float) multiple) * multiple;

    return new Vec3i(x, 0, z);
  }

  public static class PosRotScale {
    private final Vector3f pos;
    private final Vector3f rot;
    private final Vector3f scale;

    private PosRotScale(Vector3f pos, Vector3f rot, Vector3f scale) {
      this.pos = pos;
      this.rot = rot;
      this.scale = scale;
    }

    public static PosRotScale ofRadians(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
      return ofRadians(new Vector3f(px, py, pz), new Vector3f(rx, ry, rz), new Vector3f(sx, sy, sz));
    }

    public static PosRotScale ofDegrees(Vector3f pos, Vector3f rot, Vector3f scale) {
      rot.mul(RADIANS_PER_DEGREE);
      return ofRadians(pos, rot, scale);
    }

    public static PosRotScale ofDegrees(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
      return ofDegrees(new Vector3f(px, py, pz), new Vector3f(rx, ry, rz), new Vector3f(sx, sy, sz));
    }

    public static PosRotScale ofRadians(Vector3f pos, Vector3f rot, Vector3f scale) {
      return new PosRotScale(pos, rot, scale);
    }

    public void apply(MatrixStack matrices) {
      matrices.translate(this.getPos().x, this.getPos().y, this.getPos().z);
      matrices.multiply(new Quaternionf().rotationZYX(this.getRot().z, this.getRot().y, this.getRot().x));
      matrices.scale(this.getScale().x, this.getScale().y, this.getScale().z);
    }

    public Vector3f getPos() {
      return this.pos;
    }

    public Vector3f getRot() {
      return this.rot;
    }

    public Vector3f getScale() {
      return this.scale;
    }
  }
}
