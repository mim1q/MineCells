package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;

public class BalancedBladeItem extends SwordItem implements CrittingWeapon {
  public BalancedBladeItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
    super(toolMaterial, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    stack.setHolder(attacker);
    if (attacker instanceof PlayerEntityAccessor player) {
      player.addBalancedBladeStack();
    }
    return super.postHit(stack, target, attacker);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    return true;
  }

  @Override
  public float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    if (attacker instanceof PlayerEntityAccessor player) {
      return player.getBalancedBladeStacks() * 0.75F;
    }
    return 0.0F;
  }

  @Override
  public boolean shouldPlayCritSound(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    if (attacker instanceof PlayerEntityAccessor player) {
      return player.getBalancedBladeStacks() >= 9;
    }
    return false;
  }
}
