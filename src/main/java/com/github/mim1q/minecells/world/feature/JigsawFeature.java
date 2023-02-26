package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.structure.grid.MineCellsStructurePoolBasedGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
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
    var world = context.getWorld();
    var registryManager = world.getRegistryManager();
    var noiseConfig = world.getChunkManager() instanceof ServerChunkManager ? ((ServerChunkManager) world.getChunkManager()).getNoiseConfig() : null;
    var poolRegistry = registryManager.get(Registry.STRUCTURE_POOL_KEY);
    var optPoolEntry = poolRegistry.getEntry(RegistryKey.of(Registry.STRUCTURE_POOL_KEY, context.getConfig().templatePool()));
    if (optPoolEntry.isEmpty()) {
      return false;
    }
    var pos = context.getOrigin().add(this.getOffset(context.getOrigin()));
    var rotation = BlockRotation.random(context.getRandom());
    MineCellsStructurePoolBasedGenerator.generate(
      world,
      context.getGenerator(),
      registryManager,
      world.toServerWorld().getStructureTemplateManager(),
      world.toServerWorld().getStructureAccessor(),
      noiseConfig,
      context.getRandom(),
      (int) world.getSeed(),
      optPoolEntry.get(),
      8,
      pos,
      rotation
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