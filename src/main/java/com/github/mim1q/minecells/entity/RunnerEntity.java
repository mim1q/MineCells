package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;

public class RunnerEntity extends MineCellsEntity {

    public AnimationProperty bendAngle = new AnimationProperty(0.0F);

    public RunnerEntity(EntityType<RunnerEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.5D, true));
    }

    public static DefaultAttributeContainer.Builder createRunnerAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }
}
