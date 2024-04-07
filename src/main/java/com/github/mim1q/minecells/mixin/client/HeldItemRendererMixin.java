package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
  @ModifyExpressionValue(
    method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
      ordinal = 1
    )
  )
  private boolean minecells$modifyIsCrossbowInRenderFirstPersonItem(boolean original, @Local(argsOnly = true) ItemStack stack) {
    return original || stack.getItem() instanceof CustomCrossbowItem;
  }
}
