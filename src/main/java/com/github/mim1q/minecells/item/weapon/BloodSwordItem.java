package com.github.mim1q.minecells.item.weapon;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;

public class BloodSwordItem extends SwordItem {
  public BloodSwordItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }
}
