package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.github.mim1q.minecells.util.MathUtils.radians;

public class SweeperEntity extends MineCellsEntity {
  private Vec3d gauntletPosition = this.getPos();
  private static final Vec3d GAUNTLET_OFFSET = new Vec3d(-0.75D, 0.2D, -0.9D);

  public SweeperEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new SweeperMoveControl(this);
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    goalSelector.add(0, new MeleeAttackGoal(this, 1.0D, false));
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient) {
      var lastGauntletPosition = gauntletPosition;
      gauntletPosition = getPos().add(MathUtils.vectorRotateY(GAUNTLET_OFFSET, radians(this.getBodyYaw())));
      spawnGauntletParticles(lastGauntletPosition.squaredDistanceTo(gauntletPosition) > 0.0001D);
    }
  }

  private void spawnGauntletParticles(boolean moving) {
    var chance = moving ? 1F : 0.1F;
    if (random.nextFloat() < chance) {
      var randomOffset = new Vec3d(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).multiply(0.5);
      var pos = gauntletPosition.add(randomOffset);
      getWorld().addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0.01, 0);
    }
    if (moving) {
      var randomOffset = new Vec3d(random.nextDouble() - 0.5, 0.0, random.nextDouble() - 0.5).multiply(0.5);
      var pos = gauntletPosition.add(randomOffset);
      getWorld().addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0, 0.01, 0);
    }
  }

  public static DefaultAttributeContainer.Builder createSweeperAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.19D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 18.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D);
  }

  private static class SweeperMoveControl extends MoveControl {
    public SweeperMoveControl(MobEntity entity) {
      super(entity);
    }

    @Override
    public void tick() {
      if (this.entity.age % 30 == 0 && this.state == State.MOVE_TO) {
        this.entity.playSound(SoundEvents.BLOCK_GRINDSTONE_USE, 1.0F, 1.0F);
      }
      if (this.entity.age % 30 <= 15) {
        super.tick();
      } else {
        this.entity.setMovementSpeed(0F);
      }
    }
  }
}
