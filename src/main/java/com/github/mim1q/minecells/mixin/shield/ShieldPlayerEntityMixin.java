package com.github.mim1q.minecells.mixin.shield;

import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class ShieldPlayerEntityMixin extends LivingEntity {
  @Shadow public abstract boolean damage(DamageSource source, float amount);

  private boolean minecells$shieldBlocking = false;

  private ShieldPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(
    method = "damage",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (minecells$shieldBlocking) {
      minecells$shieldBlocking = false;
      return;
    }

    var activeItem = getActiveItem();
    if (activeItem.getItem() instanceof CustomShieldItem shield) {
      if (CustomShieldItem.shouldTryToBlock(getThis(), source, 90)) {
        var parryTime = 20;
        if (this.getItemUseTime() <= parryTime) {
          cir.setReturnValue(false);
        } else {
          minecells$shieldBlocking = true;
          var reduction = 0.5f;
          var result = damage(source, amount * (1f - reduction));
          cir.setReturnValue(result);
        }
      }
    }
  }

  @Unique private PlayerEntity getThis() {
    return (PlayerEntity) (Object) this;
  }
}
