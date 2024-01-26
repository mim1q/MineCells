package com.github.mim1q.minecells.world.processor;

import com.github.mim1q.minecells.registry.MineCellsStructureProcessorTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SwitchBlockStructureProcessor extends StructureProcessor {
  public static final Codec<SwitchBlockStructureProcessor> CODEC = SwitchBlockRule.CODEC
    .listOf()
    .fieldOf("rules")
    .xmap(SwitchBlockStructureProcessor::new, it -> it.rules)
    .codec();

  private final List<SwitchBlockRule> rules;
  private final Map<Block, SwitchBlockRule> rulesByFromBlock;

  public SwitchBlockStructureProcessor(List<SwitchBlockRule> rules) {
    this.rules = rules;
    this.rulesByFromBlock = rules.stream().collect(
      java.util.stream.Collectors.toMap(
        rule -> rule.fromBlock,
        rule -> rule
      )
    );
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
    var oldState = currentBlockInfo.state();
    var rule = rulesByFromBlock.get(oldState.getBlock());
    if (rule == null) {
      return currentBlockInfo;
    }
    var newState = rule.selectRandomBlock(Random.create(currentBlockInfo.pos().hashCode())).getDefaultState();
    for (var property : newState.getProperties()) {
      newState = copyProperty(oldState, newState, property);
    }
    return new StructureBlockInfo(currentBlockInfo.pos(), newState, currentBlockInfo.nbt());
  }

  private static <T extends Comparable<T>> BlockState copyProperty(BlockState copyFrom, BlockState copyTo, Property<T> property) {
    if (copyFrom.contains(property)) {
      return copyTo.with(property, copyFrom.get(property));
    }
    return copyTo;
  }

  @Override
  protected StructureProcessorType<?> getType() {
    return MineCellsStructureProcessorTypes.SWITCH_BLOCK;
  }

  public static final class SwitchBlockRule {
    public static final Codec<SwitchBlockRule> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
        Registries.BLOCK.getCodec().fieldOf("from").forGetter(rule -> rule.fromBlock),
        Codec.unboundedMap(Registries.BLOCK.getCodec(), Codec.INT).fieldOf("to").forGetter(rule -> rule.toBlocksWithWeights)
      ).apply(instance, SwitchBlockRule::new)
    );

    private final Block fromBlock;
    private final Map<Block, Integer> toBlocksWithWeights;
    private final int totalWeight;

    private SwitchBlockRule(
      Block from,
      Map<Block, Integer> to
    ) {
      this.fromBlock = from;
      this.toBlocksWithWeights = to;
      this.totalWeight = toBlocksWithWeights.values().stream().reduce(0, Integer::sum);
    }

    private Block selectRandomBlock(Random random) {
      var selectedWeight = random.nextInt(totalWeight) + 1;
      var currentWeight = 0;
      for (var entry : toBlocksWithWeights.entrySet()) {
        currentWeight += entry.getValue();
        if (currentWeight >= selectedWeight) {
          return entry.getKey();
        }
      }
      return fromBlock;
    }
  }
}
