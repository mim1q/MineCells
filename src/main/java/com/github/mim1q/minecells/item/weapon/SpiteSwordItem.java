package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class SpiteSwordItem extends CustomMeleeWeapon {
  public SpiteSwordItem(Settings settings) {
    super("spite_sword", settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    var lastAttacked = ((LivingEntityAccessor) attacker).getLastDamageTime();
    return attacker.getWorld().getTime() - lastAttacked <= 80;
  }
}
