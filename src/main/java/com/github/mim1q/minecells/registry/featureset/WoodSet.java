package com.github.mim1q.minecells.registry.featureset;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.sign.SignTypeRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WoodSet extends FeatureSet {
  public final SignType signType = SignTypeRegistry.registerSignType(id(name));

  public final Block planks = registerBlockWithItem(name + "_planks", new Block(defaultBlockSettings()));
  public final PillarBlock log = registerBlockWithItem(name + "_log", new PillarBlock(defaultBlockSettings()));
  public final PillarBlock strippedLog = registerBlockWithItem("stripped_" + name + "_log", new PillarBlock(defaultBlockSettings()));
  public final PillarBlock wood = registerBlockWithItem(name + "_wood", new PillarBlock(defaultBlockSettings()));
  public final PillarBlock strippedWood = registerBlockWithItem("stripped_" + name + "_wood", new PillarBlock(defaultBlockSettings()));
  public final StairsBlock stairs = registerBlockWithItem(name + "_stairs", new StairsBlock(planks.getDefaultState(), defaultBlockSettings()));
  public final SlabBlock slab = registerBlockWithItem(name + "_slab", new SlabBlock(defaultBlockSettings()));
  public final DoorBlock door = registerBlockWithItem(name + "_door", new DoorBlock(defaultBlockSettings().nonOpaque()));
  public final TrapdoorBlock trapdoor = registerBlockWithItem(name + "_trapdoor", new TrapdoorBlock(defaultBlockSettings().nonOpaque()));
  public final FenceBlock fence = registerBlockWithItem(name + "_fence", new FenceBlock(defaultBlockSettings()));
  public final FenceGateBlock fenceGate = registerBlockWithItem(name + "_fence_gate", new FenceGateBlock(defaultBlockSettings()));
  public final WoodenButtonBlock button = registerBlockWithItem(name + "_button", new WoodenButtonBlock(defaultBlockSettings().noCollision()));
  public final PressurePlateBlock pressurePlate = registerBlockWithItem(name + "_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, defaultBlockSettings().noCollision()));
  public final SignBlock sign = registerBlock(name + "_sign", new SignBlock(defaultBlockSettings(), signType));
  public final WallSignBlock wallSign = registerBlock(name + "_wall_sign", new WallSignBlock(defaultBlockSettings(), signType));
  public final SignItem signItem = registerItem(name + "_sign", new SignItem(defaultItemSettings().maxCount(16), sign, wallSign));

  private final List<ItemStack> stacks = Stream.of(
    planks, log, strippedLog, wood, strippedWood, stairs, slab, door, trapdoor, fence, fenceGate, button, pressurePlate, signItem
  ).map(b -> b.asItem().getDefaultStack()).toList();

  public WoodSet(
    Identifier identifier,
    Supplier<FabricItemSettings> defaultItemSettings,
    Supplier<FabricBlockSettings> defaultBlockSettings
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
