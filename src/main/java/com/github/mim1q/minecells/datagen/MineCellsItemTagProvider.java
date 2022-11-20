package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.registry.Registry;

public class MineCellsItemTagProvider extends FabricTagProvider<Item> {
  public MineCellsItemTagProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, Registry.ITEM);
  }

  @Override
  protected void generateTags() {
    getOrCreateTagBuilder(ItemTags.LOGS)
      .add(MineCellsBlocks.PUTRID_LOG.asItem())
      .add(MineCellsBlocks.STRIPPED_PUTRID_LOG.asItem())
      .add(MineCellsBlocks.PUTRID_WOOD.asItem())
      .add(MineCellsBlocks.STRIPPED_PUTRID_WOOD.asItem());
    getOrCreateTagBuilder(ItemTags.PLANKS)
      .add(MineCellsBlocks.PUTRID_PLANKS.asItem());
  }
}
