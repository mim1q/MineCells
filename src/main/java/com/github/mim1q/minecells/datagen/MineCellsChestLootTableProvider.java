package com.github.mim1q.minecells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class MineCellsChestLootTableProvider extends MineCellsLootTableHelper {
  public MineCellsChestLootTableProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, LootContextTypes.CHEST);
  }

  @Override
  public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {

  }
}
