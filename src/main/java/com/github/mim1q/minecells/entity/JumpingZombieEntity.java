package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.JumpAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IJumpAttackEntity;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;


public class JumpingZombieEntity extends MineCellsEntity implements IAnimatable, IJumpAttackEntity {

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

    @Override
    public void initGoals() {
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0d));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0d));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(0, new JumpingZombieJumpAttackGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.25D, false));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(JUMP_COOLDOWN, 50);
    }

    //endregion

    //region Animations

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movementController", 5, this::movementPredicate));
        data.addAnimationController(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
        data.setResetSpeedInTicks(0);
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
        boolean isMoving = MathHelper.abs(event.getLimbSwingAmount()) > 0.05f;

        if (this.getAttackState().equals("none") && isMoving)
            event.getController().setAnimation(new AnimationBuilder().addAnimation("jumping_zombie.walk"));
        else
            event.getController().setAnimation(new AnimationBuilder().addAnimation("jumping_zombie.idle"));

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {
        if (this.getAttackState().equals("jump")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("jumping_zombie.jump"));
        } else {
            event.getController().markNeedsReload();
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    //endregion

    //region IJumpAttackEntity Implementation

    public void setJumpAttackCooldown(int ticks) {
        this.dataTracker.set(JUMP_COOLDOWN, ticks);
    }

    public int getJumpAttackCooldown() {
        return this.dataTracker.get(JUMP_COOLDOWN);
    }

    public int getJumpAttackMaxCooldown() {
        return 80 + this.getRandom().nextInt(80);
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

    //endregion

    private void decrementCooldowns() {
        if (this.getJumpAttackCooldown() > 0 && !this.getAttackState().equals("jump"))
            this.setJumpAttackCooldown(this.getJumpAttackCooldown() - 1);
    }

    public static DefaultAttributeContainer.Builder createJumpingZombieAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0d)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0d);
    }

    static class JumpingZombieJumpAttackGoal extends JumpAttackGoal<JumpingZombieEntity> {

        public JumpingZombieJumpAttackGoal(JumpingZombieEntity entity) {
            super(entity, 10, 30);
        }

        @Override
        public boolean canStart() {
            boolean canJump = super.canStart() && this.entity.getRandom().nextFloat() < 0.2f;
            if (!canJump)
                return false;
            double d = this.entity.distanceTo(this.entity.getTarget());
            return d >= 4.0d && d <= 12.0d;
        }
    }
}
