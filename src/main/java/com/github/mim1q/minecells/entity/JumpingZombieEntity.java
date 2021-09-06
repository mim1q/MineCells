package com.github.mim1q.minecells.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;

public class JumpingZombieEntity extends MineCellsEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public JumpingZombieEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;

        validStates.add("melee_attack");
        validStates.add("jump_attack");
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(10, new WanderAroundFarGoal(this, 1.0d));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));

        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this).setGroupRevenge());

        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(1, new JumpingZombieMeleeAttackGoal(this, 1.0d));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    private<E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {

        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = limbSwingAmount < -0.075f || limbSwingAmount > 0.075f;

        if (isMoving) {
            if(getState().equals("none")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.walking", true));
            }
            else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.walking_legs", true));
            }
        }
        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.idle", true));
        }
        return PlayState.CONTINUE;
    }

    private<E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {
        if(getState().equals("melee_attack")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.attack.melee", true));
        } else if(getState().equals("jump_attack")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.attack.jump", true));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movementController", 5, this::movementPredicate));
        AnimationController<JumpingZombieEntity> attackController = new AnimationController<>(this, "attackController", 5, this::attackPredicate);
        data.addAnimationController(attackController);

        attackController.registerSoundListener(this::soundListener);
        attackController.registerCustomInstructionListener(this::instructionListener);
    }

    private<E extends IAnimatable> void soundListener(SoundKeyframeEvent<E> event) {

    }

    private<E extends IAnimatable> void instructionListener(CustomInstructionKeyframeEvent<E> event) {

    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static DefaultAttributeContainer.Builder createJumpingZombieAttributes() {

        return PathAwareEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15d)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 15.0d)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d);
    }

    private static class JumpingZombieMeleeAttackGoal extends MeleeAttackGoal {
        JumpingZombieMeleeAttackGoal(JumpingZombieEntity entity, double speed) {
            super(entity, speed, false);
        }
    }




}
