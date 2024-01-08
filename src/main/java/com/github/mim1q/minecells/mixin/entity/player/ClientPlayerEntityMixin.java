package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.network.c2s.RequestSyncMineCellsPlayerDataC2SPacket;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
  @Shadow
  @Final
  protected MinecraftClient client;
  @Unique
  private boolean minecells$wasAwakened = false;

  private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
    super(world, profile);
  }

  @Inject(method = "init", at = @At("RETURN"))
  private void init(CallbackInfo ci) {
    RequestSyncMineCellsPlayerDataC2SPacket.send();
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void minecells$tick(CallbackInfo ci) {
    var awakened = ((LivingEntityAccessor) this).getMineCellsFlag(MineCellsEffectFlags.AWAKENED);
    if (awakened != minecells$wasAwakened) {
      var particlePos = getEyePos().add(getRotationVecClient().multiply(0.2));
      clientWorld.addParticle(
        MineCellsParticles.EXPLOSION,
        particlePos.x,
        particlePos.y,
        particlePos.z,
        0.0D,
        0.0D,
        0.0D
      );
      var worldRenderer = MinecraftClient.getInstance().worldRenderer;
      worldRenderer.reload();
      minecells$wasAwakened = awakened;
    }
  }
}
