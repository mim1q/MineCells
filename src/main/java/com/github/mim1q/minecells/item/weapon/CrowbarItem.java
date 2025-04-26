package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import com.github.mim1q.minecells.util.LegacyUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class CrowbarItem extends CustomMeleeWeapon {
  public CrowbarItem(Settings settings) {
    super("crowbar", settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    long lastDoorBreakTime = LegacyUtil.getOrCreateNbt(stack).getLong("lastDoorBreakTime");
    return attacker.getWorld().getTime() - lastDoorBreakTime < 20 * 5;
  }
}
