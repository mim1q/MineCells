package com.github.mim1q.minecells.registry.featureset;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FullStoneSet extends StoneSet {
  public final ButtonBlock button = registerBlockWithItem(name + "_button", new ButtonBlock(blockSetType, 10, defaultBlockSettings().noCollision()));
  public final PressurePlateBlock pressurePlate = registerBlockWithItem(name + "_pressure_plate", new PressurePlateBlock(blockSetType, defaultBlockSettings().noCollision()));

  private final List<ItemStack> stacks = Stream.of(
    block, stairs, slab, wall, pressurePlate, button
  ).map(b -> b.asItem().getDefaultStack()).toList();

  public FullStoneSet(
    Identifier identifier,
    String baseSuffix,
    Supplier<Item.Settings> defaultItemSettings,
    Supplier<AbstractBlock.Settings> defaultBlockSettings
  ) {
    super(identifier, baseSuffix, defaultItemSettings, defaultBlockSettings);
  }

  @Override
  public List<ItemStack> getStacks() {
    return stacks;
  }
}
