package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TimedDashGoal<E extends HostileEntity> extends TimedActionGoal<E> {

  private final float speed;
  protected final float damage;
  protected final boolean rotate;
  protected final double margin;
  protected final boolean onGround;
  protected final int alignTick;
  protected final ParticleEffect particle;

  protected Entity target;
  protected Vec3d direction;
  protected double targetDistance;
  protected double distanceTravelled;
  protected Vec3d targetPos;
  private float yaw = 0.0F;
  private final List<Integer> attackedIds = new ArrayList<>();

  public TimedDashGoal(Builder<E> builder) {
    super(builder);
    speed = builder.speed;
    damage = builder.damage;
    rotate = builder.rotate;
    margin = builder.margin;
    onGround = builder.onGround;
    alignTick = builder.alignTick;
    particle = builder.particle;
    setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  @Override
  public boolean canStart() {
    target = entity.getTarget();
    return super.canStart()
      && target != null
      && (!onGround || entity.getY() >= target.getY());
  }

  @Override
  public void start() {
    super.start();
    distanceTravelled = 0;
    targetDistance = 0;
    targetPos = target.getPos();
    attackedIds.clear();
  }

  @Override
  public boolean shouldContinue() {
    return super.shouldContinue()
      && distanceTravelled <= targetDistance
      && target != null;
  }

  @Override
  protected void charge() {
    if (ticks() <= alignTick) {
      targetPos = target.getPos().add(0.0D, target.getHeight() * 0.5D, 0.0D);
      Vec3d diff = targetPos.subtract(entity.getPos()).multiply(1.0D, onGround ? 0.0D : 1.0D, 1.0D);
      direction = diff.normalize();
      targetDistance = diff.length();
      if (rotate) {
        entity.getLookControl().lookAt(targetPos);
        yaw = entity.getYaw();
      }
    }
    if (particle != null) {
      spawnParticles();
    }
  }

  protected void spawnParticles() {
    super.charge();
    Vec3d entityPos = this.entity.getPos().add(0.0D, 3.0D, 0.0D);
    Vec3d diff = targetPos.subtract(entityPos);
    Vec3d norm = diff.normalize();
    for (float i = 0; i < diff.length(); i += 0.1F) {
      Vec3d particlePos = entityPos.add(norm.multiply(i));
      if (entity.world instanceof ServerWorld serverWorld && entity.getRandom().nextFloat() < 0.1F) {
        serverWorld.spawnParticles(particle, particlePos.x, particlePos.y, particlePos.z, 1, 0.1D, 0.1D, 0.1D, 0.01D);
      }
    }
  }

  @Override
  protected void release() {
    if (rotate) {
      entity.setYaw(yaw);
    }
    entity.setVelocity(direction.multiply(speed));
    distanceTravelled += speed;
    List<Entity> entitiesInRange = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(margin));
    for (Entity e : entitiesInRange) {
      if (e instanceof LivingEntity && !attackedIds.contains(e.getId())) {
        e.damage(DamageSource.mob(entity), damage);
        attackedIds.add(e.getId());
      }
    }
  }

  public static class Builder<E extends HostileEntity> extends TimedActionGoal.Builder<E, Builder<E>> {

    public float speed;
    public float damage;
    public boolean rotate = true;
    public double margin = 0.0D;
    public boolean onGround = false;
    public int alignTick = 0;
    public ParticleEffect particle = null;

    public Builder(E entity) {
      super(entity);
    }

    public Builder<E> speed(float speed) {
      this.speed = speed;
      return this;
    }

    public Builder<E> damage(float damage) {
      this.damage = damage;
      return this;
    }

    public Builder<E> noRotation() {
      this.rotate = false;
      return this;
    }

    public Builder<E> margin(double margin) {
      this.margin = margin;
      return this;
    }

    public Builder<E> onGround() {
      this.onGround = true;
      return this;
    }

    public Builder<E> alignTick(int alignTick) {
      this.alignTick = alignTick;
      return this;
    }

    public Builder<E> particle(ParticleEffect particle) {
      this.particle = particle;
      return this;
    }

    @Override
    public TimedDashGoal<E> build() {
      this.check();
      return new TimedDashGoal<>(this);
    }
  }
}
