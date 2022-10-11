package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class MineCellsModelProvider extends FabricModelProvider {

  public MineCellsModelProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator);
  }

  @Override
  public void generateBlockStateModels(BlockStateModelGenerator generator) {
    generator.registerSimpleCubeAll(MineCellsBlocks.HARDSTONE);
    generator.registerAxisRotated(MineCellsBlocks.PUTRID_LOG, TexturedModel.CUBE_COLUMN);
    generator.registerAxisRotated(MineCellsBlocks.STRIPPED_PUTRID_LOG, TexturedModel.CUBE_COLUMN);
    generator.registerAxisRotated(MineCellsBlocks.PUTRID_WOOD, TexturedModel.CUBE_ALL);
    generator.registerAxisRotated(MineCellsBlocks.STRIPPED_PUTRID_WOOD, TexturedModel.CUBE_ALL);
    generator.registerSimpleCubeAll(MineCellsBlocks.PUTRID_PLANKS);
    generator.registerParentedItemModel(MineCellsBlocks.PUTRID_SLAB, MineCells.createId("block/putrid_slab/putrid_slab"));
    generator.registerParentedItemModel(MineCellsBlocks.PUTRID_STAIRS, MineCells.createId("block/putrid_stairs/putrid_stairs"));
    generator.registerSingleton(MineCellsBlocks.WILTED_LEAVES, TexturedModel.LEAVES);
    generator.registerParentedItemModel(MineCellsBlocks.HANGING_WILTED_LEAVES, MineCells.createId("block/hanging_wilted_leaves"));

    generator.registerSimpleCubeAll(MineCellsBlocks.ELEVATOR_ASSEMBLER);
    generator.registerSimpleCubeAll(MineCellsBlocks.CHAIN_PILE_BLOCK);
    generator.registerAxisRotated(MineCellsBlocks.CRATE, TexturedModel.CUBE_COLUMN);
    generator.registerStateWithModelReference(MineCellsBlocks.BIOME_BANNER, Blocks.WHITE_WOOL);
    generator.registerStateWithModelReference(MineCellsBlocks.PRISON_BOX, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlocks.CONJUNCTIVIUS_BOX, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlocks.SHOCKER_BOX, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlocks.KINGDOM_PORTAL_CORE, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlocks.SEWAGE, Blocks.WATER);
    generator.registerStateWithModelReference(MineCellsBlocks.ANCIENT_SEWAGE, Blocks.WATER);
  }

  @Override
  public void generateItemModels(ItemModelGenerator generator) {
    for (SpawnEggItem spawnEggItem : MineCellsEntities.getSpawnEggs()) {
      registerSpawnEggModel(generator, spawnEggItem);
    }
    for (Item item : MineCellsItems.getSimpleItems()) {
      generator.register(item, Models.GENERATED);
    }
    generator.register(MineCellsBlocks.CAGE.asItem(), Models.GENERATED);
    generator.register(MineCellsBlocks.BROKEN_CAGE.asItem(), Models.GENERATED);
    generator.register(MineCellsItems.ASSASSINS_DAGGER, Models.HANDHELD);
  }

  public static void registerSpawnEggModel(ItemModelGenerator generator, SpawnEggItem item) {
    generator.register(item, new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
  }
}
