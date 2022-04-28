package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShockAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IShockAttackEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ShockerEntity extends MineCellsEntity implements IAnimatable, IShockAttackEntity {

    AnimationFactory factory = new AnimationFactory(this);

    public ShockerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(SHOCK_COOLDOWN, null);
        this.handleStates();
    }

    //region Goals and Tracked Data

    public static final TrackedData<Integer> SHOCK_COOLDOWN = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOCK_COOLDOWN, 50);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new ShockAttackGoal<>(this, 12.0d, 20, 40));
    }

    //endregion
    //region Animations

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.getAttackState().equals("none")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("shocker.idle"));
        }
        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("shocker.attack"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    //endregion
    //region Handle States

    private void handleStates() {
        if (this.getAttackState().equals("shock_charge")) {
            this.spawnShockParticles(ParticleTypes.ELECTRIC_SPARK, 5, 2.0d, -0.5d);
        }
        else if (this.getAttackState().equals("shock_release")) {
            this.spawnShockParticles(ParticleTypes.ELECTRIC_SPARK, 100, 9.5d, 0.3d);
            this.spawnShockParticles(ParticleTypes.ELECTRIC_SPARK, 10, 1.0d, 5.0d);
        }
    }
    //endregion
    //region Attributes and Initialization

    public static DefaultAttributeContainer.Builder createShockerAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0d)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0d)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0d)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0d);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setNoGravity(true);
        this.setPosition(this.getPos().add(0.0d, 1.5d, 0.0d));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
    //endregion
    //region IShockAttackEntity Implementation

    public int getShockAttackMaxCooldown() {
        return 20;
    }

    public int getShockAttackCooldown() {
        return this.dataTracker.get(SHOCK_COOLDOWN);
    }

    public void setShockAttackCooldown(int ticks) {
        this.dataTracker.set(SHOCK_COOLDOWN, ticks);
    }

    public SoundEvent getShockAttackChargeSoundEvent() {
        return SoundRegistry.SHOCKER_CHARGE;
    }

    public SoundEvent getShockAttackReleaseSoundEvent() {
        return SoundRegistry.SHOCKER_RELEASE;
    }

    public void spawnShockParticles(ParticleEffect particle, int amount, double radius, double speed) {
        for (int i = 0; i < amount; i++) {
            Vec3d offset = new Vec3d(
                    this.getRandom().nextDouble() * 2.0d - 1.0d,
                    this.getRandom().nextDouble() * 2.0d - 1.0d,
                    this.getRandom().nextDouble() * 2.0d - 1.0d
            ).normalize();
            Vec3d velocity = offset.multiply(speed);
            offset = offset.multiply(radius);
            this.world.addParticle(
                    particle,
                    this.getX() + offset.x,
                    this.getY() + offset.y + 1.0d,
                    this.getZ() + offset.z,
                    velocity.x, velocity.y, velocity.z
            );
        }
    }
    //endregion
    //region Sounds

    @Override
    public SoundEvent getDeathSound() {
        return SoundRegistry.SHOCKER_DEATH;
    }
    //endregion
}
