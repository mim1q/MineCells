package com.github.mim1q.minecells.block.blockentity.spawnerrune;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntryList {
  public static final EntryList PRISON = new EntryList()
    .addEntry(MineCellsEntities.LEAPING_ZOMBIE, 6)
    .addEntry(MineCellsEntities.SHIELDBEARER, 3)
    .addEntry(MineCellsEntities.UNDEAD_ARCHER, 3)
    .addEntry(MineCellsEntities.GRENADIER, 2);

  public static final EntryList PROMENADE_OF_THE_CONDEMNED = new EntryList()
    .addEntry(MineCellsEntities.LEAPING_ZOMBIE, 4)
    .addEntry(MineCellsEntities.GRENADIER, 3)
    .addEntry(MineCellsEntities.RUNNER, 1)
    .addEntry(MineCellsEntities.MUTATED_BAT, 3);

  public static final EntryList PROTECTOR = new EntryList()
    .addEntry(MineCellsEntities.PROTECTOR, 1);

  public static final EntryList SHOCKER = new EntryList()
    .addEntry(MineCellsEntities.SHOCKER, 1);

  public List<Entry> entries;

  public EntryList(List<Entry> entries) {
    this.entries = entries;
  }

  public EntryList() {
    this(new ArrayList<>());
  }

  public EntryList addEntry(EntityType<?> entityType, int weight) {
    entries.add(new Entry(entityType, weight));
    return this;
  }

  public NbtList toNbt() {
    NbtList list = new NbtList();
    for (Entry entry : entries) {
      list.add(entry.toNbt());
    }
    return list;
  }

  public static EntryList fromNbt(NbtList nbt) {
    List<Entry> entries = new ArrayList<>();
    for (int i = 0; i < nbt.size(); i++) {
      entries.add(Entry.fromNbt(nbt.getCompound(i)));
    }
    return new EntryList(entries);
  }

  public List<EntityType<?>> selectEntityTypes(int count, Random random) {
    List<EntityType<?>> entityTypes = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      entityTypes.add(selectEntityType(random));
    }
    return entityTypes;
  }

  private EntityType<?> selectEntityType(Random random) {
    int totalWeight = 0;
    for (Entry entry : entries) {
      totalWeight += entry.weight;
    }
    int randomWeight = random.nextInt(totalWeight);
    for (Entry entry : entries) {
      randomWeight -= entry.weight;
      if (randomWeight < 0) {
        return entry.entityType;
      }
    }
    return null;
  }

  public static class Entry {
    public final EntityType<?> entityType;
    public final int weight;

    public Entry(String entityId, int weight) {
      this.entityType = EntityType.get(entityId).orElseThrow();
      this.weight = weight;
    }

    public Entry(EntityType<?> entityType, int weight) {
      this.entityType = entityType;
      this.weight = weight;
    }

    public NbtCompound toNbt() {
      NbtCompound tag = new NbtCompound();
      tag.putString("entityId", EntityType.getId(entityType).toString());
      tag.putInt("weight", weight);
      return tag;
    }

    public static Entry fromNbt(NbtCompound tag) {
      return new Entry(
        tag.getString("entityId"),
        tag.getInt("weight")
      );
    }
  }
}
