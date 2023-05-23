package com.github.mim1q.minecells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class MineCellsBlockLootTableProvider extends MineCellsLootTableHelper {
  public MineCellsBlockLootTableProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, LootContextTypes.BLOCK);
  }

  @Override
  public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
  }

  public static void generateSelfDroppingBlock(BiConsumer<Identifier, LootTable.Builder> biConsumer, Block block) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(block));
  }

  public static void generateSelfDroppingBlocks(BiConsumer<Identifier, LootTable.Builder> biConsumer, Block... blocks) {
    for (Block block : blocks) {
      generateSelfDroppingBlock(biConsumer, block);
    }
  }

  public static void generateBlock(
    BiConsumer<Identifier, LootTable.Builder> biConsumer,
    Block block,
    ItemConvertible drop
  ) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(drop));
  }

  public static LeafEntry.Builder<?> silkTouchEntry(ItemConvertible item, boolean silkTouch) {
    var condition = MatchToolLootCondition.builder(
      ItemPredicate.Builder.create().enchantment(
        new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))
      )
    );
    return ItemEntry.builder(item).conditionally(silkTouch ? condition : condition.invert());
  }

  public static LeafEntry.Builder<?> silkTouchEntry(ItemConvertible item) {
    return silkTouchEntry(item, true);
  }

  public static LeafEntry.Builder<?> noSilkTouchEntry(ItemConvertible item) {
    return silkTouchEntry(item, false);
  }
}
