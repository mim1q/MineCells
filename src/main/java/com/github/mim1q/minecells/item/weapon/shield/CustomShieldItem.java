package com.github.mim1q.minecells.item.weapon.shield;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;

public class CustomShieldItem extends Item {
  private static final int MAX_USE_DURATION = 60 * 60 * 20;
  public final CustomShieldType shieldType;

  public CustomShieldItem(Settings settings, CustomShieldType type) {
    super(settings);
    this.shieldType = type;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    shieldType.onUse(new CustomShieldType.ShieldUseContext(user));
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BLOCK;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return MAX_USE_DURATION;
  }

  public static boolean shouldTryToBlock(
    PlayerEntity player,
    DamageSource source,
    float maxAngleDifference
  ) {
    if (source.getPosition() == null) return false;

    var damageDirection = source.getPosition().subtract(player.getPos());
    var playerRotation = player.getRotationVector();

    var dotProduct = damageDirection.dotProduct(playerRotation);
    var aMagnitude = damageDirection.length();
    var bMagnitude = playerRotation.length();

    var angleDifference = (float) toDegrees(acos(dotProduct / (aMagnitude * bMagnitude)));

    return angleDifference <= maxAngleDifference;
  }
}
