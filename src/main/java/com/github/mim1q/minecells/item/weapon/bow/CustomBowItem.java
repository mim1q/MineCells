package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.LegacyUtil;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static com.github.mim1q.minecells.item.MineCellsItemTags.*;

public class CustomBowItem extends RangedWeaponItem implements CustomArrowShooter {
  private final static int MAX_USE_TIME = 60 * 60 * 20;

  protected final CustomArrowType arrowType;
  protected final int maxProjectileCount;

  protected CustomBowItem(Settings settings, CustomArrowType arrowType, int maxProjectileCount) {
    super(settings);
    this.arrowType = arrowType;
    this.maxProjectileCount = maxProjectileCount;
  }

  public CustomBowItem(Settings settings, CustomArrowType arrowType) {
    this(settings, arrowType, 1);
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) return;

    var ticks = getMaxUseTime(stack, user) - remainingUseTicks;

    if (ticks < getDrawTime(user, stack) || !user.isPlayer()) return;

    var loaded = loadMaxProjectiles(world, (PlayerEntity) user, stack, user.getProjectileType(stack), maxProjectileCount);
    setLoadedProjectiles(stack, loaded);
    shoot(world, user, stack);
    stack.damage(1, user, user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
  }

  protected void shoot(World world, LivingEntity user, ItemStack stack) {
    world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_RELEASE, SoundCategory.PLAYERS, 0.5f, 0.9f);

    var velocity = user.getRotationVec(1f);
    spawnArrow(world, (PlayerEntity) user, stack, velocity);
    setLoadedProjectiles(stack, 0);
  }

  protected CustomArrowEntity spawnArrow(World world, PlayerEntity user, ItemStack stack, Vec3d velocity) {
    var arrow = new CustomArrowEntity(world, user, arrowType, user.getEyePos(), stack);
    var context = ValueCalculatorContext.create()
      .with(ValueCalculatorParameter.HOLDER, user)
      .with(ValueCalculatorParameter.HOLDER_STACK, stack);

    arrow.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ(), arrowType.getSpeed(context), arrowType.getSpread(context));

    var flame = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.FLAME);
    var punch = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PUNCH);

    var flameLevel = flame.map(it -> EnchantmentHelper.getLevel(it, stack)).orElse(0);
    var punchLevel = punch.map(it -> EnchantmentHelper.getLevel(it, stack)).orElse(0);

    if (flameLevel > 0) {
      arrow.setOnFireFor(1000);
    }
//    arrow.setPunch(punchLevel, stack);
    world.spawnEntity(arrow);
    return arrow;
  }

  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    var stack = user.getStackInHand(hand);
    var projectileStack = user.getProjectileType(stack);
    var infinity = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.INFINITY);
    boolean hasInfinity = infinity.map(it -> EnchantmentHelper.getLevel(it, stack) > 0).orElse(false);
    var projectileNeeded = arrowType.getAmmoItem().isPresent();

    var hasProjectile = !projectileStack.isEmpty();

    if (hasProjectile || hasInfinity || !projectileNeeded) {
      world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_CHARGE, SoundCategory.PLAYERS, 0.5f, 0.8f);
      user.setCurrentHand(hand);
      return TypedActionResult.consume(stack);
    }

    return TypedActionResult.fail(stack);
  }

  @Override
  public Predicate<ItemStack> getProjectiles() {
    return item -> arrowType.getAmmoItem()
      .map(arrow -> item.getItem() == arrow)
      .orElse(true);
  }

  protected final int loadMaxProjectiles(World world, PlayerEntity user, ItemStack bow, ItemStack arrow, int maxCount) {
    var infinity = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.INFINITY);
    var hasInfinity = infinity.map(it -> EnchantmentHelper.getLevel(it, bow) > 0).orElse(false);
    if (
      user.isCreative()
        || world.isClient
        || arrow.isEmpty()
        || arrowType.getAmmoItem().isEmpty()
        || hasInfinity
    ) {
      return maxCount;
    }

    for (int i = 0; i < maxCount; i++) {
      if (arrow.isEmpty()) {
        return i;
      }
      arrow.decrement(1);
    }
    return maxCount;
  }

  @Override
  public int getRange() {
    return 100;
  }

  @Override
  protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack, LivingEntity user) {
    return MAX_USE_TIME;
  }

  public int getDrawTime(LivingEntity user, ItemStack stack) {
    var context = ValueCalculatorContext.create()
      .with(ValueCalculatorParameter.HOLDER, user)
      .with(ValueCalculatorParameter.HOLDER_STACK, stack);
    return arrowType.getDrawTime(context);
  }

  public float getFovMultiplier(PlayerEntity player, ItemStack stack) {
    var multiplier = player.getItemUseTime() / (float) getDrawTime(player, stack);
    if (multiplier > 1.0F) {
      multiplier = 1.0F;
    } else {
      multiplier *= multiplier;
    }
    return 1.0f - multiplier * 0.15f;
  }

  @Override
  public boolean canBeEnchantedWith(
    ItemStack stack,
    RegistryEntry<Enchantment> enchantment,
    EnchantingContext context
  ) {
    var enchantId = enchantment.getKey().orElseThrow();
    if (
      enchantId == Enchantments.INFINITY && stack.isIn(BOWS_ACCEPTING_INFINITY)
        || enchantId == Enchantments.PUNCH && stack.isIn(BOWS_ACCEPTING_PUNCH)
        || enchantId == Enchantments.POWER && stack.isIn(BOWS_ACCEPTING_POWER)
        || enchantId == Enchantments.FLAME && stack.isIn(BOWS_ACCEPTING_FLAME)
        || enchantId == Enchantments.QUICK_CHARGE && stack.isIn(BOWS_ACCEPTING_QUICK_CHARGE)
    ) {
      return true;
    }

    return super.canBeEnchantedWith(stack, enchantment, context);
  }

  public static int getLoadedProjectiles(ItemStack bow) {
    var bowItem = (CustomBowItem) bow.getItem();
    return bowItem.maxProjectileCount == 1 ? 1 : LegacyUtil.getOrCreateNbt(bow).getInt("LoadedProjectiles");
  }

  public static void setLoadedProjectiles(ItemStack bow, int count) {
    var bowItem = (CustomBowItem) bow.getItem();
    if (bowItem.maxProjectileCount == 1) return;
    var nbt = LegacyUtil.getOrCreateNbt(bow);
    nbt.putInt("LoadedProjectiles", count);
    LegacyUtil.writeNbt(bow, nbt);
  }

  @Override
  public CustomArrowType getArrowType() {
    return arrowType;
  }
}
