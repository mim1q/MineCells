package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class CustomBowItem extends RangedWeaponItem {
  private final static int MAX_USE_TIME = 60 * 60 * 20;

  private final CustomArrowType arrowType;
  private final int drawTime;

  public CustomBowItem(Settings settings, CustomArrowType arrowType, int drawTime) {
    super(settings);
    this.arrowType = arrowType;
    this.drawTime = drawTime;
  }

  public CustomBowItem(Settings settings, CustomArrowType arrowType) {
    this(settings, arrowType, 10);
  }

  @Override public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) return;

    var ticks = getMaxUseTime(stack) - remainingUseTicks;
    if (ticks < getDrawTime(stack) || !user.isPlayer()) return;

    var arrow = new CustomArrowEntity(world, (PlayerEntity) user, arrowType, user.getEyePos(), stack);
    arrow.setVelocity(user.getRotationVec(1f).multiply(5.0));
    world.spawnEntity(arrow);
  }

  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    var stack = user.getStackInHand(hand);
    var hasProjectile = !user.getProjectileType(stack).isEmpty();
    if (hasProjectile || user.getAbilities().creativeMode) {
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
    return drawTime;
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
