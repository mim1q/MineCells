package com.github.mim1q.minecells.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;
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
    var templatePool = context.getConfig().templatePool;
    var registry = context.getWorld().getRegistryManager().get(Registry.STRUCTURE_POOL_KEY);
    var key = RegistryKey.of(Registry.STRUCTURE_POOL_KEY, templatePool);
    var entry = registry.entryOf(key);
    if (entry == null) {
      return false;
    }
    var templateManager = context.getWorld().toServerWorld().getStructureTemplateManager();
    var element = entry.value().getRandomElement(context.getRandom());
    var start = element.getStart(templateManager, BlockRotation.NONE);
    var pos = context.getOrigin().add(this.getOffset(start));

    element.generate(
      templateManager,
      context.getWorld(),
      context.getWorld().toServerWorld().getStructureAccessor(),
      context.getGenerator(),
      pos,
      context.getOrigin(),
      BlockRotation.random(context.getRandom()),
      BlockBox.create(context.getOrigin().add(-5, -5, -5), context.getOrigin().add(5, 5, 5)),
      context.getRandom(),
      false
    );

    return true;
  }

  public Vec3i getOffset(Vec3i start) {
    return Vec3i.ZERO;
  }

  public record JigsawFeatureConfig(
    Identifier templatePool,
    Identifier start
  ) implements FeatureConfig {
      public static final Codec<JigsawFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        Identifier.CODEC.fieldOf("template_pool").forGetter((config) -> config.templatePool),
        Identifier.CODEC.fieldOf("start").forGetter((config) -> config.start)
      ).apply(instance, JigsawFeatureConfig::new));
  }
}
