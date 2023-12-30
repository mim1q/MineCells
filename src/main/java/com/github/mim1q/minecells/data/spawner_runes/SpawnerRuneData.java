package com.github.mim1q.minecells.data.spawner_runes;

import com.github.mim1q.minecells.MineCells;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

  public List<EntitySpawnData> getSelectedEntities(Random random) {
    var entities = new ArrayList<EntitySpawnData>();
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

    private List<EntitySpawnData> getSelectedEntities(Random random) {
      int weightSum = entries.stream().map(e -> e.weight).reduce(Integer::sum).orElse(1);
      var count = rolls.get(random);
      var entities = new ArrayList<EntitySpawnData>();
      for (int i = 0; i < count; i++) {
        entities.add(chooseEntity(weightSum, random));
      }
      return entities;
    }

    private EntitySpawnData chooseEntity(int weightSum, Random random) {
      var randomWeight = random.nextInt(weightSum);

      for (var entry : entries) {
        randomWeight -= entry.weight;
        if (randomWeight < 0) {
          return new EntitySpawnData(entry.entityType, entry.attributeOverrides);
        }
      }
      return null;
    }
  }

  private static class Entry {
    private final int weight;
    private final EntityType<?> entityType;
    private final Map<String, Double> attributeMap;
    public final Map<EntityAttribute, Double> attributeOverrides;

    private Entry(int weight, String entityType, Map<String, Double> attributeOverrides) {
      this.weight = weight;
      this.entityType = entityType == null ? null : Registries.ENTITY_TYPE.get(Identifier.tryParse(entityType));
      this.attributeMap = attributeOverrides;
      this.attributeOverrides = attributeOverrides
        .entrySet()
        .stream()
        .map(entry -> {
            var attribute = Registries.ATTRIBUTE.get(Identifier.tryParse(entry.getKey()));
            if (attribute == null) {
              MineCells.LOGGER.warn("Unknown attribute in Spawner Rune data: " + entry.getKey());
              return null;
            }
            return Map.entry(
              attribute,
              entry.getValue()
            );
          }
        )
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
        Codec.INT.optionalFieldOf("weight", 1).forGetter(e -> e.weight),
        Codec.STRING.optionalFieldOf("entity", null).forGetter(e -> Registries.ENTITY_TYPE.getId(e.entityType).toString()),
        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).optionalFieldOf("attributes", Map.of()).forGetter(e -> e.attributeMap)
      ).apply(instance, Entry::new)
    );
  }

  public record EntitySpawnData(
    EntityType<?> entityType,
    Map<EntityAttribute, Double> attributeOverrides
  ) {
  }
}
