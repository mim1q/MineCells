package com.github.mim1q.minecells.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MineCellsEntity extends HostileEntity {

    private static final TrackedData<String> STATE = DataTracker.registerData(MineCellsEntity.class, TrackedDataHandlerRegistry.STRING);
    protected static final ArrayList<String> validStates = new ArrayList<>();

    public MineCellsEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
        validStates.add("none");
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(STATE, "none");
    }

    public String getState() {
        return this.dataTracker.get(STATE);
    }

    public void setState(String state) {
        if(validStates.contains(state)) {
            this.dataTracker.set(STATE, state);
        }
    }
}
