package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.util.TextUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrowbarItem extends SwordItem implements CrittingWeapon {
  public CrowbarItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
    super(toolMaterial, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    long lastDoorBreakTime = stack.getOrCreateNbt().getLong("lastDoorBreakTime");
    return attacker.getWorld().getTime() - lastDoorBreakTime < 20 * 5;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, "item.minecells.crowbar.description", getAdditionalCritDamage(stack, null, null));
  }
}
