package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
  @Inject(
    method = "applyFog",
    at = @At("TAIL")
  )
  private static void minecells$applyFog(
    Camera camera,
    BackgroundRenderer.FogType fogType,
    float viewDistance,
    boolean thickFog,
    float tickDelta,
    CallbackInfo ci
  ) {
    var world = MinecraftClient.getInstance().world;
    if (world != null && MineCellsDimension.of(world) == MineCellsDimension.PROMENADE_OF_THE_CONDEMNED) {
      RenderSystem.setShaderFogStart(24F);
    }
  }
}
