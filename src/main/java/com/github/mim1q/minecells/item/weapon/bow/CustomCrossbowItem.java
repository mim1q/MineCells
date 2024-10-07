package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CustomCrossbowItem extends CustomBowItem {
  public CustomCrossbowItem(Settings settings, CustomArrowType arrowType) {
    super(settings, arrowType);
  }

  protected CustomCrossbowItem(Settings settings, CustomArrowType arrowType, int maxProjectileCount) {
    super(settings, arrowType, maxProjectileCount);
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) return;

    var ticks = getMaxUseTime(stack) - remainingUseTicks;
    if (!CrossbowItem.isCharged(stack) && ticks > getDrawTime(user, stack) && user.isPlayer()) {
      CrossbowItem.setCharged(stack, true);
      var loaded = loadMaxProjectiles(world, (PlayerEntity) user, stack, user.getProjectileType(stack), maxProjectileCount);
      CustomBowItem.setLoadedProjectiles(stack, loaded);
    }
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    var stack = user.getStackInHand(hand);

    if (CrossbowItem.isCharged(stack)) {
      shoot(world, user, stack);
      CrossbowItem.setCharged(stack, false);
      stack.damage(1, user, player -> player.sendToolBreakStatus(hand));
      return TypedActionResult.consume(stack);
    }

    var hasAmmo = !user.getProjectileType(stack).isEmpty() || user.isCreative();
    if (!hasAmmo) {
      return TypedActionResult.fail(stack);
    }

    world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_CHARGE, SoundCategory.PLAYERS, 1f, 0.8f);
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override public UseAction getUseAction(ItemStack stack) {
    return UseAction.CROSSBOW;
  }

  @Override
  public float getFovMultiplier(PlayerEntity player, ItemStack stack) {
    return 1.0f;
  }
}
