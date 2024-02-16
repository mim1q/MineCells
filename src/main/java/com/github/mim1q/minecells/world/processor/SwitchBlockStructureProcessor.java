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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwitchBlockStructureProcessor extends StructureProcessor {
  public static final Codec<SwitchBlockStructureProcessor> CODEC =
    RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.optionalFieldOf("default_namespace", "minecraft").forGetter(it -> it.defaultNamespace),
      SwitchBlockRule.CODEC.listOf().fieldOf("rules").forGetter(it -> it.rules)
    ).apply(instance, SwitchBlockStructureProcessor::new));

  private final List<SwitchBlockRule> rules;
  private final String defaultNamespace;
  private final Map<Block, SwitchBlockRule> rulesByFromBlock;

  public SwitchBlockStructureProcessor(String defaultNamespace, List<SwitchBlockRule> rules) {
    this.rules = rules;
    this.rules.forEach(rule -> rule.setup(defaultNamespace));
    this.defaultNamespace = defaultNamespace == null ? "minecraft" : defaultNamespace;
    this.rulesByFromBlock = rules.stream().collect(
      Collectors.toMap(
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
    var rng = Random.create(currentBlockInfo.pos().hashCode());
    rng.nextInt();
    var newState = rule.selectRandomBlock(rng).getDefaultState();
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
        Codec.STRING.fieldOf("from").forGetter(rule -> rule.fromBlockId),
        Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("to").forGetter(rule -> rule.toBlocksIdsWithWeights)
      ).apply(instance, SwitchBlockRule::new)
    );

    private final String fromBlockId;
    private final Map<String, Integer> toBlocksIdsWithWeights;

    private Block fromBlock;
    private Map<Block, Integer> toBlocksWithWeights;

    private final int totalWeight;

    private SwitchBlockRule(
      String from,
      Map<String, Integer> to
    ) {
      this.fromBlockId = from;
      this.toBlocksIdsWithWeights = to;
      this.totalWeight = toBlocksIdsWithWeights.values().stream().reduce(0, Integer::sum);
    }

    private Block selectRandomBlock(Random random) {
      var selected = random.nextInt(totalWeight) + 1;

      var current = 0;
      for (var entry : toBlocksWithWeights.entrySet()) {
        current += entry.getValue();
        if (current >= selected) {
          return entry.getKey();
        }
      }

      return fromBlock;
    }

    private void setup(String defaultNamespace) {
      var fromBlock = Registries.BLOCK.get(getBlockId(fromBlockId, defaultNamespace));
      var toBlocksWithWeights = toBlocksIdsWithWeights.entrySet().stream().collect(
        Collectors.toMap(
          entry -> Registries.BLOCK.get(getBlockId(entry.getKey(), defaultNamespace)),
          Map.Entry::getValue
        )
      );
      this.fromBlock = fromBlock;
      this.toBlocksWithWeights = toBlocksWithWeights;
    }

    private static Identifier getBlockId(String id, String defaultNamespace) {
      return new Identifier(id.contains(":") ? id : defaultNamespace + ":" + id);
    }
  }
}
