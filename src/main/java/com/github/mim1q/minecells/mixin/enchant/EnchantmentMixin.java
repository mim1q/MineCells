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
  @Inject(
    method = "isAcceptableItem",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
    var obj = (Enchantment) (Object) this;
    if (
         obj == Enchantments.INFINITY     && stack.isIn(BOWS_ACCEPTING_INFINITY)
      || obj == Enchantments.PUNCH        && stack.isIn(BOWS_ACCEPTING_PUNCH)
      || obj == Enchantments.POWER        && stack.isIn(BOWS_ACCEPTING_POWER)
      || obj == Enchantments.FLAME        && stack.isIn(BOWS_ACCEPTING_FLAME)
      || obj == Enchantments.QUICK_CHARGE && stack.isIn(BOWS_ACCEPTING_QUICK_CHARGE)
    ) {
      cir.setReturnValue(true);
    }
  }
}
