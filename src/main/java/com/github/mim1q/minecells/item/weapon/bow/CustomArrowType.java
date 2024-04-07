package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.misc.MineCellsExplosion;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomArrowType {
  private static final HashMap<String, CustomArrowType> arrowTypes = new HashMap<>();

  public static final CustomArrowType DEFAULT = create("default");

  public static final CustomArrowType MARKSMAN = create("marksman", it -> {
    it.speed = 3;
    it.defaultDamage = 5f;
    it.additionalCritDamage = 9f;
    it.shouldCrit = context -> {
      var distanceSq = context.shotFromPos.squaredDistanceTo(context.hitPos);
      return distanceSq > 20 * 20;
    };
  });

  public static final CustomArrowType INFANTRY = create("infantry", it -> {
    it.defaultDamage = 6f;
    it.additionalCritDamage = 5f;
    it.shouldCrit = context -> {
      var distanceSq = context.shotFromPos.squaredDistanceTo(context.hitPos);
      return distanceSq < 10 * 10;
    };
  });

  public static final CustomArrowType ICE = create("ice", it -> {
    it.defaultDamage = 4f;
    it.onEntityHit = context -> {
      context.target.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.FROZEN, 100));
    };
    it.particle = ParticleTypes.SNOWFLAKE;
  });

  public static final CustomArrowType EXPLOSIVE_BOLT = create("explosive_bolt", it -> {
    it.defaultDamage = 0f;
    it.onBlockHit = context -> {
      MineCellsExplosion.explode(context.world, context.arrow, context.shooter, context.hitPos, 10f, 4f, Objects::nonNull);
      context.arrow.discard();
    };
    it.onEntityHit = context -> {
      MineCellsExplosion.explode(context.world, context.arrow, context.shooter, context.hitPos, 10f, 4f, Objects::nonNull);
      context.arrow.discard();
    };
    it.particle = ParticleTypes.SMOKE;
  });

  public static final CustomArrowType QUICK = create("quick", it -> {
    it.speed = 1.5f;
    it.defaultDamage = 7f;
    it.drawTime = 6;
    it.spread = 3f;
  });

  public static final CustomArrowType NERVES_OF_STEEL = create("nerves_of_steel", it -> {
    it.drawTime = 10;
    it.defaultDamage = 4f;
    it.additionalCritDamage = 7f;
    it.shouldCrit = context -> {
      var nbt = context.bow().getOrCreateNbt();
      return nbt.getBoolean("crit");
    };
  });

  public static final CustomArrowType HEAVY_BOLT = create("heavy_bolt", it -> {
    it.defaultDamage = 8f;
    it.speed = 0.6f;
    it.maxAge = 5;
    it.spread = 30;
  });

  public static final CustomArrowType MULTIPLE_NOCKS = create("multiple_nocks", it -> {

  });

  public static final CustomArrowType ENDLESS = create("endless", it -> {

  });

  //#region Class definition

  private final String name;
  private float defaultDamage = 5f;
  private float additionalCritDamage = 0f;
  private int drawTime = 20;
  private float speed = 2f;
  private int maxAge = 60 * 20;
  private float spread = 1f;
  private ParticleEffect particle = null;
  private Consumer<ArrowEntityHitContext> onEntityHit = context -> {};
  private Consumer<ArrowBlockHitContext> onBlockHit = context -> {};
  private Function<ArrowEntityHitContext, Boolean> shouldCrit = context -> false;

  private CustomArrowType(String name) {
    this.name = name;
  }

  public void onEntityHit(ArrowEntityHitContext context) {
    onEntityHit.accept(context);
  }

  public void onBlockHit(ArrowBlockHitContext context) {
    onBlockHit.accept(context);
  }

  public int getDrawTime() {
    return drawTime;
  }

  public boolean shouldCrit(ArrowEntityHitContext context) {
    return shouldCrit.apply(context);
  }

  public float getDamage() {
    return defaultDamage;
  }

  public float getAdditionalCritDamage() {
    return additionalCritDamage;
  }

  public String getName() {
    return name;
  }

  public ParticleEffect getParticle() {
    return particle;
  }

  public float getSpeed() {
    return speed;
  }

  public int getMaxAge() {
    return maxAge;
  }

  public float getSpread() {
    return spread;
  }

  //#endregion

  //#region Static methods
  protected static CustomArrowType create(String name, Consumer<CustomArrowType> setup) {
    CustomArrowType arrowType = new CustomArrowType(name);
    setup.accept(arrowType);
    arrowTypes.put(name, arrowType);
    return arrowType;
  }

  protected static CustomArrowType create(String name) {
    return create(name, it -> {});
  }

  public static CustomArrowType get(String name) {
    return arrowTypes.getOrDefault(name, DEFAULT);
  }

  public static Set<String> getAllNames() {
    return arrowTypes.keySet();
  }
  //#endregion

  //#region Context classes
  public record ArrowEntityHitContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    LivingEntity target,
    Vec3d shotFromPos,
    Vec3d hitPos,
    CustomArrowEntity arrow
  ) {}

  public record ArrowBlockHitContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    Vec3d shotFromPos,
    BlockPos hitBlockPos,
    Vec3d hitPos,
    CustomArrowEntity arrow
  ) {}
  //#endregion
}
