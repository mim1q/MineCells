package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.AnimatedAttackGoal;
import com.github.mim1q.minecells.entity.ai.goal.JumpingZombieJumpAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IAnimatedAttackEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;


public class JumpingZombieEntity extends HostileEntity implements IAnimatable, IAnimatedAttackEntity {
    AnimationFactory factory = new AnimationFactory(this);

    public JumpingZombieEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    public static final TrackedData<String> ATTACK_STATE = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> JUMP_COOLDOWN_TICKS = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public void initGoals() {
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.6d));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.6d));

        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, false));

        this.goalSelector.add(2, new JumpingZombieMeleeAttackGoal(this, "melee"));
        this.goalSelector.add(1, new JumpingZombieJumpAttackGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(ATTACK_STATE, "none");
        this.dataTracker.startTracking(JUMP_COOLDOWN_TICKS, 0);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movementController", 5, this::movementPredicate));
        data.addAnimationController(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        boolean isMoving = event.getLimbSwingAmount() > 0.05f || event.getLimbSwingAmount() < -0.05f;

        if(isMoving)
            if(this.getAttackState().equals("none"))
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.walking"));
            else
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.walking_legs"));
        else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.idle"));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {

        if(this.getAttackState().equals("melee")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.attack.melee"));
            return PlayState.CONTINUE;
        }
        else if(this.getAttackState().equals("jump")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.attack.jump"));
            return PlayState.CONTINUE;
        }

        if(event.getController().getAnimationState() == AnimationState.Stopped) {
            event.getController().markNeedsReload();
            return PlayState.STOP;
        }
        return PlayState.STOP;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getJumpCooldownTicks() > 0) //&& !this.getAttackState().equals("jump"))
            this.setJumpCooldownTicks(this.getJumpCooldownTicks() - 1);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static DefaultAttributeContainer.Builder createJumpingZombieAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0d)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0d);
    }

    public void setJumpCooldownTicks(int ticks) {
        this.dataTracker.set(JUMP_COOLDOWN_TICKS, ticks);
    }

    public int getJumpCooldownTicks() {
        return this.dataTracker.get(JUMP_COOLDOWN_TICKS);
    }

    @Override
    public int getAttackTickCount(String attackName) {
        return switch (attackName) {
            case "melee" -> 15;
            case "jump" -> 27;
            default -> 0;
        };
    }

    @Override
    public void setAttackState(String attackName) {
        this.dataTracker.set(ATTACK_STATE, attackName);
    }

    @Override
    public String getAttackState() {
        return this.dataTracker.get(ATTACK_STATE);
    }

    @Override
    public int getAttackCooldown(String attackName) {
        return switch (attackName) {
            case "melee" -> 20;
            case "jump" -> 300;
            default -> 0;
        };
    }

    @Override
    public int getAttackLength(String attackName) {
        return switch (attackName) {
            case "melee" -> 20;
            case "jump" -> 45;
            default -> 0;
        };
    }

    @Override
    public void stopAnimations() {
        this.setAttackState("none");
    }

    public static class JumpingZombieMeleeAttackGoal extends AnimatedAttackGoal<JumpingZombieEntity> {

        public JumpingZombieMeleeAttackGoal(JumpingZombieEntity entity, String attackName) {
            super(entity, attackName);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.entity.getJumpCooldownTicks() < 100;
        }
    }
}
