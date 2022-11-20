package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodSwordItem extends SwordItem {
  private static final String TOOLTIP1_KEY = "item.minecells.blood_sword.tooltip1";
  private static final String TOOLTIP2_KEY = "item.minecells.blood_sword.tooltip2";
  private static final String EFFECT_KEY = "effect.minecells.bleeding";

  public BloodSwordItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (attacker instanceof ServerPlayerEntity player) {
      if (!player.getItemCooldownManager().isCoolingDown(this)) {
        player.getItemCooldownManager().set(this, 20 * 20);
        target.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.BLEEDING, 20 * 10, 2, false, false, true));
      }
      return true;
    }
    return false;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable(TOOLTIP1_KEY, Text.translatable(EFFECT_KEY).formatted(Formatting.RED)).formatted(Formatting.GRAY));
    tooltip.add(Text.translatable(TOOLTIP2_KEY).formatted(Formatting.DARK_GRAY));
  }
}
