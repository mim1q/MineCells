package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedTeleportGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

import static com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal.State.CHARGE;

public class RunnerEntity extends MineCellsEntity {

    public static final TrackedData<Boolean> TIMED_ATTACK_CHARGING = DataTracker.registerData(RunnerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> TIMED_ATTACK_RELEASING = DataTracker.registerData(RunnerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> TELEPORT_CHARGING = DataTracker.registerData(RunnerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public AnimationProperty bendAngle = new AnimationProperty(0.0F);
    public AnimationProperty swingChargeProgress = new AnimationProperty(0.0F);
    public AnimationProperty swingReleaseProgress = new AnimationProperty(0.0F);
    private int attackCooldown = 0;
    private int teleportCooldown = 0;

    public RunnerEntity(EntityType<RunnerEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TIMED_ATTACK_CHARGING, false);
        this.dataTracker.startTracking(TIMED_ATTACK_RELEASING, false);
        this.dataTracker.startTracking(TELEPORT_CHARGING, false);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(1, new WalkTowardsTargetGoal(this, 1.2F, false));
        this.goalSelector.add(0, new RunnerTimedAttackGoal.Builder(this)
            .cooldownSetter((cooldown) -> this.attackCooldown = cooldown)
            .cooldownGetter(() -> this.attackCooldown)
            .stateSetter(this::switchAttackState)
            .chargeSound(SoundRegistry.GRENADIER_CHARGE)
            .releaseSound(SoundRegistry.SWIPE)
            .defaultCooldown(40)
            .actionTick(12)
            .length(25)
            .build()
        );
        this.goalSelector.add(0, new TimedTeleportGoal.Builder(this)
            .cooldownSetter((cooldown) -> this.teleportCooldown = cooldown)
            .cooldownGetter(() -> this.teleportCooldown)
            .stateSetter(this::switchTeleportState)
            .defaultCooldown(40)
            .actionTick(20)
            .length(30)
            .build()
        );
    }

    @Override
    public void tick() {
        super.tick();
        if (this.dataTracker.get(TELEPORT_CHARGING) && this.world.isClient()) {
            for (int i = 0; i < 5; i++) {
                ParticleUtils.addParticle(
                    (ClientWorld) this.world,
                    ParticleRegistry.CHARGE,
                    this.getPos().add(0.0D, this.getHeight() * 0.5F, 0.0D),
                    Vec3d.ZERO
                );
            }
        }
    }

    @Override
    protected void decrementCooldowns() {
        this.attackCooldown = Math.max(0, this.attackCooldown - 1);
        this.teleportCooldown = Math.max(0, this.teleportCooldown - 1);
    }

    public static DefaultAttributeContainer.Builder createRunnerAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0D);
    }

    public void switchAttackState(TimedActionGoal.State state, boolean value) {
        switch (state) {
            case CHARGE -> this.dataTracker.set(TIMED_ATTACK_CHARGING, value);
            case RELEASE -> this.dataTracker.set(TIMED_ATTACK_RELEASING, value);
        }
    }

    public void switchTeleportState(TimedActionGoal.State state, boolean value) {
        if (state == CHARGE) {
            this.dataTracker.set(TELEPORT_CHARGING, value);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("attackCooldown", this.attackCooldown);
        nbt.putInt("teleportCooldown", this.teleportCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.attackCooldown = nbt.getInt("attackCooldown");
        this.teleportCooldown = nbt.getInt("teleportCooldown");
    }

    public static class RunnerTimedAttackGoal extends TimedActionGoal<RunnerEntity> {
        protected Entity target;

        public RunnerTimedAttackGoal(Builder builder) {
            super(builder);
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            this.target = this.entity.getTarget();

            return this.target != null && this.target.isAlive() && this.target.isAttackable()
                && this.entity.distanceTo(this.target) < 1.5D
                && super.canStart();
        }

        @Override
        public void tick() {
            this.entity.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 0.001D);
            this.entity.getLookControl().lookAt(target);
            super.tick();
        }

        @Override
        protected void runAction() {
            if (this.target.isAlive() && this.entity.distanceTo(this.target) < 3.5D) {
                this.entity.tryAttack(this.target);
            }
        }

        public static class Builder extends TimedActionGoal.Builder<RunnerEntity, Builder> {

            public Builder(RunnerEntity entity) {
                super(entity);
            }

            public RunnerTimedAttackGoal build() {
                this.check();
                return new RunnerTimedAttackGoal(this);
            }
        }
    }
}
