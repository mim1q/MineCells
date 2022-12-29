package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HealthFlaskItem extends Item {
  private static final String TOOLTIP_KEY = "item.minecells.health_flask.tooltip";

  public HealthFlaskItem(Settings settings) {
    super(settings);
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    user.heal(4.0F);
    user.removeStatusEffect(MineCellsStatusEffects.BLEEDING);
    user.removeStatusEffect(StatusEffects.POISON);
    stack.setCount(stack.getCount() - 1);
    return stack;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 40;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.DRINK;
  }

  @Override
  public SoundEvent getDrinkSound() {
    return SoundEvents.ENTITY_GENERIC_DRINK;
  }

  @Override
  public SoundEvent getEatSound() {
    return SoundEvents.ENTITY_GENERIC_DRINK;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable(TOOLTIP_KEY).formatted(net.minecraft.util.Formatting.GRAY));
  }
}
