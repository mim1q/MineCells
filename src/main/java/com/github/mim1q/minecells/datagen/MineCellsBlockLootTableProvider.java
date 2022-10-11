package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.util.DropUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class MineCellsBlockLootTableProvider extends SimpleFabricLootTableProvider {
  public MineCellsBlockLootTableProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, LootContextTypes.BLOCK);
  }

  @Override
  public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.PUTRID_LOG);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.STRIPPED_PUTRID_LOG);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.PUTRID_WOOD);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.STRIPPED_PUTRID_WOOD);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.PUTRID_PLANKS);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.PUTRID_STAIRS);
    biConsumer.accept(MineCellsBlocks.PUTRID_SLAB.getLootTableId(), BlockLootTableGenerator.slabDrops(MineCellsBlocks.PUTRID_SLAB));
    generateShearsOrSilkTouchDrop(biConsumer, MineCellsBlocks.WILTED_LEAVES, MineCellsBlocks.WILTED_LEAVES);
    generateShearsOrSilkTouchDrop(biConsumer, MineCellsBlocks.HANGING_WILTED_LEAVES, MineCellsBlocks.HANGING_WILTED_LEAVES);
    generateShearsOrSilkTouchDrop(biConsumer, MineCellsBlocks.ORANGE_WILTED_LEAVES, MineCellsBlocks.ORANGE_WILTED_LEAVES);
    generateShearsOrSilkTouchDrop(biConsumer, MineCellsBlocks.HANGING_ORANGE_WILTED_LEAVES, MineCellsBlocks.HANGING_ORANGE_WILTED_LEAVES);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.HARDSTONE);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.BIG_CHAIN);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.ELEVATOR_ASSEMBLER);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.CAGE);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.BROKEN_CAGE);
    generateBlock(biConsumer, MineCellsBlocks.BIOME_BANNER, MineCellsItems.BIOME_BANNER);

    biConsumer.accept(
      MineCellsBlocks.HANGED_SKELETON.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(ItemEntry.builder(Items.BONE), 2, 5))
        .pool(simplePool(ItemEntry.builder(Items.SKELETON_SKULL), 1))
    );

    biConsumer.accept(
      MineCellsBlocks.CHAIN_PILE_BLOCK.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(silkTouchEntry(MineCellsBlocks.CHAIN_PILE_BLOCK), 1))
        .pool(simplePool(noSilkTouchEntry(MineCellsBlocks.BIG_CHAIN), 1, 2))
        .pool(simplePool(noSilkTouchEntry(Blocks.CHAIN), 1, 2))
    );

    biConsumer.accept(
      MineCellsBlocks.CHAIN_PILE.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(silkTouchEntry(MineCellsBlocks.CHAIN_PILE), 1))
        .pool(simplePool(noSilkTouchEntry(MineCellsBlocks.BIG_CHAIN), 1))
    );
  }

  public static void generateSelfDroppingBlock(BiConsumer<Identifier, LootTable.Builder> biConsumer, Block block) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(block));
  }

  public static void generateBlock(
    BiConsumer<Identifier, LootTable.Builder> biConsumer,
    Block block,
    ItemConvertible drop
  ) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(drop));
  }

  public static LootPool.Builder simplePool(LeafEntry.Builder<?> entryBuilder, int count) {
    return LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
      entryBuilder.apply(
        SetCountLootFunction.builder(ConstantLootNumberProvider.create(count))
      )
    );
  }

  public static LootPool.Builder simplePool(LeafEntry.Builder<?> entryBuilder, int min, int max) {
    return LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
      entryBuilder.apply(
        SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max))
      )
    );
  }

  public static LeafEntry.Builder<?> conditionalEntry(
    ItemConvertible drop,
    LootCondition.Builder condition,
    LootNumberProvider count
  ) {
    return ItemEntry.builder(drop).conditionally(condition).apply(SetCountLootFunction.builder(count));
  }

  public static void generateShearsOrSilkTouchDrop(
    BiConsumer<Identifier, LootTable.Builder> biConsumer,
    Block block,
    ItemConvertible drop
  ) {
    biConsumer.accept(block.getLootTableId(), LootTable.builder()
      .pool(simplePool(conditionalEntry(drop, DropUtils.SHEARS_OR_SILK_TOUCH, ConstantLootNumberProvider.create(1)), 1))
    );
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
