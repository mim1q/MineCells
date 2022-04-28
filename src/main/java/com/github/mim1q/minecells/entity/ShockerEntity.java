package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.AuraAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IAuraAttackEntity;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.minecraft.client.world.ClientWorld;
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

public class ShockerEntity extends MineCellsEntity implements IAnimatable, IAuraAttackEntity {

    AnimationFactory factory = new AnimationFactory(this);

    public ShockerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(AURA_COOLDOWN, null);
        this.handleStates();
    }

    //region Goals and Tracked Data

    public static final TrackedData<Integer> AURA_COOLDOWN = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(AURA_COOLDOWN, 50);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new AuraAttackGoal<>(this, 10.0d, 20, 50));
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
        if (this.world.isClient()) {
            if (!this.getAttackState().equals("none")) {
                Vec3d pos = this.getPos().add(0.0D, 1.0D, 0.0D);

                if (this.getAttackState().equals("aura_charge")) {
                    ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 5, 2.0D, -0.1D);
                } else {
                    ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 100, 9.5D, 0.01D);
                    ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 10, 1.0D, 0.5D);
                }
            }
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
    //region IAuraAttackEntity Implementation

    public float getAuraAttackDamage() {
        return 4.0F;
    }

    public int getAuraAttackMaxCooldown() {
        return 20;
    }

    public int getAuraAttackCooldown() {
        return this.dataTracker.get(AURA_COOLDOWN);
    }

    public void setAuraAttackCooldown(int ticks) {
        this.dataTracker.set(AURA_COOLDOWN, ticks);
    }

    public SoundEvent getAuraAttackChargeSoundEvent() {
        return SoundRegistry.SHOCKER_CHARGE;
    }

    public SoundEvent getAuraAttackReleaseSoundEvent() {
        return SoundRegistry.SHOCKER_RELEASE;
    }

    //endregion
    //region Sounds

    @Override
    public SoundEvent getDeathSound() {
        return SoundRegistry.SHOCKER_DEATH;
    }
    //endregion
}
