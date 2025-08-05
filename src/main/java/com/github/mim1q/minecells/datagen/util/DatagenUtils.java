package com.github.mim1q.minecells.datagen.util;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DatagenUtils {
  InitializerHolder getInitializers();

  record InitializerHolder(
    List<Consumer<BlockStateModelGenerator>> blockState,
    List<Consumer<ItemModelGenerator>> itemModel,
    List<Consumer<RecipeExporter>> recipe,
    List<Consumer<FabricBlockLootTableProvider>> blockLootTable,
    Map<LootContextType, List<Consumer<BiConsumer<RegistryKey<LootTable>, LootTable.Builder>>>> otherLootTable,
    List<AdvancementEntry> advancements,
    TagInitializerHolder tags
  ) {
    public static InitializerHolder createEmpty() {
      return new InitializerHolder(
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new HashMap<>(),
        new ArrayList<>(),
        new TagInitializerHolder()
      );
    }
  }

  class TagInitializerHolder {
    final Map<TagKey<Block>, List<Consumer<FabricTagProvider<Block>.FabricTagBuilder>>> blockTags = new HashMap<>();
    final Map<TagKey<Item>, List<Consumer<FabricTagProvider<Item>.FabricTagBuilder>>> itemTags = new HashMap<>();
    final Map<TagKey<BlockEntityType<?>>, List<Consumer<FabricTagProvider<BlockEntityType<?>>.FabricTagBuilder>>> blockEntityTags = new HashMap<>();
    final Map<TagKey<Fluid>, List<Consumer<FabricTagProvider<Fluid>.FabricTagBuilder>>> fluidTags = new HashMap<>();
    final Map<TagKey<Enchantment>, List<Consumer<FabricTagProvider<Enchantment>.FabricTagBuilder>>> enchantmentTags = new HashMap<>();
    final Map<TagKey<EntityType<?>>, List<Consumer<FabricTagProvider<EntityType<?>>.FabricTagBuilder>>> entityTags = new HashMap<>();

    public void block(TagKey<Block> tag, Consumer<FabricTagProvider<Block>.FabricTagBuilder> consumer) {
      blockTags.computeIfAbsent(tag, k -> new ArrayList<>()).add(consumer);
    }

    public void item(TagKey<Item> tag, Consumer<FabricTagProvider<Item>.FabricTagBuilder> consumer) {
      itemTags.computeIfAbsent(tag, k -> new ArrayList<>()).add(consumer);
    }

    public void blockEntity(TagKey<BlockEntityType<?>> tag, Consumer<FabricTagProvider<BlockEntityType<?>>.FabricTagBuilder> consumer) {
      blockEntityTags.computeIfAbsent(tag, k -> new ArrayList<>()).add(consumer);
    }

    public void fluid(TagKey<Fluid> tag, Consumer<FabricTagProvider<Fluid>.FabricTagBuilder> consumer) {
      fluidTags.computeIfAbsent(tag, k -> new ArrayList<>()).add(consumer);
    }

    public void enchantment(TagKey<Enchantment> tag, Consumer<FabricTagProvider<Enchantment>.FabricTagBuilder> consumer) {
      enchantmentTags.computeIfAbsent(tag, k -> new ArrayList<>()).add(consumer);
    }

    public void entity(TagKey<EntityType<?>> tag, Consumer<FabricTagProvider<EntityType<?>>.FabricTagBuilder> consumer) {
      entityTags.computeIfAbsent(tag, k -> new ArrayList<>()).add(consumer);
    }
  }
}