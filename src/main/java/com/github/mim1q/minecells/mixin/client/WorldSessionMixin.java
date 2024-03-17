package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSession.class)
public class WorldSessionMixin {
  @Inject(method = "onAdvancementMade", at = @At("HEAD"))
  void minecells$onAdvancementMade(World world, Advancement advancement, CallbackInfo ci) {
    AdvancementHintRenderer.setAdvancementRendered(advancement.getId(), false);
  }

  @Inject(method = "onLoad", at = @At("HEAD"))
  void minecells$onLoad(CallbackInfo ci) {
    AdvancementHintRenderer.resetAdvancements();
  }
}
