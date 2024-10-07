package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.effect.BleedingStatusEffect;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.world.MineCellsExplosion;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomArrowType {
  private static final HashMap<String, CustomArrowType> arrowTypes = new HashMap<>();

  public static final CustomArrowType DEFAULT = create("default");

  public static final CustomArrowType MARKSMAN = create("marksman", it -> {
    it.shouldCrit = context -> {
      var distanceSq = context.shotFromPos.squaredDistanceTo(context.hitPos);
      return distanceSq > 24 * 24;
    };
  });

  public static final CustomArrowType INFANTRY = create("infantry", it -> {
    it.shouldCrit = context -> {
      var distanceSq = context.shotFromPos.squaredDistanceTo(context.hitPos);
      return distanceSq < 10 * 10;
    };
  });

  public static final CustomArrowType ICE = create("ice", it -> {
    it.onEntityHit = context -> {
      context.target.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.FROZEN, 100));
    };
    it.particle = ParticleTypes.SNOWFLAKE;
    it.ammo = () -> MineCellsItems.ICE_ARROW;
  });

  public static final CustomArrowType EXPLOSIVE_BOLT = create("explosive_bolt", it -> {
    it.onBlockHit = context -> {
      MineCellsExplosion.explode(context.world, context.arrow, context.shooter, context.hitPos, 10f, 4f, Objects::nonNull);
      context.arrow.discard();
    };
    it.onEntityHit = context -> {
      MineCellsExplosion.explode(context.world, context.arrow, context.shooter, context.hitPos, 10f, 4f, Objects::nonNull);
      context.arrow.discard();
    };
    it.particle = ParticleTypes.SMOKE;
    it.ammo = () -> MineCellsItems.EXPLOSIVE_BOLT;
  });

  public static final CustomArrowType QUICK = create("quick", it -> {
    it.damageSourceFactory = (world, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(world, shooter);
  });

  public static final CustomArrowType NERVES_OF_STEEL = create("nerves_of_steel", it -> {
    it.shouldCrit = context -> {
      var nbt = context.bow().getOrCreateNbt();
      return nbt.getBoolean("crit");
    };
  });

  public static final CustomArrowType HEAVY_BOLT = create("heavy_bolt", it -> {
    it.maxAge = 15;
    it.damageSourceFactory = (world, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(world, shooter);
  });

  public static final CustomArrowType MULTIPLE_NOCKS = create("multiple_nocks", it -> {

  });

  public static final CustomArrowType ENDLESS = create("endless", it -> {
    it.ammo = () -> null;
  });

  private static void placeFire(ArrowBlockHitContext context) {
    var blockPos = context.hitBlockPos().add(context.hitFace().getVector());
    var fireState = Blocks.FIRE.getPlacementState(new ItemPlacementContext(
      context.shooter(),
      context.shooter().getActiveHand(),
      context.bow(),
      new BlockHitResult(
        context.hitPos(),
        context.hitFace(),
        context.hitBlockPos(),
        false
      )
    ));
    if (context.world.getBlockState(blockPos).isReplaceable()) {
      context.world.setBlockState(blockPos, fireState);
    }
  }

  public static final CustomArrowType FIREBRANDS = create("firebrands", it -> {
    it.onEntityHit = context -> context.target.setOnFireFor(5);
    it.onBlockHit = context -> {
      placeFire(context);
      context.arrow.discard();
    };
    it.particle = ParticleTypes.FLAME;
  });

  public static final CustomArrowType THROWING_KNIFE = create("throwing_knife", it -> {
    it.onEntityHit = context -> BleedingStatusEffect.apply(context.target, 20 * 4);
    it.ammo = () -> MineCellsItems.THROWING_KNIFE;
    it.particle = MineCellsParticles.DROP.get(0xDD3000);
    it.damageSourceFactory = (world, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(world, shooter);
  });

  //#region Class definition

  private final String name;
  private final ValueCalculator defaultDamage;
  private final ValueCalculator additionalCritDamage;
  private final ValueCalculator drawTime;
  private final ValueCalculator speed;
  private final ValueCalculator spread;
  private final ValueCalculator cooldown;
  private int maxAge = 60 * 20;
  private ParticleEffect particle = null;
  private Consumer<ArrowEntityHitContext> onEntityHit = context -> {
  };
  private Consumer<ArrowBlockHitContext> onBlockHit = context -> {
  };
  private Function<ArrowEntityHitContext, Boolean> shouldCrit = context -> false;
  private DamageSourceFactory damageSourceFactory = (world, arrow, shooter) -> world.getDamageSources().mobProjectile(arrow, shooter);
  private Supplier<Item> ammo = () -> Items.ARROW;
  private final String translationKey;

  private CustomArrowType(String name) {
    this.name = name;
    this.translationKey = "entity.minecells.custom_arrow." + name;

    defaultDamage = new ValueCalculator(MineCells.createId("ranged/" + name), "damage", 1.0);
    additionalCritDamage = new ValueCalculator(MineCells.createId("ranged/" + name), "crit_damage", 0.0);
    drawTime = new ValueCalculator(MineCells.createId("ranged/" + name), "draw_time", 1.0);
    speed = new ValueCalculator(MineCells.createId("ranged/" + name), "speed", 2.0);
    spread = new ValueCalculator(MineCells.createId("ranged/" + name), "spread", 1.0);
    cooldown = new ValueCalculator(MineCells.createId("ranged/" + name), "cooldown", 0.0);
  }

  public void onEntityHit(ArrowEntityHitContext context) {
    onEntityHit.accept(context);
  }

  public void onBlockHit(ArrowBlockHitContext context) {
    onBlockHit.accept(context);
  }

  public int getDrawTime(ValueCalculatorContext context) {
    return (int) (20 * drawTime.calculate(context));
  }

  public boolean shouldCrit(ArrowEntityHitContext context) {
    return shouldCrit.apply(context);
  }

  public float getDamage(ValueCalculatorContext context) {
    return (float) defaultDamage.calculate(context);
  }

  public float getAdditionalCritDamage(ValueCalculatorContext context) {
    return (float) additionalCritDamage.calculate(context);
  }

  public String getName() {
    return name;
  }

  public ParticleEffect getParticle() {
    return particle;
  }

  public float getSpeed(ValueCalculatorContext context) {
    return (float) speed.calculate(context);
  }

  public int getMaxAge() {
    return maxAge;
  }

  public float getSpread(ValueCalculatorContext context) {
    return (float) spread.calculate(context);
  }

  public int getCooldown(ValueCalculatorContext context) {
    return (int) (cooldown.calculate(context) * 20);
  }

  public Optional<Item> getAmmoItem() {
    return Optional.ofNullable(ammo.get());
  }

  public DamageSource getDamageSource(World world, CustomArrowEntity arrow, LivingEntity shooter) {
    return damageSourceFactory.create(world, arrow, shooter);
  }

  public Text getTranslation() {
    return Text.translatable(translationKey);
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

  public static Set<String> getAllNames() {
    return arrowTypes.keySet();
  }
  //#endregion

  //#region Context classes
  @FunctionalInterface
  public interface DamageSourceFactory {
    DamageSource create(World world, CustomArrowEntity arrow, LivingEntity shooter);
  }

  public record ArrowEntityHitContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    LivingEntity target,
    Vec3d shotFromPos,
    Vec3d hitPos,
    CustomArrowEntity arrow
  ) {
  }

  public record ArrowBlockHitContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    Vec3d shotFromPos,
    BlockPos hitBlockPos,
    Vec3d hitPos,
    Direction hitFace,
    CustomArrowEntity arrow
  ) {
  }
  //#endregion
}
