package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class NutcrackerItem extends CustomMeleeWeapon {
  public NutcrackerItem(Settings settings) {
    super("nutcracker", settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    stack.damage(1, attacker, (user) -> user.sendToolBreakStatus(user.getActiveHand()));
    return super.postHit(stack, target, attacker);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (target == null) return false;

    return target instanceof LivingEntityAccessor living
      && (living.getMineCellsFlag(MineCellsEffectFlags.FROZEN) || living.getMineCellsFlag(MineCellsEffectFlags.STUNNED));
  }
}
