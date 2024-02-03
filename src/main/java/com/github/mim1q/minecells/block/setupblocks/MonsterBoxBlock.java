package com.github.mim1q.minecells.block.setupblocks;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class MonsterBoxBlock extends SetupBlock {
  private final Identifier spawnerRuneDataId;


  public MonsterBoxBlock(Identifier spawnerRuneDataId) {
    super(Settings.copy(Blocks.BEDROCK));
    this.spawnerRuneDataId = spawnerRuneDataId;

  }

  @Override
  public boolean setup(World world, BlockPos pos, BlockState state) {
    world.removeBlock(pos, false);
    if (world.isClient) return false;
    SpawnerRuneController.spawnEntities((ServerWorld) world, spawnerRuneDataId, pos, e -> {
      if (e instanceof MobEntity entity) {
        entity.setPersistent();
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        entity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
        entity.resetPosition();
      }
    });
    return false;
  }
}
