package com.github.mim1q.minecells.item.weapon.shield;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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
    it.parryAngle = 360f;
    it.cooldown = 60;
    it.cooldownAfterParry = 20;
    it.onUse = context -> {
      var user = context.player();
      user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), MineCellsSounds.LEAPING_ZOMBIE_RELEASE, user.getSoundCategory(), 1.0f, 1.0f);
      user.setVelocity(user.getRotationVector()
        .multiply(0.5, 0.0, 0.5)
        .normalize()
        .multiply(1.5)
        .add(0.0, 0.2, 0.0)
      );
    };
    it.onHold = context -> {
      var user = context.player();
      if (context.useTicks() < 10) {
        var box = user.getBoundingBox().expand(1.0);
        var entities = user.getEntityWorld().getOtherEntities(
          user,
          box,
          entity -> entity instanceof LivingEntity
        );
        entities.forEach(entity -> {
          ((LivingEntity) entity).takeKnockback(0.5, entity.getX() - user.getX(), entity.getZ() - user.getZ());
          entity.damage(user.getWorld().getDamageSources().playerAttack(user), 8.0f);
        });
      }
    };
  });

  private static void applyBleedingAround(PlayerEntity user, Vec3d pos) {
    var entities = user.getWorld().getOtherEntities(
      null,
      Box.of(pos, 8, 8, 8),
      entity ->
        entity.squaredDistanceTo(pos) <= 16
        && entity != user
        && entity instanceof LivingEntity
    );
    entities.forEach(entity -> {
      ((LivingEntity) entity).addStatusEffect(
        new StatusEffectInstance(MineCellsStatusEffects.BLEEDING, 60, 1, false, false, true)
      );
    });
  }

  public static final CustomShieldType BLOOD = new CustomShieldType(it -> {
    it.onParry = context -> {
      applyBleedingAround(context.player(), context.player().getPos());
    };

    it.onMeleeParry = context -> {
      applyBleedingAround(context.player(), context.attacker.getPos());
      context.attacker.addStatusEffect(new StatusEffectInstance(
        MineCellsStatusEffects.BLEEDING, 100, 2, false, false, true
      ));
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
    it.particle = ParticleTypes.SNOWFLAKE;
  });

  public static final CustomShieldType GREED = new CustomShieldType(it -> {
    it.onMeleeParry = context -> {
      var serverWorld = (ServerWorld) context.player().getWorld();
      var lootTable = serverWorld
        .getServer()
        .getLootManager()
        .getLootTable(MineCells.createId("gameplay/greed_shield_parry"));

      var attacker = context.attacker();

      var lootContext = new LootContextParameterSet.Builder(serverWorld)
        .add(LootContextParameters.THIS_ENTITY, attacker)
        .add(LootContextParameters.ORIGIN, attacker.getPos())
        .add(LootContextParameters.DAMAGE_SOURCE, context.source())
        .addOptional(LootContextParameters.KILLER_ENTITY, context.player())
        .addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, context.player())
        .build(LootContextTypes.ENTITY);

      lootTable.generateLoot(lootContext, stack -> {
        var item = new ItemEntity(serverWorld, attacker.getX(), attacker.getY() + attacker.getHeight() / 2.0, attacker.getZ(), stack);
        item.setPickupDelay(20);
        serverWorld.spawnEntity(item);
      });
    };
  });


  //#endregion

  //#region Class Definition
  private float blockDamageReduction = 0.5f;
  private float blockAngle = 90f;
  private float parryAngle = 90f;
  private float parryTime = 5f;
  private float parryDamage = 6.0f;
  private int cooldown = 20;
  private int cooldownAfterParry = 5;

  private ParticleEffect particle = null;

  private Consumer<ShieldUseContext> onUse = context -> {};
  private Consumer<DamageContext> onParry = context -> {};
  private Consumer<MeleeDamageContext> onMeleeParry = context -> {};
  private Consumer<RangedDamageContext> onRangedParry = context -> {};

  private Consumer<ShieldHoldContext> onHold = context -> {};
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

  public float getParryAngle() {
    return parryAngle;
  }

  public float getParryTime() {
    return parryTime;
  }

  public float getParryDamage() {
    return parryDamage;
  }

  public ParticleEffect getParticle() {
    return particle;
  }

  public int getCooldown(boolean parried) {
    return parried ? cooldownAfterParry : cooldown;
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

  public void onHold(ShieldHoldContext context) {
    onHold.accept(context);
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

  public record ShieldHoldContext(
    PlayerEntity player,
    int useTicks
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
