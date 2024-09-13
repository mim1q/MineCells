package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.effect.BleedingStatusEffect;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.world.MineCellsExplosion;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
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
    it.speed = 3;
    it.defaultDamage = 5f;
    it.additionalCritDamage = 11f;
    it.shouldCrit = context -> {
      var distanceSq = context.shotFromPos.squaredDistanceTo(context.hitPos);
      return distanceSq > 24 * 24;
    };
  });

  public static final CustomArrowType INFANTRY = create("infantry", it -> {
    it.defaultDamage = 6f;
    it.additionalCritDamage = 6f;
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
    it.ammo = () -> MineCellsItems.ICE_ARROW;
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
    it.ammo = () -> MineCellsItems.EXPLOSIVE_BOLT;
  });

  public static final CustomArrowType QUICK = create("quick", it -> {
    it.speed = 2.2f;
    it.defaultDamage = 6f;
    it.drawTime = 6;
    it.spread = 3f;
    it.damageSourceFactory = (world, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(world, shooter);
  });

  public static final CustomArrowType NERVES_OF_STEEL = create("nerves_of_steel", it -> {
    it.drawTime = 10;
    it.defaultDamage = 5f;
    it.additionalCritDamage = 9f;
    it.shouldCrit = context -> {
      var nbt = context.bow().getOrCreateNbt();
      return nbt.getBoolean("crit");
    };
  });

  public static final CustomArrowType HEAVY_BOLT = create("heavy_bolt", it -> {
    it.defaultDamage = 4f;
    it.drawTime = 35;
    it.speed = 0.6f;
    it.maxAge = 15;
    it.spread = 45;
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
    it.defaultDamage = 4f;
    it.speed = 1f;
    it.onEntityHit = context -> context.target.setOnFireFor(5);
    it.onBlockHit = context -> {
      placeFire(context);
      context.arrow.discard();
    };
    it.particle = ParticleTypes.FLAME;
    it.cooldown = 20;
  });

  public static final CustomArrowType THROWING_KNIFE = create("throwing_knife", it -> {
    it.defaultDamage = 4f;
    it.speed = 1.75f;
    it.spread = 0.5f;
    it.onEntityHit = context -> BleedingStatusEffect.apply(context.target, 20 * 4);
    it.cooldown = 5;
    it.ammo = () -> MineCellsItems.THROWING_KNIFE;
    it.particle = MineCellsParticles.DROP.get(0xDD3000);
    it.damageSourceFactory = (world, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(world, shooter);
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
  private DamageSourceFactory damageSourceFactory = (world, arrow, shooter) -> world.getDamageSources().mobProjectile(arrow, shooter);
  private int cooldown = 0;
  private Supplier<Item> ammo = () -> Items.ARROW;
  private final String translationKey;

  private CustomArrowType(String name) {
    this.name = name;
    this.translationKey = "entity.minecells.custom_arrow." + name;
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

  public int getCooldown() {
    return cooldown;
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
  ) {}

  public record ArrowBlockHitContext(
    ServerWorld world,
    ItemStack bow,
    PlayerEntity shooter,
    Vec3d shotFromPos,
    BlockPos hitBlockPos,
    Vec3d hitPos,
    Direction hitFace,
    CustomArrowEntity arrow
  ) {}
  //#endregion
}
