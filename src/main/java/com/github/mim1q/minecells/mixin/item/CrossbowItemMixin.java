package com.github.mim1q.minecells.mixin.item;

import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.github.mim1q.minecells.util.LegacyUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
  @Inject(
    method = "isCharged",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void minecells$injectIsCharged(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
    if (stack.getItem() instanceof CustomCrossbowItem) {
      var nbt = LegacyUtil.getOrCreateNbt(stack);
      if (nbt != null && nbt.getBoolean("Charged")) {
        cir.setReturnValue(true);
      }
    }
  }

  @Inject(
    method = "getPullTime",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void minecells$injectGetPullTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
    if (stack.getItem() instanceof CustomCrossbowItem customCrossbow) {
      cir.setReturnValue(customCrossbow.getDrawTime(user, stack));
    }
  }
}
