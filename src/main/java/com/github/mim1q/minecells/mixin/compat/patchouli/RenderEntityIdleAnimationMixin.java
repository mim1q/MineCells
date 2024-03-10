package com.github.mim1q.minecells.mixin.compat.patchouli;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "vazkii.patchouli.client.book.page.PageEntity")
public abstract class RenderEntityIdleAnimationMixin {
  @Inject(method = "renderEntity(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/Entity;FFFFF)V", at = @At("HEAD"))
  private static void onRenderEntity(DrawContext context, Entity entity, float x, float y, float z, float yaw, float partialTicks, CallbackInfo ci) {
    entity.age = MinecraftClient.getInstance().inGameHud.getTicks();
  }
}
