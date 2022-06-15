package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.registry.StatusEffectRegistry;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ProtectorEntity extends MineCellsEntity {

    @Environment(EnvType.SERVER)
    protected int stateTicks = 0;
    public List<Entity> trackedEntities = new ArrayList<>();

    private static final TrackedData<Boolean> ACTIVE = DataTracker.registerData(ProtectorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ProtectorEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ACTIVE, false);
    }

    @Override
    protected void initGoals() { }

    @Override
    public void tick() {
        if (this.isActive()) {
            List<Entity> entities = this.world.getOtherEntities(
                this,
                Box.of(this.getPos(), 15.0D, 15.0D, 15.0D),
                entity -> ProtectorEntity.canProtect(entity) && entity.distanceTo(this) < 7.5D
            );
            this.trackedEntities = entities;

            if (!this.world.isClient()) {
                if (this.stateTicks == 1 || this.stateTicks % 20 == 0) {
                    this.playSound(SoundRegistry.BUZZ, 0.25F, 1.0F);
                }
                if (this.stateTicks > 50) {
                    this.setActive(false);
                    this.stateTicks = 0;
                }
                for (Entity e : entities) {
                    StatusEffectInstance effect = new StatusEffectInstance(StatusEffectRegistry.PROTECTED, 5, 0, false, false);
                    ((LivingEntity) e).addStatusEffect(effect);
                }
            } else if (!this.trackedEntities.isEmpty()) {
                ParticleHelper.addParticle((ClientWorld)this.world, ParticleRegistry.PROTECTOR, this.getPos().add(0.0D, 1.0D, 0.0D), Vec3d.ZERO);
            }
        }
        if (!this.world.isClient) {
            if (!this.isActive() && this.stateTicks > 100) {
                this.setActive(true);
                this.stateTicks = 0;
            }
            this.stateTicks++;
        }

        super.tick();
        this.setPosition(this.prevX, this.getY(), this.prevZ);
    }

    protected static boolean canProtect(Entity e) {
        if (e instanceof KamikazeEntity) { return false; }
        if (e instanceof ProtectorEntity) { return false; }
        if (e instanceof MutatedBatEntity) { return false; }

        return e instanceof HostileEntity;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public boolean isActive() {
        return this.dataTracker.get(ACTIVE);
    }
    public void setActive(boolean active) {
        this.dataTracker.set(ACTIVE, active);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("stateTicks", stateTicks);
        nbt.putBoolean("active", this.isActive());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.stateTicks = nbt.getInt("stateTicks");
        this.setActive(nbt.getBoolean("active"));
    }

    public static DefaultAttributeContainer.Builder createProtectorAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0F)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0F)
            .add(EntityAttributes.GENERIC_ARMOR, 5.0F);
    }
}
