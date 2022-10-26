package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({
  ServerPlayerEntity.class,
  ClientPlayerEntity.class
})
public abstract class ServerPlayerAndClientPlayerMixin extends PlayerEntity implements PlayerEntityAccessor {
  public ServerPlayerAndClientPlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
    super(world, pos, yaw, gameProfile, publicKey);
  }

  @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"), cancellable = true)
  public void swingHand(Hand hand, CallbackInfo ci) {
    if (this.shouldCancelSwing()) {
      ci.cancel();
    }
  }
}
