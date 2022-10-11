package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssassinsDaggerItem extends AbstractCritWeaponItem {
  public AssassinsDaggerItem(Settings settings) {
    super(ToolMaterials.IRON, 5.0F, 3.0F, -2.0F, settings);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    float difference = MathHelper.angleBetween(target.bodyYaw, attacker.getHeadYaw());
    return difference < 60.0F;
  }

  @Override
  public DamageSource getDamageSource(ItemStack stack, LivingEntity attacker, LivingEntity target) {
    return MineCellsDamageSource.backstab(attacker);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    MutableText text = Text.literal("+" + this.critAttackDamage).formatted(Formatting.RED);
    tooltip.add(Text.translatable("item.minecells.assassins_dagger.tooltip", text).formatted(Formatting.GRAY));
  }
}
