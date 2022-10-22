package com.github.mim1q.minecells.util;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

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

  public static float lerp(float a, float b, float delta) {
    return MathHelper.lerp(delta, a, b);
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

  // Thank you, GolamMazid Sajib!
  // https://stackoverflow.com/a/61231766
  public static Vec3i getSpiralPosition(int i) {
    i += 1;
    int k = (int) Math.ceil((Math.sqrt(i) - 1) / 2);
    int t = 2 * k + 1;
    int m = t * t;
    t -= 1;
    if (i >= m - t) {
      return new Vec3i(k - (m - i), 0, -k);
    }
    m -= t;
    if (i >= m - t) {
      return new Vec3i(-k, 0, -k + (m - i));
    }
    m -= t;
    if (i >= m - t) {
      return new Vec3i(-k + (m - i), 0, k);
    }
    return new Vec3i(k, 0, k - (m - i - t));
  }

  // Thank you, MBo!
  // https://stackoverflow.com/a/9971465/20281585
  public static int getSpiralIndex(int x, int z) {
    if (z * z >= x * x) {
      int p = 4 * z * z - z - x;
      if (z < x) {
        return p - 2 * (z - x);
      }
      return p;
    }
    int p = 4 * x * x - z - x;
    if (z < x) {
      return p + 2 * (z - x);
    }
    return p;
  }

  public static Vec3i getClosestMultiplePosition(Vec3i pos, int multiple) {
    int x = pos.getX();
    int z = pos.getZ();

    x = x + multiple / 2 - x % multiple;
    z = z + multiple / 2 - z % multiple;

    return new Vec3i(x, 0, z);
  }

  public static class PosRotScale {
    private final Vec3f pos;
    private final Vec3f rot;
    private final Vec3f scale;

    private PosRotScale(Vec3f pos, Vec3f rot, Vec3f scale) {
      this.pos = pos;
      this.rot = rot;
      this.scale = scale;
    }

    public static PosRotScale ofRadians(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
      return ofRadians(new Vec3f(px, py, pz), new Vec3f(rx, ry, rz), new Vec3f(sx, sy, sz));
    }

    public static PosRotScale ofDegrees(Vec3f pos, Vec3f rot, Vec3f scale) {
      rot.scale(RADIANS_PER_DEGREE);
      return ofRadians(pos, rot, scale);
    }

    public static PosRotScale ofDegrees(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
      return ofDegrees(new Vec3f(px, py, pz), new Vec3f(rx, ry, rz), new Vec3f(sx, sy, sz));
    }

    public static PosRotScale ofRadians(Vec3f pos, Vec3f rot, Vec3f scale) {
      return new PosRotScale(pos, rot, scale);
    }

    public void apply(MatrixStack matrices) {
      matrices.translate(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());

      matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(this.getRot().getZ()));
      matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(this.getRot().getY()));
      matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(this.getRot().getX()));

      matrices.scale(this.getScale().getX(), this.getScale().getY(), this.getScale().getZ());
    }

    public Vec3f getPos() {
      return this.pos;
    }

    public Vec3f getRot() {
      return this.rot;
    }

    public Vec3f getScale() {
      return this.scale;
    }
  }
}
