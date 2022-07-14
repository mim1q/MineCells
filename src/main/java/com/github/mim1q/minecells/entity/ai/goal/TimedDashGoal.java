package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class TimedDashGoal<E extends HostileEntity> extends TimedActionGoal<E> {

  private final float speed;
  protected final float damage;

  protected Entity target;
  protected Vec3d direction;
  protected double targetDistance;
  protected double distanceTravelled;
  private float yaw = 0.0F;

  public TimedDashGoal(Builder<E> builder) {
    super(builder);
    this.speed = builder.speed;
    this.damage = builder.damage;
    this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  @Override
  public boolean canStart() {
    this.target = this.entity.getTarget();
    return super.canStart()
      && this.target != null;
  }

  @Override
  public void start() {
    super.start();
    this.distanceTravelled = 0;
    this.targetDistance = 0;
  }

  @Override
  public boolean shouldContinue() {
    return super.shouldContinue()
      && this.distanceTravelled <= this.targetDistance
      && this.target != null;
  }

  @Override
  public void tick() {
    super.tick();
  }

  @Override
  protected void charge() {
    this.entity.getLookControl().lookAt(this.target, 20.0F, 20.0F);
    this.yaw = this.entity.getYaw();
  }

  @Override
  protected void runAction() {
    Vec3d diff = this.target.getCameraPosVec(1.0F).subtract(this.entity.getPos());
    this.direction = diff.normalize();
    this.targetDistance = diff.length();
  }

  @Override
  protected void release() {
    this.entity.setYaw(this.yaw);
    this.entity.setVelocity(this.direction.multiply(this.speed));
    this.distanceTravelled += this.speed;
    List<Entity> entitiesInRange = this.entity.world.getOtherEntities(this.entity, this.entity.getBoundingBox());
    for (Entity e : entitiesInRange) {
      if (e instanceof LivingEntity) {
        e.damage(DamageSource.mob(this.entity), this.damage);
      }
    }
  }

  public static class Builder<E extends HostileEntity> extends TimedActionGoal.Builder<E, Builder<E>> {

    public float speed;
    public float damage;

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

    @Override
    public TimedDashGoal<E> build() {
      this.check();
      return new TimedDashGoal<>(this);
    }
  }
}
