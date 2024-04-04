package com.github.mim1q.minecells.item.weapon.bow;

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

  public CustomBowItem(Settings settings, CustomArrowType arrowType) {
    super(settings);
    this.arrowType = arrowType;
  }

  @Override public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) return;
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
    return super.getMaxUseTime(stack);
  }
}
