package com.github.mim1q.minecells.mixin.network;

import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
  @Shadow public ServerPlayerEntity player;

  @Inject(
    method = "onPlayerAction(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;)V",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/server/network/ServerPlayerEntity;clearActiveItem()V",
      shift = At.Shift.BEFORE,
      ordinal = 0
    )
  )
  private void minecells$onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
    var mainhandStack = this.player.getMainHandStack();
    var offhandStack = this.player.getOffHandStack();

    if (mainhandStack.getItem() instanceof CustomShieldItem shield) {
      player.getItemCooldownManager().set(shield, shield.shieldType.getCooldown(false));
    }

    if (offhandStack.getItem() instanceof CustomShieldItem shield) {
      player.getItemCooldownManager().set(shield, shield.shieldType.getCooldown(false));
    }
  }
}
