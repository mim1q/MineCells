package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.JumpingZombieMeleeAttackGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.entity.ai.goal.JumpingZombieJumpAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IAnimatedAttackEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
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
        this.ignoreCameraFrustum = true;
    }

    public static final TrackedData<String> ATTACK_STATE = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> JUMP_COOLDOWN_TICKS = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> MELEE_COOLDOWN_TICKS = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);


    @Override
    public void initGoals() {
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.6d));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.6d));

        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(2, new WalkTowardsTargetGoal(this));
        this.goalSelector.add(1, new JumpingZombieJumpAttackGoal(this));
        this.goalSelector.add(1, new JumpingZombieMeleeAttackGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(ATTACK_STATE, "none");
        this.dataTracker.startTracking(JUMP_COOLDOWN_TICKS, 50);
        this.dataTracker.startTracking(MELEE_COOLDOWN_TICKS, 50);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movementController", 10, this::movementPredicate));
        data.addAnimationController(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        boolean isMoving = event.getLimbSwingAmount() > 0.05f || event.getLimbSwingAmount() < -0.05f;

        if(this.getAttackState().equals("none") && isMoving)
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.walking"));
        else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.idle"));

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {
        if(this.getAttackState().equals("jump"))
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.attack.jump"));
        else if(this.getAttackState().equals("melee"))
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.jumping_zombie.attack.melee"));

        if(this.getAttackState().equals("none")) {
            event.getController().markNeedsReload();
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getJumpCooldownTicks() > 0 && !this.getAttackState().equals("jump"))
            this.setJumpCooldownTicks(this.getJumpCooldownTicks() - 1);
        if(this.getMeleeCooldownTicks() > 0 && !this.getAttackState().equals("melee"))
            this.setMeleeCooldownTicks(this.getMeleeCooldownTicks() - 1);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static DefaultAttributeContainer.Builder createJumpingZombieAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0d)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0d);
    }

    public void setJumpCooldownTicks(int ticks) {
        this.dataTracker.set(JUMP_COOLDOWN_TICKS, ticks);
    }

    public int getJumpCooldownTicks() {
        return this.dataTracker.get(JUMP_COOLDOWN_TICKS);
    }

    public void setMeleeCooldownTicks(int ticks) {
        this.dataTracker.set(MELEE_COOLDOWN_TICKS, ticks);
    }

    public int getMeleeCooldownTicks() {
        return this.dataTracker.get(MELEE_COOLDOWN_TICKS);
    }

    public int getAttackTickCount(String attackName) {
        return switch (attackName) {
            case "melee" -> 15;
            case "jump" -> 27;
            default -> 0;
        };
    }

    public void setAttackState(String attackName) {
        this.dataTracker.set(ATTACK_STATE, attackName);
    }

    public String getAttackState() {
        return this.dataTracker.get(ATTACK_STATE);
    }

    public int getAttackCooldown(String attackName) {
        return switch (attackName) {
            case "melee" -> 20;
            case "jump" -> 60;
            default -> 0;
        };
    }

    public int getAttackLength(String attackName) {
        return switch (attackName) {
            case "melee" -> 20;
            case "jump" -> 47;
            default -> 0;
        };
    }

    public void stopAnimations() {
        this.setAttackState("none");
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundRegistry.JUMPING_ZOMBIE_DEATH_SOUND_EVENT;
    }
}
