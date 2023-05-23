package com.github.mim1q.minecells.registry.featureset;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.StoneButtonBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FullStoneSet extends StoneSet {
  public final StoneButtonBlock button = registerBlockWithItem(name + "_button", new StoneButtonBlock(defaultBlockSettings().noCollision()));
  public final PressurePlateBlock pressurePlate = registerBlockWithItem(name + "_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, defaultBlockSettings().noCollision()));

  private final List<ItemStack> stacks = Stream.of(
    block, stairs, slab, wall, pressurePlate, button
  ).map(b -> b.asItem().getDefaultStack()).toList();

  public FullStoneSet(
    Identifier identifier,
    String baseSuffix,
    Supplier<FabricItemSettings> defaultItemSettings,
    Supplier<FabricBlockSettings> defaultBlockSettings
  ) {
    super(identifier, baseSuffix, defaultItemSettings, defaultBlockSettings);
  }

  @Override
  public List<ItemStack> getStacks() {
    return stacks;
  }
}
