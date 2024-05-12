package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.util.TextUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.mim1q.minecells.registry.MineCellsItems.CELL_INFUSED_STEEL_MATERIAL;

public class CursedSwordItem extends SwordItem {
  private static final String TOOLTIP = "item.minecells.cursed_sword.tooltip1";
  private static final String TOOLTIP2 = "item.minecells.cursed_sword.tooltip2";

  public CursedSwordItem(int attackDamage, float attackSpeed, Settings settings) {
    super(CELL_INFUSED_STEEL_MATERIAL, attackDamage, attackSpeed, settings);
  }

  @Override
  public Text getName(ItemStack stack) {
    return Text.translatable(getTranslationKey()).formatted(Formatting.RED);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, "item.minecells.cursed_sword.description");
  }
}
