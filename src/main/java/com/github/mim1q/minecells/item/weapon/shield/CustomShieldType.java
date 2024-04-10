package com.github.mim1q.minecells.item.weapon.shield;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

import java.util.function.Consumer;

public class CustomShieldType {
  //#region Custom Shield Types
  public static final CustomShieldType DEFAULT = new CustomShieldType(it -> {
  });

  public static final CustomShieldType CUDGEL = new CustomShieldType(it -> {
    it.blockDamageReduction = 0.3f;
    it.onMeleeParry = context -> {
      context.attacker.addStatusEffect(new StatusEffectInstance(
        MineCellsStatusEffects.STUNNED, 30, 0, false, false, true
      ));
    };
    it.onMeleeBlock = context -> {
      context.attacker.addStatusEffect(new StatusEffectInstance(
        MineCellsStatusEffects.STUNNED, 5, 0, false, false, true
      ));
    };
  });
  //#endregion

  //#region Class Definition
  private float blockDamageReduction = 0.5f;
  private float blockAngle = 90f;
  private float parryTime = 20f;
  private float parryDamage = 6.0f;

  private Consumer<DamageContext> onParry = context -> {
  };
  private Consumer<MeleeDamageContext> onMeleeParry = context -> {
  };
  private Consumer<RangedDamageContext> onRangedParry = context -> {
  };

  private Consumer<DamageContext> onBlock = context -> {
  };
  private Consumer<MeleeDamageContext> onMeleeBlock = context -> {
  };
  private Consumer<RangedDamageContext> onRangedBlock = context -> {
  };

  public CustomShieldType(Consumer<CustomShieldType> setup) {
    setup.accept(this);
  }

  public float getBlockDamageReduction() {
    return blockDamageReduction;
  }

  public float getBlockAngle() {
    return blockAngle;
  }

  public float getParryTime() {
    return parryTime;
  }

  public float getParryDamage() {
    return parryDamage;
  }

  public void onParry(DamageContext context) {
    onParry.accept(context);
  }

  public void onMeleeParry(MeleeDamageContext context) {
    onMeleeParry.accept(context);
  }

  public void onRangedParry(RangedDamageContext context) {
    onRangedParry.accept(context);
  }

  public void onBlock(DamageContext context) {
    onBlock.accept(context);
  }

  public void onMeleeBlock(MeleeDamageContext context) {
    onMeleeBlock.accept(context);
  }

  public void onRangedBlock(RangedDamageContext context) {
    onRangedBlock.accept(context);
  }
  //#endregion

  //#region Context Classes
  public record DamageContext(
    DamageSource source,
    PlayerEntity player,
    float amount
  ) {
  }

  public record MeleeDamageContext(
    DamageSource source,
    PlayerEntity player,
    LivingEntity attacker,
    float amount
  ) {
  }

  public record RangedDamageContext(
    DamageSource source,
    PlayerEntity player,
    LivingEntity attacker,
    ProjectileEntity projectile,
    float amount
  ) {
  }
  //#endregion
}
