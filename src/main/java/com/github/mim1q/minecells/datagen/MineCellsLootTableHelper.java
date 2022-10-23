package com.github.mim1q.minecells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public abstract class MineCellsLootTableHelper extends SimpleFabricLootTableProvider {

  public MineCellsLootTableHelper(FabricDataGenerator dataGenerator, LootContextType lootContextType) {
    super(dataGenerator, lootContextType);
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
}
