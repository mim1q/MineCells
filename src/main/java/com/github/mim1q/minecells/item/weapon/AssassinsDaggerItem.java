package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class AssassinsDaggerItem extends CustomMeleeWeapon {
  public AssassinsDaggerItem(Settings settings) {
    super("assassins_dagger", settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (target == null) return false;

    float difference = MathHelper.angleBetween(target.bodyYaw, attacker.getHeadYaw());
    return difference < 60.0F;
  }
}
