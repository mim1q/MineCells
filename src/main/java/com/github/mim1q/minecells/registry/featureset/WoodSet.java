package com.github.mim1q.minecells.registry.featureset;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WoodSet extends FeatureSet {
  public final BlockSetType blockSetType = new BlockSetType(name);
  public final WoodType woodType = new WoodTypeBuilder().register(MineCells.createId(name), blockSetType);

  public final Block planks = registerBlockWithItem(name + "_planks", new Block(defaultBlockSettings()));
  public final PillarBlock log = registerBlockWithItem(name + "_log", new PillarBlock(defaultBlockSettings()));
  public final PillarBlock strippedLog = registerBlockWithItem("stripped_" + name + "_log", new PillarBlock(defaultBlockSettings()));
  public final PillarBlock wood = registerBlockWithItem(name + "_wood", new PillarBlock(defaultBlockSettings()));
  public final PillarBlock strippedWood = registerBlockWithItem("stripped_" + name + "_wood", new PillarBlock(defaultBlockSettings()));
  public final StairsBlock stairs = registerBlockWithItem(name + "_stairs", new StairsBlock(planks.getDefaultState(), defaultBlockSettings()));
  public final SlabBlock slab = registerBlockWithItem(name + "_slab", new SlabBlock(defaultBlockSettings()));
  public final DoorBlock door = registerBlockWithItem(name + "_door", new DoorBlock(blockSetType, defaultBlockSettings().nonOpaque()));
  public final TrapdoorBlock trapdoor = registerBlockWithItem(name + "_trapdoor", new TrapdoorBlock(blockSetType, defaultBlockSettings().nonOpaque()));
  public final FenceBlock fence = registerBlockWithItem(name + "_fence", new FenceBlock(defaultBlockSettings()));
  public final FenceGateBlock fenceGate = registerBlockWithItem(name + "_fence_gate", new FenceGateBlock(woodType, defaultBlockSettings()));
  public final ButtonBlock button = registerBlockWithItem(name + "_button", new ButtonBlock(blockSetType, 20, defaultBlockSettings().noCollision()));
  public final PressurePlateBlock pressurePlate = registerBlockWithItem(name + "_pressure_plate", new PressurePlateBlock(blockSetType, defaultBlockSettings().noCollision()));
  public final SignBlock sign = registerBlock(name + "_sign", new SignBlock(woodType, defaultBlockSettings().nonOpaque().noCollision()));
  public final WallSignBlock wallSign = registerBlock(name + "_wall_sign", new WallSignBlock(woodType, defaultBlockSettings().nonOpaque().noCollision()));
  public final SignItem signItem = registerItem(name + "_sign", new SignItem(defaultItemSettings().maxCount(16), sign, wallSign));

  private final List<ItemStack> stacks = Stream.of(
    planks, log, strippedLog, wood, strippedWood, stairs, slab, door, trapdoor, fence, fenceGate, button, pressurePlate, signItem
  ).map(b -> b.asItem().getDefaultStack()).toList();

  public WoodSet(
    Identifier identifier,
    Supplier<Item.Settings> defaultItemSettings,
    Supplier<AbstractBlock.Settings> defaultBlockSettings
  ) {
    super(identifier, defaultItemSettings, defaultBlockSettings);
    StrippableBlockRegistry.register(log, strippedLog);
    StrippableBlockRegistry.register(wood, strippedWood);
  }

  @Override
  public List<ItemStack> getStacks() {
    return stacks;
  }
}
