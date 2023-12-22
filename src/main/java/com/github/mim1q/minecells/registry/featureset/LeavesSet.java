package com.github.mim1q.minecells.registry.featureset;

import com.github.mim1q.minecells.block.HangingLeavesBlock;
import com.github.mim1q.minecells.block.WallLeavesBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LeavesSet extends FeatureSet {
  public final LeavesBlock leaves = registerBlockWithItem(name + "_leaves", new LeavesBlock(defaultBlockSettings()));
  public final WallLeavesBlock wallLeaves = registerBlockWithItem(name + "_wall_leaves", new WallLeavesBlock(defaultBlockSettings().noCollision().replaceable().breakInstantly()));
  public final HangingLeavesBlock hangingLeaves = registerBlockWithItem(name + "_hanging_leaves", new HangingLeavesBlock(defaultBlockSettings().noCollision().breakInstantly()));

  private final List<ItemStack> stacks = Stream.of(leaves, wallLeaves, hangingLeaves).map(b -> b.asItem().getDefaultStack()).toList();

  public LeavesSet(Identifier identifier, Supplier<FabricItemSettings> defaultItemSettings, Supplier<FabricBlockSettings> defaultBlockSettings) {
    super(identifier, defaultItemSettings, defaultBlockSettings);
  }

  @Override
  public List<ItemStack> getStacks() {
    return stacks;
  }
}
