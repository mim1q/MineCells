package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShootGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import com.github.mim1q.minecells.entity.projectile.BigGrenadeEntity;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GrenadierEntity extends MineCellsEntity implements IAnimatable, IShootEntity {

    private final AnimationFactory factory = new AnimationFactory(this);

    private static final TrackedData<Integer> SHOOT_COOLDOWN = DataTracker.registerData(GrenadierEntity.class, TrackedDataHandlerRegistry.INTEGER);

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

       if (this.getAttackState().equals("none")) {
            if (isMoving) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("grenadier.walk"));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("grenadier.idle"));
            }
        } else if (this.getAttackState().equals("shoot")) {
           event.getController().setAnimation(new AnimationBuilder().addAnimation("grenadier.throw"));
       }

        return PlayState.CONTINUE;
    }

    private PlayState ballsPredicate(AnimationEvent<GrenadierEntity> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("grenadier.bombs"));
        return PlayState.CONTINUE;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(SHOOT_COOLDOWN, 50);
    }

    @Override
    public void initGoals() {
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(0, new GrenadierShootGoal(this, 18, 30));
        this.goalSelector.add(1, new WalkTowardsTargetGoal(this, 1.0D, true, 7.0D));
    }

    @Override
    public void tick() {
        System.out.println(this.getShootCooldown());
        super.tick();
        this.decrementCooldowns();
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

    private void decrementCooldowns() {
        if (this.getShootCooldown() > 0 && !this.getAttackState().equals("shoot")) {
            this.setShootCooldown(this.getShootCooldown() - 1);
        }
    }

    @Override
    public int getShootMaxCooldown() {
        return 60;
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
        return null;
    }

    @Override
    public SoundEvent getShootReleaseSoundEvent() {
        return null;
    }

    public static class GrenadierShootGoal extends ShootGoal<GrenadierEntity> {

        public GrenadierShootGoal(GrenadierEntity entity, int actionTick, int lengthTicks) {
            super(entity, actionTick, lengthTicks);
        }

        @Override
        public void shoot(LivingEntity target) {
            Vec3d targetPos = target.getPos();
            Vec3d entityPos = this.entity.getPos();

            Vec3d delta = targetPos.subtract(entityPos).multiply(0.035D).add(0.0D, 0.5D, 0.0D);

            GrenadeEntity grenade = new GrenadeEntity(EntityRegistry.GRENADE, this.entity.world);
            grenade.setPosition(entityPos.add(0.0D, 1.5D, 0.0D));
            grenade.shoot(delta);

            this.entity.world.spawnEntity(grenade);
        }
    }
}
