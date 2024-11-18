package com.github.mim1q.minecells.mixin.shield;

import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShieldLivingEntityMixin extends Entity {
  @Shadow public abstract ItemStack getActiveItem();

  private ShieldLivingEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(
    method = "isBlocking",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$cancelIsBlocking(CallbackInfoReturnable<Boolean> cir) {
    if (getActiveItem().getItem() instanceof CustomShieldItem) {
      cir.setReturnValue(false);
    }
  }
}
