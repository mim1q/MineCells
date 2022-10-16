package com.github.mim1q.minecells.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class JigsawFeature extends Feature<JigsawFeature.JigsawFeatureConfig> {
  public JigsawFeature(Codec<JigsawFeatureConfig> codec) {
    super(codec);
  }

  public boolean generate(FeatureContext<JigsawFeatureConfig> context) {
    context.getWorld().setBlockState(context.getOrigin().withY(20), Blocks.AIR.getDefaultState(), 2);
    var templatePool = context.getConfig().templatePool;
    var registry = context.getWorld().getRegistryManager().get(Registry.STRUCTURE_POOL_KEY);
    var key = RegistryKey.of(Registry.STRUCTURE_POOL_KEY, templatePool);
    var entry = registry.entryOf(key);
    if (entry == null) {
      return false;
    }
    try {
      entry.value().getRandomElement(context.getRandom()).generate(
        context.getWorld().toServerWorld().getStructureTemplateManager(),
        context.getWorld(),
        context.getWorld().toServerWorld().getStructureAccessor(),
        context.getGenerator(),
        context.getOrigin(),
        context.getOrigin(),
        BlockRotation.random(context.getRandom()),
        BlockBox.create(context.getOrigin().add(-5, -5, -5), context.getOrigin().add(5, 5, 5)),
        context.getRandom(),
        false
      );
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public record JigsawFeatureConfig(Identifier templatePool, Identifier start) implements FeatureConfig {
      public static final Codec<JigsawFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        Identifier.CODEC.fieldOf("template_pool").forGetter((config) -> config.templatePool),
        Identifier.CODEC.fieldOf("start").forGetter((config) -> config.start)
      ).apply(instance, JigsawFeatureConfig::new));
  }
}
