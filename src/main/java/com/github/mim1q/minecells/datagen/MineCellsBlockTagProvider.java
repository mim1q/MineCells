package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockTagProvider extends FabricTagProvider<Block> {
  public MineCellsBlockTagProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, Registry.BLOCK);
  }

  @Override
  protected void generateTags() {
    getOrCreateTagBuilder(BlockTags.LOGS)
      .add(MineCellsBlocks.PUTRID_LOG)
      .add(MineCellsBlocks.STRIPPED_PUTRID_LOG)
      .add(MineCellsBlocks.PUTRID_WOOD)
      .add(MineCellsBlocks.STRIPPED_PUTRID_WOOD);
    getOrCreateTagBuilder(BlockTags.PLANKS)
      .add(MineCellsBlocks.PUTRID_PLANKS);
  }
}
