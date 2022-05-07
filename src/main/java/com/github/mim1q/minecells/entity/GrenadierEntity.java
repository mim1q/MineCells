package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShootGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GrenadierEntity extends MineCellsEntity implements IShootEntity {

    // Animation data
    @Environment(EnvType.CLIENT)
    public float additionalRotation = 0.0F;

    private static final TrackedData<Integer> SHOOT_COOLDOWN = DataTracker.registerData(GrenadierEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public GrenadierEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
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

        this.goalSelector.add(0, new GrenadierShootGoal(this, 10, 20));
        this.goalSelector.add(1, new WalkTowardsTargetGoal(this, 1.0D, true, 5.0D));
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(SHOOT_COOLDOWN, "shoot");
    }

    public static DefaultAttributeContainer.Builder createGrenadierAttributes() {
        return createLivingAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 25.0D)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 6.0D);
    }

    @Override
    public int getShootMaxCooldown() {
        return 20 + this.random.nextInt(40);
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
        return SoundRegistry.GRENADIER_CHARGE;
    }

    @Override
    public SoundEvent getShootReleaseSoundEvent() {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.JUMPING_ZOMBIE_DEATH;
    }

    public static class GrenadierShootGoal extends ShootGoal<GrenadierEntity> {

        public GrenadierShootGoal(GrenadierEntity entity, int actionTick, int lengthTicks) {
            super(entity, actionTick, lengthTicks, 0.3F);
        }

        @Override
        public void shoot(LivingEntity target) {
            Vec3d targetPos = target.getPos().add(this.entity.random.nextDouble() * 2.0D - 1.0D, 0.0D, this.entity.random.nextDouble() * 2.0D - 1.0D);
            Vec3d entityPos = this.entity.getPos();

            Vec3d delta = targetPos.subtract(entityPos).multiply(0.035D).add(0.0D, 0.5D, 0.0D);

            GrenadeEntity grenade = new GrenadeEntity(EntityRegistry.GRENADE, this.entity.world);
            grenade.setPosition(entityPos.add(0.0D, 1.5D, 0.0D));
            grenade.shoot(delta);

            this.entity.world.spawnEntity(grenade);
        }
    }
}
