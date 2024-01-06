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
import static org.joml.Math.clamp;

public class TimedDashGoal<E extends HostileEntity> extends TimedActionGoal<E> {
  protected final TimedDashSettings settings;

  protected Entity target;
  protected Vec3d direction;
  protected double targetDistance;
  protected double distanceTravelled;
  protected Vec3d targetPos;
  private final List<Integer> attackedIds = new ArrayList<>();
  private boolean hasLanded = false;

  public TimedDashGoal(E entity, TimedDashSettings settings, Predicate<E> predicate) {
    super(entity, settings, predicate);
    this.settings = settings;
    setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  public TimedDashGoal(E entity, Consumer<TimedDashSettings> settingsConsumer, Predicate<E> predicate) {
    this(entity, TimedActionSettings.edit(new TimedDashSettings(), settingsConsumer), predicate);
  }

  @Override
  public boolean canStart() {
    target = entity.getTarget();
    return target != null && super.canStart() && (!settings.onGround || entity.getY() >= target.getY());
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
    if (ticks() <= settings.alignTick) {
      targetPos = target.getPos().add(0.0D, target.getHeight() * 0.5D, 0.0D);
      Vec3d diff = targetPos.subtract(entity.getPos()).multiply(1.0D, settings.onGround ? 0.0D : 1.0D, 1.0D);
      direction = diff.normalize();
      targetDistance = clamp(settings.minDistance, settings.maxDistance, diff.length() + settings.overshoot);
      if (settings.rotate) {
        lookAtTarget();
      }
    }
    if (settings.particle != null) {
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
    Vec3d entityPos = entity.getPos().add(0.0D, entity.getHeight() * 0.75F, 0.0D);
    Vec3d diff = targetPos.subtract(entityPos);
    Vec3d norm = diff.normalize();
    for (float i = 0; i < diff.length(); i += 0.1F) {
      Vec3d particlePos = entityPos.add(norm.multiply(i));
      if (entity.getWorld() instanceof ServerWorld serverWorld && entity.getRandom().nextFloat() < 0.1F) {
        serverWorld.spawnParticles(settings.particle, particlePos.x, particlePos.y, particlePos.z, 1, 0.1D, 0.1D, 0.1D, 0.01D);
      }
    }
  }

  @Override
  protected void runAction() {
    if (settings.onGround) {
      var velY = max(settings.jumpHeight, targetDistance * settings.jumpHeight * 0.01);
      entity.addVelocity(0.0, velY, 0.0);
    }
  }

  @Override
  protected void release() {
    if (shouldSlowDown()) {
      if (settings.landSound != null && !hasLanded) {
        playSound(settings.landSound);
        settings.onLand.run();
        hasLanded = true;
      }
      entity.setVelocity(entity.getVelocity().multiply(0.8D, 1.0D, 0.8D));
      return;
    }

    entity.setVelocity(
      entity.getVelocity()
        .multiply(0, settings.onGround ? 1 : 0, 0)
        .add(direction.multiply(settings.speed))
    );
    distanceTravelled += settings.speed;
    List<Entity> entitiesInRange = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(settings.margin));
    for (Entity e : entitiesInRange) {
      if (e instanceof LivingEntity && !(e instanceof HostileEntity) && !attackedIds.contains(e.getId())) {
        e.damage(entity.getDamageSources().mobAttack(entity), settings.damage);
        attackedIds.add(e.getId());
      }
    }
  }

  @Override
  public void stop() {
    super.stop();
    if (!hasLanded) {
      settings.onLand.run();
      playSound(settings.landSound);
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
    public double jumpHeight = 0.0D;
    public int alignTick = 0;
    public double minDistance = 0.0;
    public double overshoot = 0.0;
    public double maxDistance = 128.0;
    public ParticleEffect particle = null;
    public SoundEvent landSound = null;
    public Runnable onLand = () -> {
    };
  }
}
