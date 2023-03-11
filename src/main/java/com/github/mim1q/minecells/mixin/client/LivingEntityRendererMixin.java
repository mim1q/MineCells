package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.client.render.feature.MineCellsEffectsFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

  @Shadow
  protected abstract boolean addFeature(FeatureRenderer<Entity, EntityModel<Entity>> feature);

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Lnet/minecraft/client/render/entity/model/EntityModel;F)V", at = @At("TAIL"))
  public void init(EntityRendererFactory.Context ctx, EntityModel<Entity> model, float shadowRadius, CallbackInfo ci) {
    LivingEntityRenderer<T, M> renderer = (LivingEntityRenderer) (Object) this;
    this.addFeature(new MineCellsEffectsFeatureRenderer(renderer));
  }
}
