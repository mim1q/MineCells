package com.github.mim1q.minecells.mixin;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {

    private static final TrackedData<Boolean> IS_PROTECTED = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker()V", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(IS_PROTECTED, false);
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return entity instanceof ElevatorEntity || super.canStartRiding(entity);
    }

    @Override
    public boolean isProtected() {
        return this.dataTracker.get(IS_PROTECTED);
    }

    @Override
    public void setProtected(boolean isProtected) {
        this.dataTracker.set(IS_PROTECTED, isProtected);
    }
}
