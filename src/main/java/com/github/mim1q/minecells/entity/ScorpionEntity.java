package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedShootGoal;
import com.github.mim1q.minecells.entity.nonliving.projectile.ScorpionSpitEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ScorpionEntity extends MineCellsEntity {

    public AnimationProperty buriedProgress = new AnimationProperty(1.0F, AnimationProperty.EasingType.LINEAR);
    public AnimationProperty swingProgress = new AnimationProperty(1.0F, AnimationProperty.EasingType.IN_OUT_QUAD);
    protected int shootCooldown = 0;

    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(ScorpionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SHOOT_CHARGING = DataTracker.registerData(ScorpionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ScorpionEntity(EntityType<ScorpionEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SLEEPING, true);
        this.dataTracker.startTracking(SHOOT_CHARGING, false);
    }

    @Override
    protected void initGoals() { }

    protected void initGoalsLate() {
        super.initGoals();

        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.add(0, new TimedShootGoal.Builder<>(this)
            .cooldownSetter((cooldown) -> this.shootCooldown = cooldown)
            .cooldownGetter(() -> this.shootCooldown)
            .stateSetter(this::handleShootState)
            .projectileCreator((pos, targetPos) -> {
                ScorpionSpitEntity entity = new ScorpionSpitEntity(EntityRegistry.SCORPION_SPIT, this.world);
                pos = pos.add(0.0D, 0.25D, 0.0D);
                entity.setPosition(pos);
                entity.setVelocity(targetPos.subtract(pos).normalize().multiply(0.8D));
                return entity;
            })
            .chargeSound(SoundRegistry.SCORPION_CHARGE)
            .actionTick(20)
            .length(20)
            .defaultCooldown(40)
            .chance(0.1F)
            .build()
        );
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isSleeping() && this.world.getClosestPlayer(this, 5.0F) != null) {
            this.dataTracker.set(SLEEPING, false);
            this.playSound(SoundRegistry.RISE, 0.5F, 1.0F);
            this.initGoalsLate();
        }
        if (this.world.isClient() && this.buriedProgress.getProgress() > 0.0F && this.buriedProgress.getProgress() < 1.0F) {
            this.spawnUnburyingParticles();
        }

        this.shootCooldown = Math.max(0, --this.shootCooldown);
    }

    @Override
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        if (target instanceof LivingEntity livingEntity) {
            StatusEffectInstance effectInstance = new StatusEffectInstance(
                StatusEffects.POISON,
                100 + 20 * this.world.getDifficulty().getId() - 1,
                0
            );
            livingEntity.addStatusEffect(effectInstance);
        }
    }

    public void spawnUnburyingParticles() {
        BlockState blockState = this.world.getBlockState(new BlockPos(this.getPos().subtract(0.0F, 0.01F, 0.0F)));
        if (blockState != null && blockState.isOpaque()) {
            ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState);
            ParticleUtils.addInBox(
                (ClientWorld) this.world,
                particle,
                Box.of(this.getPos().add(0.0D, 0.125D, 0.0D), 1.0D, 0.25D, 1.0D),
                10,
                new Vec3d(-0.01D, -0.01D, -0.01D)
            );
        }
    }

    public void handleShootState(TimedActionGoal.State state, boolean value) {
        if (state == TimedActionGoal.State.CHARGE) {
            this.dataTracker.set(SHOOT_CHARGING, value);
        }
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public static DefaultAttributeContainer.Builder createScorpionAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 5.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("shootCooldown", this.shootCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.shootCooldown = nbt.getInt("shootCooldown");
    }
}
