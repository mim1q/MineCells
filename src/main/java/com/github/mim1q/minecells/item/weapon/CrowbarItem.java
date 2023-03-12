package com.github.mim1q.minecells.item.weapon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;

public class CrowbarItem extends SwordItem implements ICritWeapon {
  public CrowbarItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
    super(toolMaterial, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    long lastDoorBreakTime = stack.getOrCreateNbt().getLong("lastDoorBreakTime");
    return attacker.getWorld().getTime() - lastDoorBreakTime < 20 * 5;
  }

  @Override
  public float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return 4.0F;
  }
}
