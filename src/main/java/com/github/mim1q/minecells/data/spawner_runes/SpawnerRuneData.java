package com.github.mim1q.minecells.data.spawner_runes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public record SpawnerRuneData(
  float cooldown,
  float spawnDistance,
  float playerDistance,
  List<Pool> pools
) {
  public static final Codec<SpawnerRuneData> CODEC = RecordCodecBuilder.create(instance ->
    instance.group(
      Codec.FLOAT.optionalFieldOf("cooldown", 60F).forGetter(SpawnerRuneData::cooldown),
      Codec.FLOAT.optionalFieldOf("spawnDistance", 0F).forGetter(SpawnerRuneData::spawnDistance),
      Codec.FLOAT.fieldOf("playerDistance").forGetter(SpawnerRuneData::playerDistance),
      Pool.CODEC.listOf().fieldOf("pools").forGetter(SpawnerRuneData::pools)
    ).apply(instance, SpawnerRuneData::new)
  );

  public List<EntityType<?>> getSelectedEntities(Random random) {
    var entities = new ArrayList<EntityType<?>>();
    for (var pool : pools) {
      entities.addAll(pool.getSelectedEntities(random));
    }
    return entities;
  }

  private record Pool(
    IntProvider rolls,
    List<Entry> entries
  ) {
    private static final Codec<Pool> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
        IntProvider.POSITIVE_CODEC.fieldOf("rolls").forGetter(Pool::rolls),
        Entry.CODEC.listOf().fieldOf("entries").forGetter(Pool::entries)
      ).apply(instance, Pool::new)
    );

    private List<EntityType<?>> getSelectedEntities(Random random) {
      int weightSum = entries.stream().map(e -> e.weight).reduce(Integer::sum).orElse(1);
      var count = rolls.get(random);
      var entities = new ArrayList<EntityType<?>>();
      for (int i = 0; i < count; i++) {
        entities.add(chooseEntity(weightSum, random));
      }
      return entities;
    }

    private EntityType<?> chooseEntity(int weightSum, Random random) {
      var randomWeight = random.nextInt(weightSum);

      for (var entry : entries) {
        randomWeight -= entry.weight;
        if (randomWeight < 0) {
          return entry.entityType;
        }
      }
      return null;
    }
  }

  private static class Entry {
    private final int weight;
    private final EntityType<?> entityType;

    private Entry(int weight, String entityType) {
      this.weight = weight;
      this.entityType = entityType == null ? null : Registry.ENTITY_TYPE.get(Identifier.tryParse(entityType));
    }

    private static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
        Codec.INT.optionalFieldOf("weight", 1).forGetter(e -> e.weight),
        Codec.STRING.optionalFieldOf("entity", null).forGetter(e -> Registry.ENTITY_TYPE.getId(e.entityType).toString())
      ).apply(instance, Entry::new)
    );
  }
}
