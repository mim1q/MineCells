package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
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

  public CustomBowItem(Settings settings, CustomArrowType arrowType) {
    super(settings);
    this.arrowType = arrowType;
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) return;

    var ticks = getMaxUseTime(stack) - remainingUseTicks;
    if (ticks < getDrawTime(stack) || !user.isPlayer()) return;

    shoot(world, user, stack);
  }

  protected void shoot(World world, LivingEntity user, ItemStack stack) {
    world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_RELEASE, SoundCategory.PLAYERS, 0.7f, 0.9f);

    var velocity = user.getRotationVec(1f);
    spawnArrow(world, (PlayerEntity) user, stack, velocity);
  }

  protected void spawnArrow(World world, PlayerEntity user, ItemStack stack, Vec3d velocity) {
    var arrow = new CustomArrowEntity(world, user, arrowType, user.getEyePos(), stack);
    arrow.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ(), arrowType.getSpeed(), arrowType.getSpread());
    world.spawnEntity(arrow);
  }

  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    var stack = user.getStackInHand(hand);
    var hasProjectile = !user.getProjectileType(stack).isEmpty();
    if (hasProjectile || user.getAbilities().creativeMode) {
      world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_CHARGE, SoundCategory.PLAYERS, 1.0f, 0.8f);
      user.setCurrentHand(hand);
      return TypedActionResult.consume(stack);
    }

    return TypedActionResult.fail(stack);
  }

  @Override
  public Predicate<ItemStack> getProjectiles() {
    return item -> item.getItem() instanceof ArrowItem;
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

  public int getDrawTime(ItemStack stack) {
    return arrowType.getDrawTime();
  }

  public float getFovMultiplier(PlayerEntity player, ItemStack stack) {
    var multiplier = player.getItemUseTime() / (float) getDrawTime(stack);
    if (multiplier > 1.0F) {
      multiplier = 1.0F;
    } else {
      multiplier *= multiplier;
    }
    return 1.0f - multiplier * 0.15f;
  }
}
