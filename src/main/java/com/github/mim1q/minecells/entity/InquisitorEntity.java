package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShootGoal;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import com.github.mim1q.minecells.entity.projectile.MagicOrbEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class InquisitorEntity extends MineCellsEntity implements IShootEntity {

    // Animation data

    @Environment(EnvType.CLIENT)
    public float offset = 0.0F;
    @Environment(EnvType.CLIENT)
    public float targetOffset = 0.0F;

    private static final TrackedData<Integer> SHOOT_COOLDOWN = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public InquisitorEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(0, new InquisitorShootGoal(this, 10, 20));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOT_COOLDOWN, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(SHOOT_COOLDOWN, "shoot");
    }

    @Override
    public int getShootMaxCooldown() {
        return 50 + this.random.nextInt(50);
    }

    @Override
    public int getShootCooldown() {
        return this.dataTracker.get(SHOOT_COOLDOWN);
    }

    @Override
    public void setShootCooldown(int ticks) {
        this.dataTracker.set(SHOOT_COOLDOWN, ticks);
    }

    @Override
    public SoundEvent getShootChargeSoundEvent() {
        return SoundRegistry.INQUISITOR_CHARGE;
    }

    @Override
    public SoundEvent getShootReleaseSoundEvent() {
        return SoundRegistry.INQUISITOR_RELEASE;
    }

    public static DefaultAttributeContainer.Builder createInquisitorAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0D);
    }

    public static class InquisitorShootGoal extends ShootGoal<InquisitorEntity> {

        public InquisitorShootGoal(InquisitorEntity entity, int actionTick, int lengthTicks) {
            super(entity, actionTick, lengthTicks);
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public void start() {
            super.start();
            if (this.target != null) {
                this.entity.getLookControl().lookAt(this.target);
            }
        }

        @Override
        public void tick() {
            super.tick();
            this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.0d);
        }

        @Override
        public void shoot(LivingEntity target) {
            Vec3d targetPos = target.getPos().add(0.0D, 1.3D, 0.0D);
            Vec3d entityPos = this.entity.getPos().add(0.0D, 2.25D, 0.0D);

            Vec3d vel = targetPos.subtract(entityPos).normalize().multiply(0.8D);

            MagicOrbEntity orb = new MagicOrbEntity(EntityRegistry.MAGIC_ORB, this.entity.world);
            orb.setPosition(entityPos);
            orb.setVelocity(vel);
            orb.setOwner(this.entity);

            this.entity.world.spawnEntity(orb);
        }
    }
}
