package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.util.TextUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssassinsDaggerItem extends SwordItem implements CrittingWeapon {
  public AssassinsDaggerItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    System.out.println(target);
    float difference = MathHelper.angleBetween(target.bodyYaw, attacker.getHeadYaw());
    return difference < 60.0F;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, "item.minecells.assassins_dagger.description", getAdditionalCritDamage(stack, null, null));
  }
}
