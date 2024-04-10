package com.github.mim1q.minecells.mixin.shield;

import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class ShieldPlayerEntityMixin extends LivingEntity {
  private ShieldPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(
    method = "damage",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    var activeItem = getActiveItem();
    if (activeItem.getItem() instanceof CustomShieldItem shield) {
      if (CustomShieldItem.shouldTryToBlock(getThis(), source, 90, 90)) {
        System.out.println("Blocked!");
        cir.setReturnValue(false);
      }
    }
  }

  @Unique private PlayerEntity getThis() {
    return (PlayerEntity) (Object) this;
  }
}
