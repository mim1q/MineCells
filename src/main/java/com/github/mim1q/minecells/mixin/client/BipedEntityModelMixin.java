package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> {
  @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
  public void setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
    if (livingEntity.getVehicle() instanceof ElevatorEntity) {
      this.riding = false;
    }
  }

}
