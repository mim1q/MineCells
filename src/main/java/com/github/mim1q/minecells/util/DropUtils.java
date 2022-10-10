package com.github.mim1q.minecells.util;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;

public class DropUtils {
  public static final LootCondition.Builder SHEARS = MatchToolLootCondition.builder(
    ItemPredicate.Builder.create().items(Items.SHEARS)
  );
  public static final LootCondition.Builder SILK_TOUCH = MatchToolLootCondition.builder(
    ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.ANY))
  );
  public static final LootCondition.Builder SHEARS_OR_SILK_TOUCH = SHEARS.or(SILK_TOUCH);
}
