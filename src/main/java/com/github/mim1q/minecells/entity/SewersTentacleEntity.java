package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.accessor.EntityAccessor;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SewersTentacleEntity extends MineCellsEntity {

    public int buriedTicks = 30;

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(SewersTentacleEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_BURIED = DataTracker.registerData(SewersTentacleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public SewersTentacleEntity(EntityType<SewersTentacleEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.0F;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        float chance = this.random.nextFloat();
        int variant = 0;
        if (chance > 0.5F) {
            if (chance > 0.83F) {
                variant = 2;
            } else {
                variant = 1;
            }
        }
        this.dataTracker.startTracking(VARIANT, variant);
        this.dataTracker.startTracking(IS_BURIED, true);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.25D, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient()) {
            this.spawnMovingParticles();
        }
        if (this.isBuried()) {
            if (this.buriedTicks < 30) {
                this.buriedTicks++;
            }
        } else {
            if (this.buriedTicks > 0) {
                this.buriedTicks--;
            }
        }
        float height = 1.9F;
        if (this.buriedTicks > 10) {
            height = 0.1F;
        }
        ((EntityAccessor)this).setDimensions(new EntityDimensions(0.7F, height, false));
    }

    protected void spawnMovingParticles() {
        if (this.getVelocity().length() > 0.1D || this.isBuried()) {
            BlockState blockState = this.world.getBlockState(new BlockPos(this.getPos().subtract(0.0F, 0.01F, 0.0F)));
            if (blockState != null && blockState.isOpaque()) {
                ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState);
                ParticleHelper.addInBox(
                    (ClientWorld) this.world,
                    particle,
                    Box.of(this.getPos().add(0.0D, 0.125D, 0.0D), 1.0D, 0.25D, 1.0D),
                    this.isBuried() ? 10 : 5,
                    new Vec3d(-0.01D, -0.01D, -0.01D)
                );
            }
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (this.isBuried() || this.buriedTicks > 10) {
            return false;
        }
        return super.tryAttack(target);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource == DamageSource.IN_WALL) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean isInvulnerable() {
        return this.isBuried();
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!(entity instanceof SewersTentacleEntity)) {
            return;
        }
        super.pushAwayFrom(entity);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SewersTentacleNavigation(this, world);
    }

    public static DefaultAttributeContainer.Builder createSewersTentacleAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public boolean isBuried() {
        return this.dataTracker.get(IS_BURIED);
    }

    public void setBuried(boolean buried) {
        this.dataTracker.set(IS_BURIED, buried);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("variant", this.getVariant());
        nbt.putBoolean("buried", this.isBuried());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(nbt.getInt("variant"));
        this.setBuried(nbt.getBoolean("buried"));
    }

    public static class SewersTentacleNavigation extends MobNavigation {
        public SewersTentacleNavigation(SewersTentacleEntity entity, World world) {
            super(entity, world);
            this.setCanEnterOpenDoors(true);
            this.setCanPathThroughDoors(true);
        }

        @Override
        public void tick() {
            super.tick();
            ((SewersTentacleEntity)this.entity).setBuried(this.shouldBury(this.getCurrentPath()));
        }

        protected boolean shouldBury(Path path) {
            if (path == null || this.entity.getTarget() == null) {
                return true;
            }
            return path.getEnd() != null && path.getEnd().y != this.entity.getPos().y;
        }
    }
}
