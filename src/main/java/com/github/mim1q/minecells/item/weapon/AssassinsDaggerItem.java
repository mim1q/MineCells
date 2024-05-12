package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.util.TextUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.mim1q.minecells.registry.MineCellsItems.CELL_INFUSED_STEEL_MATERIAL;

public class AssassinsDaggerItem extends SwordItem implements CrittingWeapon {
  public AssassinsDaggerItem(int attackDamage, float attackSpeed, Settings settings) {
    super(CELL_INFUSED_STEEL_MATERIAL, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (target == null) return false;

    float difference = MathHelper.angleBetween(target.bodyYaw, attacker.getHeadYaw());
    return difference < 60.0F;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, "item.minecells.assassins_dagger.description", getAdditionalCritDamage(stack, null, null));
  }
}
