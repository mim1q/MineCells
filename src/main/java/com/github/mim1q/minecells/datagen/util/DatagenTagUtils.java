package com.github.mim1q.minecells.datagen.util;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public interface DatagenTagUtils extends DatagenUtils {
  default void addBlockTag(TagKey<Block> tag, Consumer<FabricTagProvider<Block>.FabricTagBuilder> consumer) {
    getInitializers().tags().block(tag, consumer);
  }

  default void addBlockTag(TagKey<Block> tag, List<Block> blocks) {
    getInitializers().tags().block(tag, t -> t.add(blocks.toArray(Block[]::new)));
  }

  default void addBlockTag(TagKey<Block> tag, Block... blocks) {
    getInitializers().tags().block(tag, t -> t.add(blocks));
  }

  default void addItemTag(TagKey<Item> tag, Consumer<FabricTagProvider<Item>.FabricTagBuilder> consumer) {
    getInitializers().tags().item(tag, consumer);
  }

  default void addItemTag(TagKey<Item> tag, ItemConvertible... items) {
    getInitializers().tags().item(tag, t -> t.add(Arrays.stream(items).map(ItemConvertible::asItem).toArray(Item[]::new)));
  }

  default void addItemTag(TagKey<Item> tag, List<ItemStack> stacks) {
    getInitializers().tags().item(tag, t -> t.add(stacks.stream().map(ItemStack::getItem).toArray(Item[]::new)));
  }

  default void addBlockEntityTag(TagKey<BlockEntityType<?>> tag, Consumer<FabricTagProvider<BlockEntityType<?>>.FabricTagBuilder> consumer) {
    getInitializers().tags().blockEntity(tag, consumer);
  }

  default void addBlockEntityTag(TagKey<BlockEntityType<?>> tag, BlockEntityType<?>... blockEntities) {
    getInitializers().tags().blockEntity(tag, t -> t.add(blockEntities));
  }

  default void addFluidTag(TagKey<Fluid> tag, Consumer<FabricTagProvider<Fluid>.FabricTagBuilder> consumer) {
    getInitializers().tags().fluid(tag, consumer);
  }

  default void addFluidTag(TagKey<Fluid> tag, Fluid... fluids) {
    getInitializers().tags().fluid(tag, t -> t.add(fluids));
  }

  default void addEnchantmentTag(TagKey<Enchantment> tag, Consumer<FabricTagProvider<Enchantment>.FabricTagBuilder> consumer) {
    getInitializers().tags().enchantment(tag, consumer);
  }

  default void addEnchantmentTag(TagKey<Enchantment> tag, Enchantment... enchantments) {
    getInitializers().tags().enchantment(tag, t -> t.add(enchantments));
  }

  default void addEntityTag(TagKey<EntityType<?>> tag, Consumer<FabricTagProvider<EntityType<?>>.FabricTagBuilder> consumer) {
    getInitializers().tags().entity(tag, consumer);
  }

  default void addEntityTag(TagKey<EntityType<?>> tag, EntityType<?>... entities) {
    getInitializers().tags().entity(tag, t -> t.add(entities));
  }
}
