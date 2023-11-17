package com.github.mim1q.minecells.registry.featureset;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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
  protected final Supplier<FabricItemSettings> defaultItemSettings;
  protected final Supplier<FabricBlockSettings> defaultBlockSettings;
  protected final String name;
  protected final String namespace;

  public FeatureSet(
    Identifier identifier,
    Supplier<FabricItemSettings> defaultItemSettings,
    Supplier<FabricBlockSettings> defaultBlockSettings
  ) {
    this.defaultItemSettings = defaultItemSettings;
    this.defaultBlockSettings = defaultBlockSettings;
    this.name = identifier.getPath();
    this.namespace = identifier.getNamespace();
  }

  protected Identifier id(String name) { return new Identifier(namespace, name); }

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

  protected FabricItemSettings defaultItemSettings() {
    return defaultItemSettings.get();
  }

  protected FabricBlockSettings defaultBlockSettings() {
    return defaultBlockSettings.get();
  }

  public abstract List<ItemStack> getStacks();
}