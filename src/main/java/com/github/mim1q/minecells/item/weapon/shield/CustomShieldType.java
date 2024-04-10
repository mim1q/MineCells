package com.github.mim1q.minecells.item.weapon.shield;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Box;

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

  public static final CustomShieldType RAMPART = new CustomShieldType(it -> {
    it.blockDamageReduction = 0.85f;
    it.blockAngle = 100f;
    it.onMeleeParry = context -> {
      context.player().addStatusEffect(new StatusEffectInstance(
        MineCellsStatusEffects.PROTECTED, 30, 0, false, false, true
      ));
    };
  });

  public static final CustomShieldType ASSAULT = new CustomShieldType(it -> {
    it.blockDamageReduction = 0.6f;
    it.onUse = context -> {
      MineCells.LOGGER.info("TODO: Assault shield use");
    };
  });

  public static final CustomShieldType BLOOD = new CustomShieldType(it -> {
    it.onMeleeParry = context -> {
      var entities = context.player.getWorld().getOtherEntities(
        context.player,
        Box.of(context.attacker.getPos(), 8, 8, 8),
        entity ->
          entity.distanceTo(context.attacker) <= 4
          && entity != context.player
          && entity instanceof LivingEntity
      );
      context.attacker.addStatusEffect(new StatusEffectInstance(
        MineCellsStatusEffects.BLEEDING, 100, 2, false, false, true
      ));
      entities.forEach(entity -> {
        ((LivingEntity) entity).addStatusEffect(
          new StatusEffectInstance(MineCellsStatusEffects.BLEEDING, 60, 1, false, false, true)
        );
      });
    };
  });

  public static final CustomShieldType ICE = new CustomShieldType(it -> {
    it.onMeleeParry = context -> {
      context.attacker.addStatusEffect(new StatusEffectInstance(
        MineCellsStatusEffects.FROZEN, 100, 0, false, false, true
      ));
    };
    it.onMeleeBlock = context -> {
      context.attacker.addStatusEffect(new StatusEffectInstance(
        StatusEffects.SLOWNESS, 30, 0, false, false, true
      ));
    };
  });

  public static final CustomShieldType GREED = new CustomShieldType(it -> {
    it.onMeleeParry = context -> {
      MineCells.LOGGER.info("TODO: Greed parry");
    };
  });


  //#endregion

  //#region Class Definition
  private float blockDamageReduction = 0.5f;
  private float blockAngle = 90f;
  private float parryTime = 5f;
  private float parryDamage = 6.0f;

  private Consumer<ShieldUseContext> onUse = context -> {};
  private Consumer<DamageContext> onParry = context -> {};
  private Consumer<MeleeDamageContext> onMeleeParry = context -> {};
  private Consumer<RangedDamageContext> onRangedParry = context -> {};

  private Consumer<DamageContext> onBlock = context -> {};
  private Consumer<MeleeDamageContext> onMeleeBlock = context -> {};
  private Consumer<RangedDamageContext> onRangedBlock = context -> {};

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

  public void onUse(ShieldUseContext context) {
    onUse.accept(context);
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
  public record ShieldUseContext(
    PlayerEntity player
  ) {
  }

  public record DamageContext(
    DamageSource source,
    PlayerEntity player,
    float amount
  ) {
    public MeleeDamageContext toMelee(LivingEntity attacker) {
      return new MeleeDamageContext(source, player, attacker, amount);
    }

    public RangedDamageContext toRanged(LivingEntity attacker, ProjectileEntity projectile) {
      return new RangedDamageContext(source, player, attacker, projectile, amount);
    }
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
