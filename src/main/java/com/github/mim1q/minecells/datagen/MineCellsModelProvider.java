package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
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
    generator.registerSimpleCubeAll(MineCellsBlocks.ELEVATOR_ASSEMBLER);
    generator.registerStateWithModelReference(MineCellsBlockEntities.PRISON_BOX, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlockEntities.CONJUNCTIVIUS_BOX, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlockEntities.SHOCKER_BOX, Blocks.AIR);
    generator.registerStateWithModelReference(MineCellsBlockEntities.KINGDOM_PORTAL_CORE, Blocks.AIR);
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
    generator.register(MineCellsItems.ASSASSINS_DAGGER, Models.HANDHELD);
  }

  public static void registerSpawnEggModel(ItemModelGenerator generator, SpawnEggItem item) {
    generator.register(item, new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
  }
}
