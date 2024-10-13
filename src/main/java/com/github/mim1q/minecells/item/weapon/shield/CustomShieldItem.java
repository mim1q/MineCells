package com.github.mim1q.minecells.item.weapon.shield;

import com.github.mim1q.minecells.util.TextUtils;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ToolItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.mim1q.minecells.registry.MineCellsItems.CELL_INFUSED_STEEL;
import static com.github.mim1q.minecells.registry.MineCellsItems.CELL_INFUSED_STEEL_MATERIAL;
import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;

public class CustomShieldItem extends ToolItem {
  private static final int MAX_USE_DURATION = 60 * 60 * 20;
  public final CustomShieldType shieldType;

  public CustomShieldItem(Settings settings, CustomShieldType type) {
    super(CELL_INFUSED_STEEL_MATERIAL, settings);
    this.shieldType = type;
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    super.usageTick(world, user, stack, remainingUseTicks);
    shieldType.onHold(new CustomShieldType.ShieldHoldContext((PlayerEntity) user, MAX_USE_DURATION - remainingUseTicks));
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    shieldType.onUse(new CustomShieldType.ShieldUseContext(user));
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    var parried = hasParried(stack);
    var ctx = ValueCalculatorContext.create()
      .with(ValueCalculatorParameter.HOLDER, user)
      .with(ValueCalculatorParameter.HOLDER_STACK, stack);
    var cooldown = shieldType.getCooldown(ctx, parried);
    ((PlayerEntity) user).getItemCooldownManager().set(this, cooldown);
    setParried(stack, false);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BLOCK;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return MAX_USE_DURATION;
  }

  public static Float getAngleDifference(
    PlayerEntity player,
    DamageSource source
  ) {
    if (source.getPosition() == null) return null;

    var damageDirection = source.getPosition().subtract(player.getPos());
    var playerRotation = player.getRotationVector();

    var dotProduct = damageDirection.dotProduct(playerRotation);
    var aMagnitude = damageDirection.length();
    var bMagnitude = playerRotation.length();

    return (float) toDegrees(acos(dotProduct / (aMagnitude * bMagnitude)));
  }

  public static void setParried(ItemStack stack, boolean parried) {
    stack.getOrCreateNbt().putBoolean("parried", parried);
  }

  public static boolean hasParried(ItemStack stack) {
    return stack.getOrCreateNbt().getBoolean("parried");
  }

  @Override
  public boolean canRepair(ItemStack stack, ItemStack ingredient) {
    return ingredient.isOf(CELL_INFUSED_STEEL);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, this.getTranslationKey() + ".description");
  }
}
