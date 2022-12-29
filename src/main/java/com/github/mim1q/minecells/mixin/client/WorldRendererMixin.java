package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {
  @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
  private boolean modifyRenderBlockOutline(boolean original) {
    PlayerEntity player = MinecraftClient.getInstance().player;
    if (player == null) {
      return true;
    }
    return !((LivingEntityAccessor) player).getMineCellsFlag(MineCellsEffectFlags.DISARMED);
  }
}
