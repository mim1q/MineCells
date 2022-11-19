package com.github.mim1q.minecells.block.blockentity.spawnerrune;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.List;

public class SpawnerRuneBlockEntity extends BlockEntity {
  private SpawnerRuneData data = new SpawnerRuneData("", new EntryList(), 0, 0, 0);
  private int cooldown = 0;

  public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.SPAWNER_RUNE_BLOCK_ENTITY, pos, state);
  }

  public static void tick(World world, BlockPos pos, BlockState state, SpawnerRuneBlockEntity entity) {
    if (world.isClient) return;

    entity.cooldown = Math.max(0, entity.cooldown - 1);
    if (entity.cooldown == 0) {
      entity.cooldown = entity.data.maxCooldown;
      int rolls = entity.data.minRolls + world.random.nextInt(entity.data.maxRolls - entity.data.minRolls + 1);
      List<EntityType<?>> entityTypes = entity.data.entryList.selectEntityTypes(rolls, world.random);
      for (EntityType<?> entityType : entityTypes) {
        Entity spawnedEntity = entityType.create(world);
        if (spawnedEntity != null) {
          spawnedEntity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
          if (spawnedEntity instanceof MineCellsEntity mcEntity) {
            mcEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.SPAWNER, null, null);
          }
          world.spawnEntity(spawnedEntity);
        }
      }
    }
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    data = SpawnerRuneData.fromNbt(nbt);
    cooldown = nbt.getInt("cooldown");
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    data.writeNbt(nbt);
    nbt.putInt("cooldown", cooldown);
  }

  @Override
  public void setStackNbt(ItemStack stack) {
    super.setStackNbt(stack);
    data.writeNbt(stack.getOrCreateNbt());
  }
}
