package com.github.mim1q.minecells.mixin.enchant;

import net.minecraft.enchantment.*;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.mim1q.minecells.item.MineCellsItemTags.*;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
  @SuppressWarnings("ConstantValue")
  @Inject(
    method = "isAcceptableItem",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
    var objThis = (Object) this;
    if (
         objThis instanceof InfinityEnchantment && stack.isIn(BOWS_ACCEPTING_INFINITY)
      || objThis instanceof PunchEnchantment    && stack.isIn(BOWS_ACCEPTING_PUNCH)
      || objThis instanceof PowerEnchantment    && stack.isIn(BOWS_ACCEPTING_POWER)
      || objThis instanceof FlameEnchantment    && stack.isIn(BOWS_ACCEPTING_FLAME)
    ) {
      cir.setReturnValue(true);
    }
  }
}
