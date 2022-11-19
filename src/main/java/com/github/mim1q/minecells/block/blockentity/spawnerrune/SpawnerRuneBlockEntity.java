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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.List;

public class SpawnerRuneBlockEntity extends BlockEntity {
  private SpawnerRuneData data = new SpawnerRuneData("", new EntryList(), 0, 0, 0, 0, 0);
  private int cooldown = 20;
  private boolean canCooldown = false;

  public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.SPAWNER_RUNE_BLOCK_ENTITY, pos, state);
  }

  public static void tick(World world, BlockPos pos, BlockState state, SpawnerRuneBlockEntity entity) {
    if (world.isClient) return;

    if (entity.canCooldown) {
      entity.cooldown = Math.max(0, entity.cooldown - 1);
    }

    if (world.getTime() % 10 == 0) {
      entity.canCooldown = !entity.areEntitiesNearby();
      if (entity.cooldown == 0) {
        if (world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), entity.data.playerRange, false) != null) {
          entity.cooldown = entity.data.maxCooldown;
          spawnEntities(world, pos, entity);
        }
      }
    }

    entity.markDirty();
  }

  private boolean areEntitiesNearby() {
    if (cooldown <= 0 || world == null) {
      return true;
    }
    EntryList list = data.entryList;
    for (int i = 0; i < list.entries.size(); i++) {
      EntityType<?> type = list.entries.get(i).entityType;
      float range = (data.spawnRadius + 1.0F) * 2.0F;
      List<? extends Entity> entities = world.getEntitiesByType(type, Box.of(Vec3d.ofCenter(pos), range, range, range), Entity::isAlive);
      if (entities.size() > 0) {
        return true;
      }
    }
    return false;
  }

  private static void spawnEntities(World world, BlockPos pos, SpawnerRuneBlockEntity entity) {
    int rolls = entity.data.minRolls + world.random.nextInt(entity.data.maxRolls - entity.data.minRolls + 1);
    List<EntityType<?>> entityTypes = entity.data.entryList.selectEntityTypes(rolls, world.random);
    for (EntityType<?> entityType : entityTypes) {
      int id = spawnEntity(world, entityType, findPos(world, pos, entity.data.spawnRadius));
    }
  }

  private static int spawnEntity(World world, EntityType<?> type, BlockPos pos) {
    Entity spawnedEntity = type.create(world);
    if (spawnedEntity != null) {
      spawnedEntity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
      if (spawnedEntity instanceof MineCellsEntity mcEntity) {
        mcEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.SPAWNER, null, null);
      }
      world.spawnEntity(spawnedEntity);
      return spawnedEntity.getId();
    }
    return -1;
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
