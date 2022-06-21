package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.World;

public class RunnerEntity extends MineCellsEntity {

    public static final TrackedData<Boolean> TIMED_ATTACK_CHARGING = DataTracker.registerData(RunnerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> TIMED_ATTACK_RELEASING = DataTracker.registerData(RunnerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public AnimationProperty bendAngle = new AnimationProperty(0.0F);
    public AnimationProperty swingProgress = new AnimationProperty(0.0F);
    private int attackCooldown;

    public RunnerEntity(EntityType<RunnerEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TIMED_ATTACK_CHARGING, false);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        TimedActionGoal<RunnerEntity> timedAttackGoal = new RunnerTimedAttackGoal(this,
            () -> this.attackCooldown,
            (cooldown) -> this.attackCooldown = cooldown,
            this::switchState,
            100,
            15,
            25,
            1.0F);

        this.goalSelector.add(1, new WalkTowardsTargetGoal(this, 1.5F, false, 1.0D));
        this.goalSelector.add(0, timedAttackGoal);
    }

    @Override
    protected void decrementCooldowns() {
        this.attackCooldown = Math.max(0, this.attackCooldown - 1);
        System.out.println(this.attackCooldown);
    }

    public static DefaultAttributeContainer.Builder createRunnerAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    public void switchState(TimedActionGoal.State state, boolean bool) {
        if (state == TimedActionGoal.State.CHARGE) {
            this.dataTracker.set(TIMED_ATTACK_CHARGING, bool);
        }
    }

    public static class RunnerTimedAttackGoal extends TimedActionGoal<RunnerEntity> {
        protected Entity target;

        public RunnerTimedAttackGoal(RunnerEntity entity,
                                     CooldownGetter cooldownGetter,
                                     CooldownSetter cooldownSetter,
                                     StateSetter stateSetter,
                                     int defaultCooldown,
                                     int actionTick,
                                     int length,
                                     float chance) {
            super(entity, cooldownGetter, cooldownSetter, stateSetter, defaultCooldown, actionTick, length, chance);
        }

        @Override
        public boolean canStart() {
            this.target = this.entity.getTarget();

            return this.target != null
                && this.entity.distanceTo(this.target) <= 4.0D
                && super.canStart();
        }

        @Override
        protected void runAction() {
            if (this.target.isAlive() && this.entity.distanceTo(this.target) < 4.0D) {
                this.entity.tryAttack(this.target);
            }
        }
    }
}
