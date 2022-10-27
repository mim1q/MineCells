package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class MineCellsEntityLootTableProvider extends MineCellsLootTableHelper {
  public MineCellsEntityLootTableProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, LootContextTypes.ENTITY);
  }

  @Override
  public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
    biConsumer.accept(
      MineCellsEntities.LEAPING_ZOMBIE.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(ItemEntry.builder(Items.ROTTEN_FLESH), 0, 1))
        .pool(simplePool(ItemEntry.builder(Items.GOLD_NUGGET), 0, 2))
        .pool(simplePool(ItemEntry.builder(Items.GLOWSTONE_DUST), 1, 2))
    );

    biConsumer.accept(
      MineCellsEntities.GRENADIER.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(ItemEntry.builder(Items.TNT), 1).conditionally(RandomChanceLootCondition.builder(0.2f)))
        .pool(simplePool(ItemEntry.builder(Items.GUNPOWDER), 1, 3))
    );

    biConsumer.accept(
      MineCellsEntities.SHIELDBEARER.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(ItemEntry.builder(Items.IRON_INGOT), 1, 2))
    );

    biConsumer.accept(
      MineCellsEntities.CONJUNCTIVIUS.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(ItemEntry.builder(Items.DIAMOND), 8, 16))
        .pool(simplePool(ItemEntry.builder(Items.EMERALD), 8, 16))
        .pool(simplePool(ItemEntry.builder(Items.GOLD_INGOT), 8, 16))
    );
  }
}
