package com.github.mim1q.minecells.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class MineCellsEntity extends HostileEntity {

    protected MineCellsEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<String> ATTACK_STATE = DataTracker.registerData(MineCellsEntity.class, TrackedDataHandlerRegistry.STRING);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_STATE, "none");
    }

    public void setAttackState(String attackName) {
        this.dataTracker.set(ATTACK_STATE, attackName);
    }

    public String getAttackState() {
        return this.dataTracker.get(ATTACK_STATE);
    }

    public void resetAttackState() {
        this.setAttackState("none");
    }
}
