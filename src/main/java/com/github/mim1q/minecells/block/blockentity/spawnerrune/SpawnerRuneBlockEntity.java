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
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.List;

public class SpawnerRuneBlockEntity extends BlockEntity {
  private SpawnerRuneData data = new SpawnerRuneData("", new EntryList(), 0, 0, 0, 0, 0);
  private int cooldown = 0;

  public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.SPAWNER_RUNE_BLOCK_ENTITY, pos, state);
  }

  public static void tick(World world, BlockPos pos, BlockState state, SpawnerRuneBlockEntity entity) {
    if (world.isClient) return;
    entity.cooldown = Math.max(0, entity.cooldown - 1);
    if (entity.cooldown == 0 && world.getTime() % 10 == 0) {
      if (world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), entity.data.playerRange, false) != null) {
        entity.cooldown = entity.data.maxCooldown;
        spawnEntities(world, pos, entity);
      }
    }
  }

  private static void spawnEntities(World world, BlockPos pos, SpawnerRuneBlockEntity entity) {
    int rolls = entity.data.minRolls + world.random.nextInt(entity.data.maxRolls - entity.data.minRolls + 1);
    List<EntityType<?>> entityTypes = entity.data.entryList.selectEntityTypes(rolls, world.random);
    for (EntityType<?> entityType : entityTypes) {
      spawnEntity(world, entityType, findPos(world, pos, entity.data.spawnRadius));
    }
  }

  private static void spawnEntity(World world, EntityType<?> type, BlockPos pos) {
    Entity spawnedEntity = type.create(world);
    if (spawnedEntity != null) {
      spawnedEntity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
      if (spawnedEntity instanceof MineCellsEntity mcEntity) {
        mcEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.SPAWNER, null, null);
      }
      world.spawnEntity(spawnedEntity);
    }
  }

  private static BlockPos findPos(World world, BlockPos pos, float radius) {
    int x = pos.getX() + (int) (radius * (world.random.nextFloat() - 0.5));
    int z = pos.getZ() + (int) (radius * (world.random.nextFloat() - 0.5));
    int y = pos.getY();
    for (int i = 0; i < 4; i++) {
      BlockState state = world.getBlockState(new BlockPos(x, y, z));
      BlockState stateBelow = world.getBlockState(new BlockPos(x, y - 1, z));
      BlockState stateAbove = world.getBlockState(new BlockPos(x, y + 1, z));
      boolean solidBelow = stateBelow.isSideSolidFullSquare(world, new BlockPos(x, y - 1, z), Direction.UP);
      boolean empty = state.getCollisionShape(world, new BlockPos(x, y, z)).isEmpty();
      boolean emptyAbove = stateAbove.getCollisionShape(world, new BlockPos(x, y + 1, z)).isEmpty();

      if (solidBelow && empty && emptyAbove) {
        return new BlockPos(x, y, z);
      }
      y++;
    }
    return pos.add(0, 1, 0);
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
