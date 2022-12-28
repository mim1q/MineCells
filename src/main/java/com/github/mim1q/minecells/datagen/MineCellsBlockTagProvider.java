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
      .add(
        MineCellsBlocks.PUTRID_LOG,
        MineCellsBlocks.STRIPPED_PUTRID_LOG,
        MineCellsBlocks.PUTRID_WOOD,
        MineCellsBlocks.STRIPPED_PUTRID_WOOD
      );
    getOrCreateTagBuilder(BlockTags.PLANKS)
      .add(
        MineCellsBlocks.PUTRID_PLANKS
      );

    getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
      .add(
        // Prison Bricks
        MineCellsBlocks.PRISON_BRICKS,
        MineCellsBlocks.PRISON_BRICK_SLAB,
        MineCellsBlocks.PRISON_BRICK_STAIRS,
        MineCellsBlocks.PRISON_BRICK_WALL,
        // Prison Stone
        MineCellsBlocks.PRISON_STONE,
        MineCellsBlocks.PRISON_STONE_SLAB,
        MineCellsBlocks.PRISON_STONE_STAIRS,
        MineCellsBlocks.PRISON_STONE_WALL,
        // Prison Cobblestone
        MineCellsBlocks.PRISON_COBBLESTONE,
        MineCellsBlocks.PRISON_COBBLESTONE_SLAB,
        MineCellsBlocks.PRISON_COBBLESTONE_STAIRS,
        MineCellsBlocks.PRISON_COBBLESTONE_WALL,
        // Small Prison Bricks
        MineCellsBlocks.SMALL_PRISON_BRICKS,
        MineCellsBlocks.SMALL_PRISON_BRICK_SLAB,
        MineCellsBlocks.SMALL_PRISON_BRICK_STAIRS,
        MineCellsBlocks.SMALL_PRISON_BRICK_WALL,
        // Other
        MineCellsBlocks.BIG_CHAIN,
        MineCellsBlocks.CHAIN_PILE,
        MineCellsBlocks.CHAIN_PILE_BLOCK,
        MineCellsBlocks.CAGE,
        MineCellsBlocks.BROKEN_CAGE
      );

    getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
      .add(
        // Putrid Wood
        MineCellsBlocks.PUTRID_LOG,
        MineCellsBlocks.PUTRID_WOOD,
        MineCellsBlocks.STRIPPED_PUTRID_LOG,
        MineCellsBlocks.STRIPPED_PUTRID_WOOD,
        MineCellsBlocks.PUTRID_PLANKS,
        MineCellsBlocks.PUTRID_STAIRS,
        MineCellsBlocks.PUTRID_SLAB,
        MineCellsBlocks.PUTRID_FENCE,
        MineCellsBlocks.PUTRID_FENCE_GATE,
        MineCellsBlocks.PUTRID_DOOR,
        MineCellsBlocks.PUTRID_TRAPDOOR,
        // Other
        MineCellsBlocks.ELEVATOR_ASSEMBLER,
        MineCellsBlocks.CRATE,
        MineCellsBlocks.SMALL_CRATE,
        MineCellsBlocks.BRITTLE_BARREL,
        MineCellsBlocks.BIOME_BANNER
      );

    getOrCreateTagBuilder(BlockTags.WALLS)
      .add(
        MineCellsBlocks.PRISON_STONE_WALL,
        MineCellsBlocks.PRISON_COBBLESTONE_WALL,
        MineCellsBlocks.PRISON_BRICK_WALL,
        MineCellsBlocks.SMALL_PRISON_BRICK_WALL
      );

    getOrCreateTagBuilder(BlockTags.WOODEN_FENCES)
      .add(
        MineCellsBlocks.PUTRID_FENCE
      );
  }
}
