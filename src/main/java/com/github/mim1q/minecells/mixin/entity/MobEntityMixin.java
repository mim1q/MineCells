package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends Entity {
  public MobEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(method = "tickNewAi", at = @At("HEAD"), cancellable = true)
  private void minecells$cancelTickNewAi(CallbackInfo ci) {
    if (((LivingEntityAccessor)this).shouldActFrozen()) {
      ci.cancel();
    }
  }

  @Inject(method = "getMaxLookYawChange", at = @At("RETURN"), cancellable = true)
  private void minecells$getMaxLookYawChange(CallbackInfoReturnable<Integer> cir) {
    if (((LivingEntityAccessor)this).shouldActFrozen()) {
      cir.setReturnValue(0);
    }
  }

  @Inject(method = "getMaxLookPitchChange", at = @At("RETURN"), cancellable = true)
  private void minecells$getMaxLookPitchChange(CallbackInfoReturnable<Integer> cir) {
    if (((LivingEntityAccessor)this).shouldActFrozen()) {
      cir.setReturnValue(0);
    }
  }
}
