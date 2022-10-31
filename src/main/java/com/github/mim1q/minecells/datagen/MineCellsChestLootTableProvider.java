package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class MineCellsChestLootTableProvider extends MineCellsLootTableHelper {
  public MineCellsChestLootTableProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, LootContextTypes.CHEST);
  }

  @Override
  public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
    biConsumer.accept(
      MineCells.createId("chests/prison"),
      LootTable.builder()
        .pool(simplePool(ItemEntry.builder(Items.ROTTEN_FLESH), 1, 4))
        .pool(simplePool(ItemEntry.builder(Items.GOLD_NUGGET), 2, 16))
        .pool(simplePool(ItemEntry.builder(Items.IRON_NUGGET), 2, 16))
        .pool(simplePool(ItemEntry.builder(Items.GOLD_INGOT), 0, 4))
        .pool(simplePool(ItemEntry.builder(Items.IRON_INGOT), 0, 4))
        .pool(simplePool(ItemEntry.builder(MineCellsBlocks.BIG_CHAIN), 0, 4))
        .pool(simplePool(ItemEntry.builder(Blocks.CHAIN), 2, 8))
        .pool(simplePool(ItemEntry.builder(MineCellsItems.ELEVATOR_MECHANISM), 0, 1))
        .pool(simplePool(randomChanceEntry(MineCellsItems.ASSASSINS_DAGGER, 0.1f, ConstantLootNumberProvider.create(1))))
        .pool(simplePool(randomChanceEntry(MineCellsItems.BLOOD_SWORD, 0.1f, ConstantLootNumberProvider.create(1))))
        .pool(simplePool(randomChanceEntry(MineCellsItems.HATTORIS_KATANA, 0.1f, ConstantLootNumberProvider.create(1))))
        .pool(simplePool(randomChanceEntry(MineCellsItems.HEALTH_FLASK, 0.2f, ConstantLootNumberProvider.create(1))))
    );
  }
}
