package com.github.mim1q.minecells.datagen;

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
    // Stone
    generator.registerSimpleCubeAll(MineCellsBlocks.HARDSTONE);

    // Decoration
    generator.registerSimpleCubeAll(MineCellsBlocks.CHAIN_PILE_BLOCK);
    generator.registerAxisRotated(MineCellsBlocks.CRATE, TexturedModel.CUBE_COLUMN);
    generator.registerParentedItemModel(
      MineCellsBlocks.BRITTLE_BARREL,
      ModelIds.getBlockModelId(MineCellsBlocks.BRITTLE_BARREL)
    );

    // Fluids
    generator.registerStateWithModelReference(MineCellsBlocks.SEWAGE, Blocks.WATER);
    generator.registerStateWithModelReference(MineCellsBlocks.ANCIENT_SEWAGE, Blocks.WATER);

    // Other
    generator.registerParentedItemModel(
      MineCellsBlocks.SPIKES,
      ModelIds.getBlockModelId(MineCellsBlocks.SPIKES)
    );
    generator.registerSimpleCubeAll(MineCellsBlocks.ELEVATOR_ASSEMBLER);
    generator.registerParentedItemModel(
      MineCellsItems.SPAWNER_RUNE,
      ModelIds.getBlockModelId(MineCellsBlocks.SPAWNER_RUNE)
    );
    generator.registerStateWithModelReference(MineCellsBlocks.CONJUNCTIVIUS_BOX, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlocks.BEAM_PLACER, Blocks.AIR);
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
    generator.register(MineCellsItems.CURSED_SWORD, Models.HANDHELD);
    generator.register(MineCellsItems.TENTACLE, Models.GENERATED);
  }

  public static void registerSpawnEggModel(ItemModelGenerator generator, SpawnEggItem item) {
    generator.register(item, new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
  }
}
