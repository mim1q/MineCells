package com.github.mim1q.minecells.mixin.compat.patchouli;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "vazkii.patchouli.client.book.page.PageEntity")
public abstract class RenderEntityIdleAnimationMixin {
  @Inject(
    method = "renderEntity(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/Entity;FFFFF)V",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void minecells$onRenderEntity(DrawContext graphics, Entity entity, float x, float y, float rotation, float renderScale, float offset, CallbackInfo ci) {
    if (entity instanceof MineCellsEntity) {
      entity.age = 10;
      var ms = graphics.getMatrices();
      ms.push();
      {
        ms.translate(x, y + 64, 50);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        ms.scale(renderScale, -renderScale, renderScale);
        ms.translate(0, offset, 0);

        var erd = MinecraftClient.getInstance().getEntityRenderDispatcher();

        RenderSystem.disableColorLogicOp();
        erd.setRenderShadows(false);
        erd.render(entity, 0, 0, 0, 0, 0f, ms, graphics.getVertexConsumers(), 0xF000F0);
        graphics.getVertexConsumers().draw();
      }
      ms.pop();
      ci.cancel();
    }
  }
}
