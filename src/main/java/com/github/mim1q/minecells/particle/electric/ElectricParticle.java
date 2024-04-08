package com.github.mim1q.minecells.particle.electric;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ElectricParticle extends SpriteBillboardParticle {
  private final SpriteProvider spriteProvider;
  private final Vec3d direction;
  private final float pitch;
  private final float yaw;
  private float roll;
  private final int length;
  private final int color;
  private final float size;
  private final boolean isMainBranch;

  private ElectricParticle(
    SpriteProvider spriteProvider,
    ClientWorld clientWorld,
    double x,
    double y,
    double z,
    Vec3d direction,
    int length,
    int color,
    float size,
    boolean isMainBranch
  ) {
    super(clientWorld, x, y, z);
    this.spriteProvider = spriteProvider;
    this.direction = direction.normalize();
    this.length = length;
    this.color = color;
    this.maxAge = 6;
    this.size = size;
    this.isMainBranch = isMainBranch;

    this.yaw = (float) Math.asin(-this.direction.y);
    this.pitch = (float) Math.atan2(this.direction.x, this.direction.z);
    this.roll = clientWorld.random.nextFloat() * 2 * MathHelper.PI;

    this.setSprite(spriteProvider.getSprite(clientWorld.random));
  }

  @Override
  public void tick() {
    super.tick();
    this.setSprite(this.spriteProvider.getSprite(world.random));
    this.roll += (world.random.nextFloat() * 0.5f - 0.25f) * MathHelper.PI;

    if (this.length == 0) {
      return;
    }

    if (this.age == 1) {
      for (int i = 0; i < 3; i++) {
        if (world.random.nextFloat() < 0.5f) this.addSideBranch();
      }
      this.addNextMainBranch();
    }
  }

  private void addSideBranch() {
    var delta = world.random.nextFloat();
    var newPos = new Vec3d(this.x, this.y, this.z).add(this.direction.multiply(delta * size));
    var randomDirection = this.direction.addRandom(world.random, 2.0f);
    var randomLength = Math.min(this.length - 1, world.random.nextInt(3));

    world.addParticle(
      new ElectricParticleEffect(randomDirection, randomLength, this.color, this.size * 0.5f, false),
      newPos.x,
      newPos.y,
      newPos.z,
      0.0D,
      0.0D,
      0.0D
    );
  }

  private void addNextMainBranch() {
    var newDirection = this.direction.addRandom(world.getRandom(), isMainBranch ? 0.1f : 2.0f);

    var newPos = new Vec3d(this.x, this.y, this.z).add(this.direction.multiply(size));

    world.addParticle(
      new ElectricParticleEffect(newDirection, this.length - 1, this.color, this.size, this.isMainBranch),
      newPos.x,
      newPos.y,
      newPos.z,
      0.0D,
      0.0D,
      0.0D
    );
  }

  @Override
  public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
    var lifetimeDelta = (this.age + tickDelta) / this.maxAge;
    var alpha = 255;

    if (lifetimeDelta < 0.2) {
      alpha = (int) (lifetimeDelta / 0.2 * 255);
    } else if (lifetimeDelta > 0.8) {
      alpha = (int) ((1 - lifetimeDelta) / 0.2 * 255);
    }

    alpha = MathHelper.clamp(alpha, 0, 255);

    var px = (float) (MathHelper.lerp(tickDelta, prevPosX, x) - camera.getPos().x);
    var py = (float) (MathHelper.lerp(tickDelta, prevPosY, y) - camera.getPos().y);
    var pz = (float) (MathHelper.lerp(tickDelta, prevPosZ, z) - camera.getPos().z);

    var quaternionf = new Quaternionf();
    quaternionf.rotateY(pitch);
    quaternionf.rotateX(yaw);
    quaternionf.rotateZ(roll);

    var vector3fs = new Vector3f[]{
      new Vector3f(-0.5f, 0f, 0.0f),
      new Vector3f(-0.5f, 0f, 1.0f),
      new Vector3f(0.5f, 0f, 1.0f),
      new Vector3f(0.5f, 0f, 0.0f)
    };

    for (int j = 0; j < 4; ++j) {
      var vector3f = vector3fs[j];
      vector3f.rotate(quaternionf);
      vector3f.mul(size);
      vector3f.add(px, py, pz);
    }

    float k = this.getMinU();
    float l = this.getMaxU();
    float m = this.getMinV();
    float n = this.getMaxV();

    var u = new float[]{l, l, k, k};
    var v = new float[]{n, m, m, n};

    int o = 0xF000F0;

    var r = this.color >> 16 & 0xFF;
    var g = this.color >> 8 & 0xFF;
    var b = this.color & 0xFF;

    for (int i = 0; i <= 3; ++i) {
      vertexConsumer.vertex(vector3fs[i].x(), vector3fs[i].y(), vector3fs[i].z()).texture(u[i], v[i]).color(r, g, b, alpha).light(o).next();
    }
    for (int i = 3; i >= 0; --i) {
      vertexConsumer.vertex(vector3fs[i].x(), vector3fs[i].y(), vector3fs[i].z()).texture(u[i], v[i]).color(r, g, b, alpha).light(o).next();
    }

  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }

  public record Factory(
    SpriteProvider spriteProvider
  ) implements ParticleFactory<ElectricParticleEffect> {
    @Override
    public Particle createParticle(
      ElectricParticleEffect parameters,
      ClientWorld world,
      double x, double y, double z,
      double velocityX, double velocityY, double velocityZ
    ) {
      return new ElectricParticle(
        spriteProvider,
        world,
        x,
        y,
        z,
        parameters.direction(),
        parameters.length(),
        parameters.color(),
        parameters.size(),
        parameters.isMainBranch()
      );
    }
  }
}
