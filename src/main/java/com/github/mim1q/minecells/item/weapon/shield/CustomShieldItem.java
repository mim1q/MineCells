package com.github.mim1q.minecells.item.weapon.shield;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static java.lang.Math.*;
import static net.minecraft.util.math.MathHelper.angleBetween;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class CustomShieldItem extends Item {
  private static final int MAX_USE_DURATION = 60 * 60 * 20;

  public CustomShieldItem(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
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
    float maxPitchDifference,
    float maxYawDifference
  ) {
    if (source.getPosition() == null) return false;
    var damageDirection = source.getPosition().relativize(player.getPos()).normalize();

    var damagePitch = wrapDegrees((float) toDegrees(asin(-damageDirection.y)));
    var damageYaw = wrapDegrees((float) toDegrees(atan2(damageDirection.x, damageDirection.z)));

    var playerPitch = player.getPitch();
    var playerYaw = player.getYaw();

    var pitchDifference = angleBetween(damagePitch, playerPitch);
    var yawDifference = angleBetween(damageYaw, playerYaw);

    MineCells.LOGGER.info("Pitch difference: {}, Yaw difference: {}", pitchDifference, yawDifference);

    return pitchDifference <= maxPitchDifference && yawDifference <= maxYawDifference;
  }
}
