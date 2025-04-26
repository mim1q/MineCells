package com.github.mim1q.minecells.registry.featureset;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SimpleSet extends FeatureSet {
  public final Block block;
  public final StairsBlock stairs;
  public final SlabBlock slab = registerBlockWithItem(name + "_slab", new SlabBlock(defaultBlockSettings().nonOpaque()));

  private final List<ItemStack> stacks;

  public SimpleSet(Identifier identifier, String baseSuffix, Supplier<Item.Settings> defaultItemSettings, Supplier<AbstractBlock.Settings> defaultBlockSettings) {
    super(identifier, defaultItemSettings, defaultBlockSettings);
    block  = registerBlockWithItem(name + baseSuffix, new Block(defaultBlockSettings.get()));
    stairs = registerBlockWithItem(name + "_stairs", new StairsBlock(block.getDefaultState(), defaultBlockSettings.get()));
    stacks = Stream.of(block, stairs, slab).map(b -> b.asItem().getDefaultStack()).toList();
  }

  @Override
  public List<ItemStack> getStacks() {
    return stacks;
  }
}
