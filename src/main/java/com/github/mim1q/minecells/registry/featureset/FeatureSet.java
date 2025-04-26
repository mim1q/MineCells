package com.github.mim1q.minecells.registry.featureset;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

abstract class FeatureSet {
  protected final Supplier<Item.Settings> defaultItemSettings;
  protected final Supplier<AbstractBlock.Settings> defaultBlockSettings;
  protected final String name;
  protected final String namespace;

  public FeatureSet(
    Identifier identifier,
    Supplier<Item.Settings> defaultItemSettings,
    Supplier<AbstractBlock.Settings> defaultBlockSettings
  ) {
    this.defaultItemSettings = defaultItemSettings;
    this.defaultBlockSettings = defaultBlockSettings;
    this.name = identifier.getPath();
    this.namespace = identifier.getNamespace();
  }

  protected Identifier id(String name) { return Identifier.of(namespace, name); }

  protected <I extends Item> I registerItem(String name, I item) {
    return Registry.register(Registries.ITEM, id(name), item);
  }

  protected <B extends Block> B registerBlock(String name, B block) {
    return Registry.register(Registries.BLOCK, id(name), block);
  }

  protected <B extends Block> B registerBlockWithItem(String name, B block) {
    registerItem(name, new BlockItem(block, defaultItemSettings.get()));
    return registerBlock(name, block);
  }

  protected Item.Settings defaultItemSettings() {
    return defaultItemSettings.get();
  }

  protected AbstractBlock.Settings defaultBlockSettings() {
    return defaultBlockSettings.get();
  }

  public abstract List<ItemStack> getStacks();
}