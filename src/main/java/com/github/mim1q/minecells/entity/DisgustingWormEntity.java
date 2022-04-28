package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.projectile.DisgustingWormEggEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
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

public class DisgustingWormEntity extends MineCellsEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);
    private int soundCountdown = 0;

    public DisgustingWormEntity(EntityType<DisgustingWormEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "movementController", 0, this::movementPredicate));
        animationData.addAnimationController(new AnimationController<>(this, "eggsController", 0, this::eggsPredicate));
        animationData.setResetSpeedInTicks(10);
    }

    private PlayState movementPredicate(AnimationEvent<DisgustingWormEntity> event) {
        boolean isMoving = MathHelper.abs(event.getLimbSwingAmount()) > 0.05f;
        if (isMoving) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("disgusting_worm.walk"));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private PlayState eggsPredicate(AnimationEvent<DisgustingWormEntity> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("disgusting_worm.eggs"));
        return PlayState.CONTINUE;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.75D, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient() && this.isAttacking()) {
            if (this.soundCountdown > 0) {
                this.soundCountdown--;
            } else {
                this.world.playSound(getX(), getY(), getZ(), SoundRegistry.DISGUSTING_WORM_ATTACK, SoundCategory.HOSTILE, 1.0F, 1.0F, true);
                this.soundCountdown = 60 + this.random.nextInt(100);
            }
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        for (int i = 0; i < 6; i++) {
            Vec3d velocity = new Vec3d(this.random.nextDouble() - 0.5D, 0.7D + this.random.nextDouble() * 0.5D, this.random.nextDouble() - 0.5D).multiply(0.3D);

            DisgustingWormEggEntity grenade = new DisgustingWormEggEntity(EntityRegistry.DISGUSTING_WORM_EGG, this.world);
            grenade.setPosition(this.getPos());
            grenade.shoot(velocity);
            grenade.setFuse(15 + i * 5 + this.random.nextInt(5));

            this.world.spawnEntity(grenade);
        }
    }

    public static DefaultAttributeContainer.Builder createDisgustingWormAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0d)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0d)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0d);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.DISGUSTING_WORM_DEATH;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
