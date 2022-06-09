package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.interfaces.IDashEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShieldbearerEntity extends MineCellsEntity implements IDashEntity {

    private static final TrackedData<Integer> DASH_COOLDOWN = DataTracker.registerData(ShieldbearerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(ShieldbearerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(ShieldbearerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ShieldbearerEntity(EntityType<ShieldbearerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(DASH_COOLDOWN);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        Vec3d pos = damageSource.getPosition();
        if (pos != null) {
            Vec3d diff = pos.subtract(this.getPos());
            float angle = (float)MathHelper.atan2(diff.z, diff.x) * MathHelper.DEGREES_PER_RADIAN + 90.0F;
            if (MathHelper.angleBetween(this.bodyYaw, angle) > 120.0F) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 0.5F, 1.0F);
                return true;
            }
        }
        return super.isInvulnerableTo(damageSource);
    }

    public static DefaultAttributeContainer.Builder createShieldbearerAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 5.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    @Override
    public boolean isDashCharging() {
        return this.dataTracker.get(DASH_CHARGING);
    }

    @Override
    public void setDashCharging(boolean charging) {
        this.dataTracker.set(DASH_CHARGING, charging);
    }

    @Override
    public boolean isDashReleasing() {
        return this.dataTracker.get(DASH_RELEASING);
    }

    @Override
    public void setDashReleasing(boolean releasing) {
        this.dataTracker.set(DASH_RELEASING, releasing);
    }

    @Override
    public int getDashCooldown() {
        return this.dataTracker.get(DASH_COOLDOWN);
    }

    @Override
    public void setDashCooldown(int ticks) {
        this.dataTracker.set(DASH_COOLDOWN, ticks);
    }

    @Override
    public int getDashMaxCooldown() {
        return 40;
    }

    @Override
    public float getDashDamage() {
        return 10.0F;
    }

    @Override
    public SoundEvent getDashChargeSoundEvent() {
        return null;
    }

    @Override
    public SoundEvent getDashReleaseSoundEvent() {
        return null;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("dashCooldown", this.getDashCooldown());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setDashCooldown(nbt.getInt("dashCooldown"));
    }
}
