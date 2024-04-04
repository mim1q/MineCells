package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.misc.MineCellsExplosion;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomArrowType {
  private static final HashMap<String, CustomArrowType> arrowTypes = new HashMap<>();

  public static final CustomArrowType DEFAULT = create("default");

  public static final CustomArrowType MARKSMAN = create("marksman", it -> {
    it.damageAndCrit = context -> {
      var distanceSq = context.shotFromPos.squaredDistanceTo(context.hitPos);
      if (distanceSq > 20 * 20) {
        return new Pair<>(it.defaultDamage * 2.5f, true);
      }
      return new Pair<>(it.defaultDamage, false);
    };
  });

  public static final CustomArrowType INFANTRY = create("infantry", it -> {
    it.defaultDamage = 6f;
    it.damageAndCrit = context -> {
      var distanceSq = context.shotFromPos.squaredDistanceTo(context.hitPos);
      if (distanceSq < 10 * 10) {
        return new Pair<>(it.defaultDamage * 2, true);
      }
      return new Pair<>(it.defaultDamage, false);
    };
  });

  public static final CustomArrowType ICE = create("ice", it -> {
    it.defaultDamage = 4f;
    it.onEntityHit = context -> {
      context.target.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.FROZEN, 100));
    };
  });

  public static final CustomArrowType EXPLOSIVE_BOLT = create("explosive_bolt", it -> {
    it.defaultDamage = 0f;
    it.onBlockHit = context -> {
      MineCellsExplosion.explode(context.world, context.shooter, context.shooter, context.hitPos, 2f, 3f);
    };
    it.onEntityHit = context -> {
      MineCellsExplosion.explode(context.world, context.shooter, context.shooter, context.hitPos, 2f, 3f);
    };
  });

  //#region Class definition
  private final String name;
  private float defaultDamage = 5f;
  private Consumer<ArrowEntityHitContext> onEntityHit = context -> {};
  private Consumer<ArrowBlockHitContext> onBlockHit = context -> {};
  private Function<ArrowEntityHitContext, Pair<Float, Boolean>> damageAndCrit = (context) -> new Pair<>(defaultDamage, false);

  private CustomArrowType(String name) {
    this.name = name;
  }

  public void onEntityHit(ArrowEntityHitContext context) {
    onEntityHit.accept(context);
  }

  public void onBlockHit(ArrowBlockHitContext context) {
    onBlockHit.accept(context);
  }

  public Pair<Float, Boolean> getDamageAndCrit(ArrowEntityHitContext context) {
    return damageAndCrit.apply(context);
  }

  public String getName() {
    return name;
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
    return create(name, it -> {
    });
  }

  public static CustomArrowType get(String name) {
    return arrowTypes.getOrDefault(name, DEFAULT);
  }
  //#endregion

  //#region Context classes
  public record ArrowShotContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    Vec3d shotFromPos
  ) {
    public ArrowEntityHitContext entityHit(LivingEntity target, Vec3d hitPos) {
      return new ArrowEntityHitContext(world, bow, shooter, target, shotFromPos, hitPos);
    }

    public ArrowBlockHitContext blockHit(Vec3d hitPos, BlockPos hitBlockPos) {
      return new ArrowBlockHitContext(world, bow, shooter, shotFromPos, hitBlockPos, hitPos);
    }
  }

  public record ArrowEntityHitContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    LivingEntity target,
    Vec3d shotFromPos,
    Vec3d hitPos
  ) {}

  public record ArrowBlockHitContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    Vec3d shotFromPos,
    BlockPos hitBlockPos,
    Vec3d hitPos
  ) {}
  //#endregion
}
