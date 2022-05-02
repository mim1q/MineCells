package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShootGoal;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import com.github.mim1q.minecells.entity.projectile.MagicOrbEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
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

import java.util.EnumSet;

public class InquisitorEntity extends MineCellsEntity implements IAnimatable, IShootEntity {

    private final AnimationFactory factory = new AnimationFactory(this);

    private static final TrackedData<Integer> SHOOT_COOLDOWN = DataTracker.registerData(InquisitorEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final Vec3d[] ORB_OFFSETS = {
            new Vec3d(-0.25D, 2.25D, 0.0D),
            new Vec3d(0.3D, 0.8D, 0.5D),
            new Vec3d(0.3D, 0.8D, -0.5D)
    };

    private final MagicOrbEntity[] orbs = new MagicOrbEntity[3];

    public InquisitorEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "movementController", 8, this::predicate));
    }

    private PlayState predicate(AnimationEvent<InquisitorEntity> event) {
        boolean isMoving = MathHelper.abs(event.getLimbSwingAmount()) > 0.05f;

        if (this.getAttackState().equals("shoot")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("inquisitor.shoot"));
        } else {
            if (isMoving) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("inquisitor.walk"));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("inquisitor.idle"));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(0, new InquisitorShootGoal(this, 10, 20));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOT_COOLDOWN, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(SHOOT_COOLDOWN, "shoot");
    }

    @Override
    public int getShootMaxCooldown() {
        return 50 + this.random.nextInt(50);
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
        return SoundRegistry.INQUISITOR_CHARGE;
    }

    @Override
    public SoundEvent getShootReleaseSoundEvent() {
        return SoundRegistry.INQUISITOR_RELEASE;
    }

    public static DefaultAttributeContainer.Builder createInquisitorAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0D);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static class InquisitorShootGoal extends ShootGoal<InquisitorEntity> {

        public InquisitorShootGoal(InquisitorEntity entity, int actionTick, int lengthTicks) {
            super(entity, actionTick, lengthTicks);
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public void start() {
            super.start();
            if (this.target != null) {
                this.entity.getLookControl().lookAt(this.target);
            }
        }

        @Override
        public void tick() {
            super.tick();
            this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.0d);
        }

        @Override
        public void shoot(LivingEntity target) {
            Vec3d targetPos = target.getPos().add(0.0D, 1.3D, 0.0D);
            Vec3d entityPos = this.entity.getPos().add(0.0D, 2.25D, 0.0D);

            Vec3d vel = targetPos.subtract(entityPos).normalize().multiply(0.8D);

            MagicOrbEntity orb = new MagicOrbEntity(EntityRegistry.MAGIC_ORB, this.entity.world);
            orb.setPosition(entityPos);
            orb.setVelocity(vel);
            orb.setOwner(this.entity);

            this.entity.world.spawnEntity(orb);
        }
    }
}
