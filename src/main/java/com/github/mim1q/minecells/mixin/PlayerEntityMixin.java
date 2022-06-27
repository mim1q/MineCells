package com.github.mim1q.minecells.mixin;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

    private static final TrackedData<Integer> CELL_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(CELL_AMOUNT, 0);
    }

    public int getCells() {
        return this.dataTracker.get(CELL_AMOUNT);
    }

    public void setCells(int amount) {
        this.dataTracker.set(CELL_AMOUNT, amount);
    }

    @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    protected void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("cells", this.getCells());
    }

    @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    protected void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setCells(nbt.getInt("cells"));
    }

    @Override
    protected void drop(DamageSource source) {
        super.drop(source);
        int amount = this.getCells() / 2;
        CellEntity.spawn(this.world, this.getPos(), amount);
    }
}
