package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.misc.MineCellsExplosion;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class KamikazeEntity extends MineCellsEntity {

    private static final TrackedData<Integer> FUSE = DataTracker.registerData(KamikazeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(KamikazeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public KamikazeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 0, true);
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUSE, -1);
        this.dataTracker.startTracking(SLEEPING, true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(3, new KamikazeFlyGoal(this, 1.0D));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(0, new KamikazeAttackGoal(this, 1.0D, 3.0D));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, true, true, null));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setPos(this.getX(), this.getY() + 0.25D, this.getZ());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void tick() {
        super.tick();
        int fuse = this.getFuse();
        if (fuse == 0 && this.isAlive()) {
            this.explode(4.0F);
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.KAMIKAZE_DEATH, SoundCategory.HOSTILE, 0.5F, 1.0F);
            this.discard();
        }
        this.setFuse(fuse - 1);
    }

    public void explode(float power) {
        if (!this.world.isClient()) {
            MineCellsExplosion.explode((ServerWorld)this.world, this, this.getPos(), power);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world);
        navigation.setCanPathThroughDoors(false);
        navigation.setCanSwim(false);
        navigation.setCanEnterOpenDoors(true);
        return navigation;
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }
    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }
    public void setSleeping(boolean sleeping) {
        this.dataTracker.set(SLEEPING, sleeping);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.KAMIKAZE_DEATH;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("sleeping", this.isSleeping());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSleeping(nbt.getBoolean("sleeping"));
    }

    public static DefaultAttributeContainer.Builder createKamikazeAttributes() {
        return createLivingAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
            .add(EntityAttributes.GENERIC_FLYING_SPEED, 3.0D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0D);
    }

    public static class KamikazeFlyGoal extends FlyGoal {

        protected final KamikazeEntity entity;

        public KamikazeFlyGoal(KamikazeEntity entity, double d) {
            super(entity, d);
            this.entity = entity;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.entity.isSleeping();
        }
    }

    public static class KamikazeAttackGoal extends Goal {

        protected final KamikazeEntity entity;
        protected final double speed;
        protected final double distance;

        public KamikazeAttackGoal(KamikazeEntity entity, double speed, double distance) {
            this.entity = entity;
            this.speed = speed;
            this.distance = distance;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.entity.getTarget() != null;
        }

        @Override
        public boolean shouldContinue() {
            return this.entity.getTarget() != null && this.entity.getTarget().isAlive();
        }

        @Override
        public void start() {
            this.entity.setSleeping(false);
            this.entity.world.playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundRegistry.KAMIKAZE_WAKE, SoundCategory.HOSTILE, 0.5F, 1.0F);
        }

        @Override
        public void stop() {}

        @Override
        public void tick() {
            Entity target = this.entity.getTarget();
            if (target != null) {
                Vec3d pos = target.getPos();
                this.entity.getMoveControl().moveTo(pos.x, pos.y + 1.5D, pos.z, this.speed);
                this.entity.getLookControl().lookAt(target);
                if (this.entity.distanceTo(target) <= this.distance && this.entity.getFuse() < 0) {
                    this.entity.setFuse(30);
                    this.entity.world.playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundRegistry.KAMIKAZE_CHARGE, SoundCategory.HOSTILE, 0.5F, 1.0F);
                }
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }
    }
}
