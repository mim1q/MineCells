package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.block.CageBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
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
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
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
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.HARDSTONE);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.BIG_CHAIN);
    generateSelfDroppingBlock(biConsumer, MineCellsBlocks.ELEVATOR_ASSEMBLER);
    generateBlock(biConsumer, MineCellsBlocks.BIOME_BANNER, MineCellsItems.BIOME_BANNER);

    biConsumer.accept(
      MineCellsBlocks.HANGED_SKELETON.getLootTableId(),
      LootTable.builder()
        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
          ItemEntry.builder(Items.BONE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 5)))
        ))
        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
          ItemEntry.builder(Items.SKELETON_SKULL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))
        ))
    );

    biConsumer.accept(
      MineCellsBlocks.CHAIN_PILE_BLOCK.getLootTableId(),
      LootTable.builder()
        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
          ItemEntry.builder(MineCellsBlocks.CHAIN_PILE_BLOCK)
            .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))))
        ))
        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
          ItemEntry.builder(Blocks.CHAIN)
            .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))).invert())
            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2)))
        ))
        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
          ItemEntry.builder(MineCellsBlocks.BIG_CHAIN)
            .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))).invert())
            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2)))
        ))
    );

    biConsumer.accept(
      MineCellsBlocks.CHAIN_PILE.getLootTableId(),
      LootTable.builder()
        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
          ItemEntry.builder(MineCellsBlocks.CHAIN_PILE)
            .conditionally(MatchToolLootCondition.builder(
              ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.ANY))
            ))
            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))
            .alternatively(
              ItemEntry.builder(MineCellsBlocks.BIG_CHAIN)
                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)))
            )
        ))
    );

    biConsumer.accept(
      MineCellsBlocks.CAGE.getLootTableId(),
      LootTable.builder()
        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(
          ItemEntry.builder(MineCellsItems.BROKEN_CAGE)
            .conditionally(
              new BlockStatePropertyLootCondition.Builder(MineCellsBlocks.CAGE)
                .properties(StatePredicate.Builder.create().exactMatch(CageBlock.BROKEN, true))
            )
            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))
            .alternatively(ItemEntry.builder(MineCellsItems.CAGE))
        ))
    );
  }

  public void generateSelfDroppingBlock(BiConsumer<Identifier, LootTable.Builder> biConsumer, Block block) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(block));
  }

  public void generateBlock(BiConsumer<Identifier, LootTable.Builder> biConsumer, Block block, ItemConvertible drop) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(drop));
  }
}
