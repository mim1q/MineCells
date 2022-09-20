package com.github.mim1q.minecells.block.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MonsterBoxBlock extends SetupBlock {

  private final List<Entry> entries = new ArrayList<>();

  public MonsterBoxBlock(Entry...entries) {
    super();
    this.entries.addAll(List.of(entries));
  }

  public MonsterBoxBlock(EntityType<?> entityType) {
    this(new Entry(entityType, 1));
  }

  @Override
  public void setup(World world, BlockPos pos, BlockState state) {
    world.removeBlock(pos, false);
    EntityType<?> entityType = chooseRandomEntry(world.getRandom()).entityType;
    HostileEntity e = (HostileEntity) entityType.create(world);
    if (e != null) {
      e.setPersistent();
      e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      e.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
      world.spawnEntity(e);
    }
  }

  protected Entry chooseRandomEntry(Random random) {
    int totalWeight = 0;
    for (Entry entry : entries) {
      totalWeight += entry.weight;
    }
    float chance = random.nextInt(totalWeight + 1);
    for (Entry entry : entries) {
      chance -= entry.weight;
      if (chance <= 0) {
        return entry;
      }
    }
    return entries.get(0);
  }

  public record Entry(EntityType<?> entityType, int weight) { }
}
