package com.github.mim1q.minecells.block.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
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
    this(new Entry(entityType, 1.0F, 1));
  }

  @Override
  public void setup(World world, BlockPos pos, BlockState state) {
    world.removeBlock(pos, false);
    for (Entry entry : entries) {
      if (world.getRandom().nextFloat() <= entry.chance) {
        for (int i = 0; i < entry.count; i++) {
          HostileEntity e = (HostileEntity) entry.entityType.create(world);
          if (e != null) {
            e.setPersistent();
            e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            e.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
            world.spawnEntity(e);
          }
        }
      }
    }
  }

  public record Entry(EntityType<?> entityType, float chance, int count) { }
}
