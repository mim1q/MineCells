package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.network.ServerPlayerEntity;

public class BloodSwordItem extends SwordItem {
  public BloodSwordItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (attacker instanceof ServerPlayerEntity player) {
      if (!player.getItemCooldownManager().isCoolingDown(this)) {
        player.getItemCooldownManager().set(this, 200);
        target.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.BLEEDING, 20 * 15, 1));
      }
      return true;
    }
    return false;
  }
}
