package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GrenadierEntity extends MineCellsEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public GrenadierEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "ballsController", 0, this::ballsPredicate));
        data.addAnimationController(new AnimationController<>(this, "movementPredicate", 5, this::movementPredicate));
    }

    private PlayState movementPredicate(AnimationEvent<GrenadierEntity> event) {
        boolean isMoving = MathHelper.abs(event.getLimbSwingAmount()) > 0.05f;

        if (this.getAttackState().equals("none") && isMoving) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("grenadier.walk"));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("grenadier.idle"));
        }

        return PlayState.CONTINUE;
    }

    private PlayState ballsPredicate(AnimationEvent<GrenadierEntity> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("grenadier.bombs"));
        return PlayState.CONTINUE;
    }

    @Override
    public void initGoals() {
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0d));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0d));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.25D, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age % 50 == 0 && !this.world.isClient()) {
            GrenadeEntity.spawnGrenade(this.world, GrenadeEntity.Variants.BIG, this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static DefaultAttributeContainer.Builder createGrenadierAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0d)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0d);
    }
}
