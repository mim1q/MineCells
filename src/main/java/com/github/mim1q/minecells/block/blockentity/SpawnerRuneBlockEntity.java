package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SpawnerRuneBlockEntity extends BlockEntity {
  private EntryList entryList = new EntryList();

  public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.SPAWNER_RUNE_BLOCK_ENTITY, pos, state);
//    this.entryList = new EntryList()
//      .addEntry(EntityType.ZOMBIE, 1)
//      .addEntry(EntityType.SKELETON, 1)
//      .addEntry(EntityType.CREEPER, 1);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    this.entryList = EntryList.fromNbt(nbt.getList("entryList", 10));
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    nbt.put("entryList", entryList.toNbt());
  }

  @Override
  public void setStackNbt(ItemStack stack) {
    super.setStackNbt(stack);
    stack.getOrCreateNbt().put("entryList", entryList.toNbt());
  }

  public static class EntryList {
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
