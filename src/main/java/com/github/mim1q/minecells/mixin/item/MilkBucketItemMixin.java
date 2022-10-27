package com.github.mim1q.minecells.mixin.item;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MilkBucketItem.class)
public abstract class MilkBucketItemMixin extends Item {
  public MilkBucketItemMixin(Settings settings) {
    super(settings);
  }

  @Redirect(
    method = "finishUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z"
    )
  )
  public boolean clearStatusEffects(LivingEntity livingEntity) {
    ((LivingEntityAccessor) livingEntity).clearCurableStatusEffects();
    return false;
  }
}
