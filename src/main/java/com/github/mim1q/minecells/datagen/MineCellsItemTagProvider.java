package com.github.mim1q.minecells.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class MineCellsItemTagProvider extends FabricTagProvider<Item> {
  public MineCellsItemTagProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, Registry.ITEM);
  }

  @Override
  protected void generateTags() {
  }
}
