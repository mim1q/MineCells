package com.github.mim1q.minecells.mixin.enchant;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.mim1q.minecells.item.MineCellsItemTags.*;

@Mixin(FabricItem.class)
public interface EnchantmentMixin {
  @Inject(
    method = "canBeEnchantedWith",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$isAcceptableItem(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context, CallbackInfoReturnable<Boolean> cir) {
    if (
      enchantment == Enchantments.INFINITY && stack.isIn(BOWS_ACCEPTING_INFINITY)
        || enchantment == Enchantments.PUNCH && stack.isIn(BOWS_ACCEPTING_PUNCH)
        || enchantment == Enchantments.POWER && stack.isIn(BOWS_ACCEPTING_POWER)
        || enchantment == Enchantments.FLAME && stack.isIn(BOWS_ACCEPTING_FLAME)
        || enchantment == Enchantments.QUICK_CHARGE && stack.isIn(BOWS_ACCEPTING_QUICK_CHARGE)
    ) {
      cir.setReturnValue(true);
    }
  }
}
