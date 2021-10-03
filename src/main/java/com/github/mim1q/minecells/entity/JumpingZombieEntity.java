package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.AnimetedMeleeAttackGoal;
import com.github.mim1q.minecells.entity.ai.goal.JumpAttackGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.entity.interfaces.IJumpAttackEntity;
import com.github.mim1q.minecells.entity.interfaces.IMeleeAttackEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
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
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;


public class JumpingZombieEntity extends MineCellsEntity implements IAnimatable, IMeleeAttackEntity, IJumpAttackEntity {

    AnimationFactory factory = new AnimationFactory(this);

    public JumpingZombieEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldowns();
    }

    //region Goals and Tracked Data

    public static final TrackedData<Integer> JUMP_COOLDOWN = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> MELEE_COOLDOWN = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public void initGoals() {
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.6d));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.6d));

        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(2, new WalkTowardsTargetGoal(this, 1.0d, false));
        this.goalSelector.add(1, new JumpAttackGoal<>(this));
        this.goalSelector.add(1, new AnimetedMeleeAttackGoal<>(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(JUMP_COOLDOWN, 50);
        this.dataTracker.startTracking(MELEE_COOLDOWN, 50);
    }

    //endregion

    //region Animations

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
    public AnimationFactory getFactory() {
        return this.factory;
    }

    //endregion

    //region Decrement Cooldowns

    private void decrementCooldowns() {
        if(this.getJumpAttackCooldown() > 0 && !this.getAttackState().equals("jump"))
            this.setJumpAttackCooldown(this.getJumpAttackCooldown() - 1);
        if(this.getMeleeAttackCooldown() > 0 && !this.getAttackState().equals("melee"))
            this.setMeleeAttackCooldown(this.getMeleeAttackCooldown() - 1);
    }

    //endregion

    //region Attributes

    public static DefaultAttributeContainer.Builder createJumpingZombieAttributes() {
        return createLivingAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20d)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0d)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0d)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0d);
    }

    //endregion

    //region IJumpAttackEntity Implementation

    public void setJumpAttackCooldown(int ticks) {
        this.dataTracker.set(JUMP_COOLDOWN, ticks);
    }

    public int getJumpAttackCooldown() {
        return this.dataTracker.get(JUMP_COOLDOWN);
    }

    public int getJumpAttackActionTick() {
        return 10;
    }

    public int getJumpAttackMaxCooldown() {
        return 80 + this.getRandom().nextInt(80);
    }

    public int getJumpAttackLength() {
        return 35;
    }

    //endregion

    //region IMeleeAttackEntity Implementation

    public void setMeleeAttackCooldown(int ticks) {
        this.dataTracker.set(MELEE_COOLDOWN, ticks);
    }

    public int getMeleeAttackCooldown() {
        return this.dataTracker.get(MELEE_COOLDOWN);
    }

    public int getMeleeAttackActionTick() {
        return 10;
    }

    public int getMeleeAttackMaxCooldown() {
        return 15;
    }

    public int getMeleeAttackLength() {
        return 15;
    }

    //endregion

    //region Sounds

    @Override
    public SoundEvent getDeathSound() {
        return SoundRegistry.JUMPING_ZOMBIE_DEATH;
    }

    @Override
    public SoundEvent getJumpAttackChargeSoundEvent() {
        return SoundRegistry.JUMPING_ZOMBIE_JUMP_CHARGE;
    }

    @Override
    public SoundEvent getJumpAttackReleaseSoundEvent() {
        return SoundRegistry.JUMPING_ZOMBIE_JUMP_RELEASE;
    }

    @Override
    public SoundEvent getMeleeAttackChargeSoundEvent() {
        return SoundRegistry.JUMPING_ZOMBIE_MELEE_CHARGE;
    }

    @Override
    public SoundEvent getMeleeAttackReleaseSoundEvent() {
        return SoundRegistry.JUMPING_ZOMBIE_MELEE_RELEASE;
    }

    //endregion
}
