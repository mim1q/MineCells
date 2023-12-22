package com.github.mim1q.minecells.world.processor;

import com.github.mim1q.minecells.registry.MineCellsStructureProcessorTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BetterRuleStructureProcessor extends RuleStructureProcessor {
  public static final Codec<BetterRuleStructureProcessor> CODEC = StructureProcessorRule.CODEC
    .listOf()
    .fieldOf("rules")
    .xmap(BetterRuleStructureProcessor::new, processor -> processor.overrideRules)
    .codec();

  private final ImmutableList<StructureProcessorRule> overrideRules;

  public BetterRuleStructureProcessor(List<? extends StructureProcessorRule> rules) {
    super(rules);
    this.overrideRules = ImmutableList.copyOf(rules);
  }

  @Nullable
  @Override
  public StructureBlockInfo process(
    WorldView world,
    BlockPos pos,
    BlockPos pivot,
    StructureBlockInfo originalBlockInfo,
    StructureBlockInfo currentBlockInfo,
    StructurePlacementData data
  ) {
    var newData = super.process(world, pos, pivot, originalBlockInfo, currentBlockInfo, data);
    if (newData == null) return null;
    var oldState = originalBlockInfo.state();
    var newState = newData.state();
    for (var property : newState.getProperties()) {
      newState = copy(oldState, newState, property);
    }
    return new StructureBlockInfo(newData.pos(), newState, newData.nbt());
  }

  private static <T extends Comparable<T>> BlockState copy(BlockState copyFrom, BlockState copyTo, Property<T> property) {
    if (copyFrom.contains(property)) {
      return copyTo.with(property, copyFrom.get(property));
    }
    return copyTo;
  }

  @Override
  protected StructureProcessorType<?> getType() {
    return MineCellsStructureProcessorTypes.BETTER_RULE;
  }
}
