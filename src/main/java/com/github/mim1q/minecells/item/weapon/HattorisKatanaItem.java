package com.github.mim1q.minecells.item.weapon;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HattorisKatanaItem extends SwordItem {
  private static final String TOOLTIP_KEY = "item.minecells.hattoris_katana.tooltip";
  public HattorisKatanaItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable(TOOLTIP_KEY).formatted(Formatting.GRAY));
  }
}
