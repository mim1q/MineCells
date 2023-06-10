package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.network.c2s.RequestSyncMineCellsPlayerDataC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
  @Inject(method = "init", at = @At("RETURN"))
  private void init(CallbackInfo ci) {
    RequestSyncMineCellsPlayerDataC2SPacket.send();
  }
}
