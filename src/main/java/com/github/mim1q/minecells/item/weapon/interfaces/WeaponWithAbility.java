package com.github.mim1q.minecells.item.weapon.interfaces;

import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaponWithAbility {
  ValueCalculator getAbilityDamageCalculator();
  default float getAbilityDamage(ItemStack stack, LivingEntity attacker, @Nullable LivingEntity target) {
    return (float) getAbilityDamageCalculator().calculate(
      ValueCalculatorContext.create()
        .with(ValueCalculatorParameter.HOLDER, attacker)
        .with(ValueCalculatorParameter.TARGET, target)
        .with(ValueCalculatorParameter.HOLDER_STACK, stack)
    );
  }
  ValueCalculator getAbilityCooldownCalculator();
  default int getAbilityCooldown(ItemStack stack, LivingEntity attacker) {
    return (int) getAbilityCooldownCalculator().calculate(
      ValueCalculatorContext.create()
        .with(ValueCalculatorParameter.HOLDER, attacker)
        .with(ValueCalculatorParameter.HOLDER_STACK, stack)
    );
  }
  default void fillTooltip(List<Text> tooltip, boolean hold, String descriptionKey, ItemStack stack) {
  }
}
