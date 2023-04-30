package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimensions;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SynchronousResourceReloader, AutoCloseable {
  @Shadow private @Nullable ClientWorld world;

  private final static WorldBorder renderedBorder = new WorldBorder();
  static {
    renderedBorder.setSize(1023);
    renderedBorder.setCenter(0, 0);
  }

  @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
  private boolean modifyRenderBlockOutline(boolean original) {
    PlayerEntity player = MinecraftClient.getInstance().player;
    if (player == null) {
      return true;
    }
    return original && !((LivingEntityAccessor) player).getMineCellsFlag(MineCellsEffectFlags.DISARMED);
  }

  @ModifyVariable(method = "renderWorldBorder", at = @At("STORE"))
  private WorldBorder minecells$modifyRenderWorldBorder(WorldBorder original) {
    if (MineCellsDimensions.isMineCellsDimension(world)) {
      var player = MinecraftClient.getInstance().player;
      if (player == null) return original;
      var pos = MathUtils.getClosestMultiplePosition(player.getBlockPos(), 1024);
      renderedBorder.setCenter(pos.getX() + 0.5D, pos.getZ() + 0.5D);
      return renderedBorder;
    }
    return original;
  }
}
