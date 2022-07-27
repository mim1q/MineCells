package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.BlockRegistry;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.ItemRegistry;
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
    generator.registerSimpleCubeAll(BlockRegistry.HARDSTONE);
    generator.registerSimpleCubeAll(BlockRegistry.ELEVATOR_ASSEMBLER);
    generator.registerStateWithModelReference(BlockRegistry.SEWAGE, Blocks.WATER);
    generator.registerStateWithModelReference(BlockRegistry.ANCIENT_SEWAGE, Blocks.WATER);
  }

  @Override
  public void generateItemModels(ItemModelGenerator generator) {
    for (SpawnEggItem spawnEggItem : EntityRegistry.getSpawnEggs()) {
      registerSpawnEggModel(generator, spawnEggItem);
    }
    for (Item item : ItemRegistry.getSimpleItems()) {
      generator.register(item, Models.GENERATED);
    }
    generator.register(ItemRegistry.ASSASSINS_DAGGER, Models.HANDHELD);
  }

  public static void registerSpawnEggModel(ItemModelGenerator generator, SpawnEggItem item) {
    generator.register(item, new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
  }
}
