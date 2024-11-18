package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
  private AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
    super(world, pos, yaw, gameProfile);
    throw new AssertionError();
  }

  @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
  private void minecells$getFovMultiplier(CallbackInfoReturnable<Float> cir) {
    var item = this.getActiveItem().getItem();
    if (item instanceof CustomBowItem bow && this.isUsingItem()) {
      cir.setReturnValue(bow.getFovMultiplier(this, this.getActiveItem()));
    }
  }
}
