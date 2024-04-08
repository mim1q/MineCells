package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
  @ModifyExpressionValue(
    method = "getArmPose",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0)
  )
  private static boolean minecells$modifyIsCrossbowInGetArmPose(
    boolean original,
    AbstractClientPlayerEntity playerEntity,
    Hand hand
  ) {
    return original || playerEntity.getStackInHand(hand).getItem() instanceof CustomCrossbowItem;
  }
}
