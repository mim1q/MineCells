package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.effect.BleedingStatusEffect;
import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class BloodSwordItem extends CustomMeleeWeapon {
  public BloodSwordItem(Settings settings) {
    super("blood_sword", settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    BleedingStatusEffect.apply(target, 20 * 5);
    return super.postHit(stack, target, attacker);
  }
}
