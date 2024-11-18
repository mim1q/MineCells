package com.github.mim1q.minecells.mixin.shield;

import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType.DamageContext;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType.MeleeDamageContext;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType.RangedDamageContext;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import dev.mim1q.gimm1q.screenshake.ScreenShakeUtils;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
  @Unique private int minecells$lastBlockedTime = 0;
  @Unique private LivingEntity minecells$lastBlockedEntity = null;

  private ShieldPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(
    method = "damage",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (minecells$lastBlockedEntity == source.getAttacker() && age - minecells$lastBlockedTime < 10) {
      cir.setReturnValue(false);
      return;
    }

    if (minecells$shieldBlocking) {
      minecells$shieldBlocking = false;
      return;
    }

    var activeItem = getActiveItem();
    if (activeItem.getItem() instanceof CustomShieldItem shield) {
      var ctx = ValueCalculatorContext.create()
        .with(ValueCalculatorParameter.HOLDER, getThis())
        .with(ValueCalculatorParameter.HOLDER_STACK, activeItem);

      if (source.getAttacker() instanceof LivingEntity attacker) {
        ctx.with(ValueCalculatorParameter.TARGET, attacker);
      }

      var angleDifference = CustomShieldItem.getAngleDifference(getThis(), source);
      var parryTime = shield.shieldType.getParryTime();
      var damageContext = new DamageContext(source, getThis(), amount);
      var isParry = this.getItemUseTime() <= parryTime;
      var maxAngle = isParry ? shield.shieldType.getParryAngle() : shield.shieldType.getBlockAngle();

      if (angleDifference == null || angleDifference > maxAngle) return;

      getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ITEM_SHIELD_BLOCK, getSoundCategory(), 1f, 1f);
      activeItem.damage(1, getThis(), player -> player.sendToolBreakStatus(player.getActiveHand()));

      if (isParry) {
        applyShieldEffects(
          shield.shieldType,
          damageContext,
          shield.shieldType::onParry,
          shield.shieldType::onMeleeParry,
          shield.shieldType::onRangedParry,
          true,
          ctx
        );
        getWorld().playSound(null, getX(), getY(), getZ(), MineCellsSounds.CRIT, getSoundCategory(), 1f, 1f);
        CustomShieldItem.setParried(activeItem, true);

        cir.setReturnValue(false);
      } else {

        minecells$shieldBlocking = true;

        var reduction = shield.shieldType.getBlockDamageReduction(ctx);
        var result = damage(source, amount * (1f - reduction));

        applyShieldEffects(
          shield.shieldType,
          damageContext,
          shield.shieldType::onBlock,
          shield.shieldType::onMeleeBlock,
          shield.shieldType::onRangedBlock,
          false,
          ctx
        );

        cir.setReturnValue(result);
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
    boolean isParry,
    ValueCalculatorContext ctx
  ) {
    this.minecells$lastBlockedTime = age;
    this.minecells$lastBlockedEntity = context.source().getSource() instanceof LivingEntity entity ? entity : null;

    if (getWorld().isClient) return;
    baseEffect.accept(context);
    var source = context.source();

    var particle = shieldType.getParticle();

    if (particle != null) {
      var world = (ServerWorld) getWorld();
      var particlePos = getPos()
        .add(0, getHeight() / 2.0, 0)
        .add(getRotationVector()
          .normalize()
          .multiply(0.5)
        );

      world.spawnParticles(
        shieldType.getParticle(),
        particlePos.getX(),
        particlePos.getY(),
        particlePos.getZ(),
        isParry ? 10 : 3,
        0.2,
        0.2,
        0.2,
        0.1
      );
    }

    var shakeModifier = isParry ? "minecells:shield_parry" : "minecells:shield_block";
    ScreenShakeUtils.applyShake(
      (ServerPlayerEntity) getThis(),
      1.0f,
      20,
      shakeModifier
    );

    var attacker = source.getAttacker();
    if (attacker instanceof LivingEntity livingAttacker) {
      var sourceEntity = source.getSource();
      if (sourceEntity instanceof ProjectileEntity projectile) {
        rangedEffect.accept(context.toRanged(livingAttacker, projectile));
      } else {
        if (isParry) {
          livingAttacker.setVelocity(Vec3d.ZERO);
          livingAttacker.takeKnockback(1, livingAttacker.getX() - getX(), livingAttacker.getZ() - getZ());
          livingAttacker.damage(getWorld().getDamageSources().playerAttack(getThis()), shieldType.getParryDamage(ctx));
        }
        meleeEffect.accept(context.toMelee(livingAttacker));
      }
    }
  }

  @Unique
  private PlayerEntity getThis() {
    return (PlayerEntity) (Object) this;
  }
}
