package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.Math.max;

public class TimedDashGoal<E extends HostileEntity> extends TimedActionGoal<E> {
  private final float speed;
  protected final float damage;
  protected final boolean rotate;
  protected final double margin;
  protected final boolean onGround;
  protected final int alignTick;
  protected final ParticleEffect particle;
  protected final SoundEvent landSound;

  protected Entity target;
  protected Vec3d direction;
  protected double targetDistance;
  protected double distanceTravelled;
  protected Vec3d targetPos;
  private final List<Integer> attackedIds = new ArrayList<>();
  private boolean hasLanded = false;

  public TimedDashGoal(E entity, TimedDashSettings settings, Predicate<E> predicate) {
    super(entity, settings, predicate);
    damage = settings.damage;
    speed = settings.speed;
    rotate = settings.rotate;
    margin = settings.margin;
    onGround = settings.onGround;
    alignTick = settings.alignTick;
    particle = settings.particle;
    landSound = settings.landSound;
    setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  public TimedDashGoal(E entity, Consumer<TimedDashSettings> settingsConsumer, Predicate<E> predicate) {
    this(entity, TimedActionSettings.edit(new TimedDashSettings(), settingsConsumer), predicate);
  }
  
  @Override
  public boolean canStart() {
    target = entity.getTarget();
    return target != null && super.canStart() && (!onGround || entity.getY() >= target.getY());
  }

  @Override
  public void start() {
    super.start();
    distanceTravelled = 0;
    targetDistance = 0;
    targetPos = target.getPos();
    attackedIds.clear();
    hasLanded = false;
  }

  @Override
  public boolean shouldContinue() {
    return super.shouldContinue()
      && target != null;
  }

  @Override
  protected void charge() {
    if (ticks() <= alignTick) {
      targetPos = target.getPos().add(0.0D, target.getHeight() * 0.5D, 0.0D);
      Vec3d diff = targetPos.subtract(entity.getPos()).multiply(1.0D, 0.0D, 1.0D);
      direction = diff.normalize();
      targetDistance = diff.length();
      if (rotate) {
        lookAtTarget();
      }
    }
    if (particle != null) {
      spawnParticles();
    }
  }

  protected void lookAtTarget() {
    this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.01F);
    this.entity.move(MovementType.SELF, this.direction.multiply(0.01F));
    this.entity.getLookControl().lookAt(this.target, 360.0F, 360.0F);
    this.entity.getNavigation().stop();
  }

  protected void spawnParticles() {
    super.charge();
    Vec3d entityPos = entity.getPos().add(0.0D, entity.getHeight() * 0.5F, 0.0D);
    Vec3d diff = targetPos.subtract(entityPos);
    Vec3d norm = diff.normalize();
    for (float i = 0; i < diff.length(); i += 0.1F) {
      Vec3d particlePos = entityPos.add(norm.multiply(i));
      if (entity.getWorld() instanceof ServerWorld serverWorld && entity.getRandom().nextFloat() < 0.1F) {
        serverWorld.spawnParticles(particle, particlePos.x, particlePos.y, particlePos.z, 1, 0.1D, 0.1D, 0.1D, 0.01D);
      }
    }
  }

  @Override
  protected void runAction() {
    if (!onGround) {
      var velY = max(0.6, targetDistance * 0.01);
      entity.addVelocity(0.0, velY, 0.0);
    }
  }

  @Override
  protected void release() {
    if (shouldSlowDown()) {
      if (landSound != null && !hasLanded) {
        entity.playSound(landSound, 1.0F, 1.0F);
        hasLanded = true;
      }
      entity.setVelocity(entity.getVelocity().multiply(0.8D, 1.0D, 0.8D));
      return;
    }

    entity.setVelocity(entity.getVelocity().multiply(0, 1, 0).add(direction.multiply(speed)));
    distanceTravelled += speed;
    List<Entity> entitiesInRange = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(margin));
    for (Entity e : entitiesInRange) {
      if (e instanceof LivingEntity && !(e instanceof HostileEntity) && !attackedIds.contains(e.getId())) {
        e.damage(entity.getDamageSources().mobAttack(entity), damage);
        attackedIds.add(e.getId());
      }
    }
  }

  @Override
  public void stop() {
    super.stop();
    if (!hasLanded) {
      entity.playSound(landSound, 1.0F, 1.0F);
      hasLanded = true;
    }
  }

  protected boolean shouldSlowDown() {
    return distanceTravelled > targetDistance;
  }

  public static class TimedDashSettings extends TimedActionSettings {
    public float speed = 1.0F;
    public float damage = 10.0F;
    public boolean rotate = true;
    public double margin = 0.0D;
    public boolean onGround = false;
    public int alignTick = 0;
    public ParticleEffect particle = null;
    public SoundEvent landSound = null;
  }
}
