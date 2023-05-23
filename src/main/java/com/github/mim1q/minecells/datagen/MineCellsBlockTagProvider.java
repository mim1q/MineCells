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
//    getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
//      .add(
//        MineCellsBlocks.BIG_CHAIN,
//        MineCellsBlocks.CHAIN_PILE,
//        MineCellsBlocks.CHAIN_PILE_BLOCK,
//        MineCellsBlocks.CAGE,
//        MineCellsBlocks.BROKEN_CAGE
//      );

    getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
      .add(
        // Putrid Wood
        MineCellsBlocks.PUTRID_BOARDS,
        // Other
        MineCellsBlocks.ELEVATOR_ASSEMBLER,
        MineCellsBlocks.CRATE,
        MineCellsBlocks.SMALL_CRATE,
        MineCellsBlocks.BRITTLE_BARREL,
        MineCellsBlocks.BIOME_BANNER
      );
  }
}
