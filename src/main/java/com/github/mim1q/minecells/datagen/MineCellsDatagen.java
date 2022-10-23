package com.github.mim1q.minecells.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MineCellsDatagen implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    fabricDataGenerator.addProvider(MineCellsModelProvider::new);
    fabricDataGenerator.addProvider(MineCellsBlockLootTableProvider::new);
    fabricDataGenerator.addProvider(MineCellsEntityLootTableProvider::new);
    fabricDataGenerator.addProvider(MineCellsChestLootTableProvider::new);
    fabricDataGenerator.addProvider(MineCellsRecipeProvider::new);
  }
}
