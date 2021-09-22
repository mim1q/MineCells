package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShockAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IShockAttackEntity;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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
        this.decrementCooldowns();
    }

    // Goals and Tracked Data ==========================================================================================

    public static final TrackedData<Integer> SHOCK_COOLDOWN = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOCK_COOLDOWN, 50);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new ShockAttackGoal<>(this, 10.0d));
    }

    // Animations ======================================================================================================

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(this.getAttackState().equals("none")) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.shocker.idle"));
        }
        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.shocker.attack"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    // Decrement Cooldowns =============================================================================================

    private void decrementCooldowns() {
        if (this.getShockAttackCooldown() > 0)
            this.setShockAttackCooldown(this.getShockAttackCooldown() - 1);
    }

    // Attributes and Initialization ===================================================================================

    public static DefaultAttributeContainer.Builder createShockerAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0d);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setNoGravity(true);
        this.setPosition(this.getPos().add(0.0d, 1.5d, 0.0d));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    // IShockAttackEntity Implementation ===============================================================================

    @Override
    public int getShockAttackReleaseTick() {
        return 40;
    }

    @Override
    public int getShockAttackMaxCooldown() {
        return 40;
    }

    @Override
    public int getShockAttackLength() {
        return 60;
    }

    @Override
    public int getShockAttackCooldown() {
        return this.dataTracker.get(SHOCK_COOLDOWN);
    }

    @Override
    public void setShockAttackCooldown(int ticks) {
        this.dataTracker.set(SHOCK_COOLDOWN, ticks);
    }

    @Override
    public SoundEvent getShockAttackChargeSoundEvent() {
        return SoundEvents.AMBIENT_CAVE;
    }

    @Override
    public SoundEvent getShockAttackReleaseSoundEvent() {
        return SoundEvents.AMBIENT_CAVE;
    }
}
