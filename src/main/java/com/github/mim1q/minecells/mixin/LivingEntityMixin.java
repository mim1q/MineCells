package com.github.mim1q.minecells.mixin;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import com.github.mim1q.minecells.registry.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {

  int ticksInSewage = 0;

  @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

  private static final TrackedData<Boolean> IS_PROTECTED = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  public LivingEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(method = "initDataTracker()V", at = @At("TAIL"))
  public void initDataTracker(CallbackInfo ci) {
    this.dataTracker.startTracking(IS_PROTECTED, false);
  }

  @Inject(method = "tick()V", at = @At("HEAD"))
  public void tick(CallbackInfo ci) {
    if (!this.world.isClient()) {
      if (this.checkIfInSewageAndUpdate()) {
        this.ticksInSewage++;
      } else {
        this.ticksInSewage = 0;
      }
    }
  }

  protected boolean checkIfInSewageAndUpdate() {
    List<BlockState> states = this.world.getStatesInBoxIfLoaded(this.getBoundingBox()).toList();
    for (BlockState state : states) {
      boolean isAncientSewage = state.getBlock() == BlockRegistry.ANCIENT_SEWAGE;
      if (state.getBlock() == BlockRegistry.SEWAGE || isAncientSewage) {
        if (this.ticksInSewage % (isAncientSewage ? 10 : 20) == 0) {
          ((LivingEntity)(Object)this).addStatusEffect(new StatusEffectInstance(
            StatusEffects.POISON,
            40,
            isAncientSewage ? 1 : 0)
          );
        }
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onSwimmingStart() {
    if (!this.checkIfInSewageAndUpdate()) {
      super.onSwimmingStart();
    } else {
      this.emitGameEvent(GameEvent.SPLASH);
    }
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
