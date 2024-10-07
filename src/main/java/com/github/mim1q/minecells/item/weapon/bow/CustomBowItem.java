package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class CustomBowItem extends RangedWeaponItem {
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

    var ticks = getMaxUseTime(stack) - remainingUseTicks;

    if (ticks < getDrawTime(user, stack) || !user.isPlayer()) return;

    var loaded = loadMaxProjectiles(world, (PlayerEntity) user, stack, user.getProjectileType(stack), maxProjectileCount);
    setLoadedProjectiles(stack, loaded);
    shoot(world, user, stack);
    stack.damage(1, user, player -> player.sendToolBreakStatus(user.getActiveHand()));
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
    world.spawnEntity(arrow);
    return arrow;
  }

  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    var stack = user.getStackInHand(hand);
    var projectileStack = user.getProjectileType(stack);
    var projectileNeeded = arrowType.getAmmoItem().isPresent();

    var hasProjectile = !projectileStack.isEmpty();

    if (hasProjectile || !projectileNeeded) {
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
    if (
      user.isCreative()
        || world.isClient
        || arrow.isEmpty()
        || arrowType.getAmmoItem().isEmpty()
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
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
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

  public static int getLoadedProjectiles(ItemStack bow) {
    var bowItem = (CustomBowItem) bow.getItem();
    return bowItem.maxProjectileCount == 1 ? 1 : bow.getOrCreateNbt().getInt("LoadedProjectiles");
  }

  public static void setLoadedProjectiles(ItemStack bow, int count) {
    var bowItem = (CustomBowItem) bow.getItem();
    if (bowItem.maxProjectileCount == 1) return;
    bow.getOrCreateNbt().putInt("LoadedProjectiles", count);
  }
}
