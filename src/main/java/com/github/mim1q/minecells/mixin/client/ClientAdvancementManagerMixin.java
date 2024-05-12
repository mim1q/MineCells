package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
  @Inject(method = "onAdvancements", at = @At("HEAD"))
  private void minecells$onAdvancements(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
    packet.getAdvancementsToProgress().forEach(
      (id, progress) -> {
        if (progress.isDone()) AdvancementHintRenderer.setAdvancementRendered(id, false);
      }
    );
    packet.getAdvancementIdsToRemove().forEach(
      id -> AdvancementHintRenderer.setAdvancementRendered(id, true)
    );
  }
}
