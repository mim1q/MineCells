package com.github.mim1q.minecells.world.processor;

import com.github.mim1q.minecells.registry.MineCellsStructureProcessorTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RandomizePropertyStructureProcessor extends StructureProcessor {
  private final List<Block> blocks;
  private final String propertyName;

  public static final Codec<RandomizePropertyStructureProcessor> CODEC = RecordCodecBuilder.create(instance ->
    instance.group(
      Registries.BLOCK.getCodec().listOf().fieldOf("blocks").forGetter(it -> it.blocks),
      Codec.STRING.fieldOf("property_name").forGetter(it -> it.propertyName)
    ).apply(instance, RandomizePropertyStructureProcessor::new)
  );

  public RandomizePropertyStructureProcessor(List<Block> blocks, String propertyName) {
    this.blocks = blocks;
    this.propertyName = propertyName;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Nullable
  @Override
  public StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureBlockInfo originalBlockInfo, StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
    if (!blocks.contains(currentBlockInfo.state().getBlock())) {
      return currentBlockInfo;
    }

    var property = (Property) currentBlockInfo.state().getProperties().stream()
      .filter(it -> it.getName().equals(propertyName))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Property " + propertyName + " not found in block: " + currentBlockInfo.state().getBlock()));

    var random = data.getRandom(currentBlockInfo.pos());
    var propertyValue = (Comparable) property.getValues().stream()
      .skip(random.nextInt(property.getValues().size()))
      .findFirst()
      .orElseThrow();

    var newState = currentBlockInfo.state().with(property, propertyValue);
    return new StructureBlockInfo(currentBlockInfo.pos(), newState, currentBlockInfo.nbt());
  }

  @Override protected StructureProcessorType<?> getType() {
    return MineCellsStructureProcessorTypes.RANDOMIZE_PROPERTY;
  }
}
