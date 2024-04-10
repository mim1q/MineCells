package com.github.mim1q.minecells.mixin.shield;

import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType.DamageContext;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType.MeleeDamageContext;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType.RangedDamageContext;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(PlayerEntity.class)
public abstract class ShieldPlayerEntityMixin extends LivingEntity {
  @Shadow public abstract boolean damage(DamageSource source, float amount);

  @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

  @Unique private boolean minecells$shieldBlocking = false;

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
      if (CustomShieldItem.shouldTryToBlock(getThis(), source, shield.shieldType.getBlockAngle())) {

        var parryTime = shield.shieldType.getParryTime();
        var damageContext = new DamageContext(source, getThis(), amount);

        getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ITEM_SHIELD_BLOCK, getSoundCategory(), 1f, 1f);

        if (this.getItemUseTime() <= parryTime) {
          applyShieldEffects(
            shield.shieldType,
            damageContext,
            shield.shieldType::onParry,
            shield.shieldType::onMeleeParry,
            shield.shieldType::onRangedParry,
            true
          );
          getWorld().playSound(null, getX(), getY(), getZ(), MineCellsSounds.CRIT, getSoundCategory(), 1f, 1f);

          cir.setReturnValue(false);
        } else {

          minecells$shieldBlocking = true;

          var reduction = shield.shieldType.getBlockDamageReduction();
          var result = damage(source, amount * (1f - reduction));

          applyShieldEffects(
            shield.shieldType,
            damageContext,
            shield.shieldType::onBlock,
            shield.shieldType::onMeleeBlock,
            shield.shieldType::onRangedBlock,
            false
          );

          cir.setReturnValue(result);
        }
      }
    }
  }

  @Unique
  private void applyShieldEffects(
    CustomShieldType shieldType,
    DamageContext context,
    Consumer<DamageContext> baseEffect,
    Consumer<MeleeDamageContext> meleeEffect,
    Consumer<RangedDamageContext> rangedEffect,
    boolean isParry
  ) {
    baseEffect.accept(context);
    var source = context.source();

    var attacker = source.getAttacker();
    if (attacker instanceof LivingEntity livingAttacker) {
      var projectile = source.getSource();
      if (projectile instanceof ProjectileEntity projectileEntity) {
        rangedEffect.accept(context.toRanged(livingAttacker, projectileEntity));
      } else {
        meleeEffect.accept(context.toMelee(livingAttacker));
        if (isParry) {
          livingAttacker.setVelocity(Vec3d.ZERO);
          livingAttacker.damage(getWorld().getDamageSources().playerAttack(getThis()), shieldType.getParryDamage());
        }
      }
    }
  }

  @Unique
  private PlayerEntity getThis() {
    return (PlayerEntity) (Object) this;
  }
}
