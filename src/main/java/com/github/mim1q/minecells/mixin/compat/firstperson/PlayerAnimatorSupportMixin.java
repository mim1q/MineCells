package com.github.mim1q.minecells.mixin.compat.firstperson;

import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Mixin(targets = "dev.tr7zw.firstperson.modsupport.PlayerAnimatorSupport")
public class PlayerAnimatorSupportMixin {
  @Inject(
    method = "applyOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FLnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
    at = @At("HEAD"),
    cancellable = true
  )
  void applyOffset(AbstractClientPlayerEntity player, float delta, Vec3d original, Vec3d current, CallbackInfoReturnable<Vec3d> cir) {
    if (player.hasVehicle() && player.getVehicle() instanceof ElevatorEntity) {
      float bodyYaw = MathHelper.lerpAngleDegrees(delta, player.prevBodyYaw, player.bodyYaw);
      float yaw = (90.0F - bodyYaw) * MathHelper.RADIANS_PER_DEGREE;
      float x = MathHelper.cos(yaw) * 0.25F;
      float z = MathHelper.sin(yaw) * 0.25F;
      cir.setReturnValue(original.add(x, 0.0D, -z));
    }
  }
}
