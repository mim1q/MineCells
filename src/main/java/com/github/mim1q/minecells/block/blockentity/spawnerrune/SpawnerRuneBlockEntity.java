package com.github.mim1q.minecells.block.blockentity.spawnerrune;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.network.s2c.SpawnRuneParticlesS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
      entity.markDirty();
    }

    if (world.getTime() % 10 == 0) {
      if (entity.cooldown == 0) {
        if (isPlayerNearby(world, pos, entity.data.playerRange)) {
          entity.cooldown = entity.data.maxCooldown;
          spawnEntities(world, pos, entity);
        }
      }
      entity.canCooldown = !areEntitiesNearby(world, pos);
    }
  }

  public static boolean isPlayerNearby(World world, BlockPos pos, double range) {
    return world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), range, true) != null;
  }

  private static boolean areEntitiesNearby(World world, BlockPos pos) {
    if (world == null) {
      return true;
    }
    List<MineCellsEntity> entities = world.getEntitiesByClass(
      MineCellsEntity.class,
      Box.of(Vec3d.ofCenter(pos), 32, 32, 32),
      entity -> entity.spawnRunePos != null && entity.spawnRunePos.equals(pos)
    );
    return entities.size() > 0;
  }

  private static void spawnEntities(World world, BlockPos pos, SpawnerRuneBlockEntity entity) {
    int rolls = entity.data.minRolls + world.random.nextInt(entity.data.maxRolls - entity.data.minRolls + 1);
    List<EntityType<?>> entityTypes = entity.data.entryList.selectEntityTypes(rolls, world.random);
    for (EntityType<?> entityType : entityTypes) {
      spawnEntity((ServerWorld) world, entityType, findPos(world, pos, entity.data.spawnRadius), pos);
    }
  }

  private static void spawnEntity(ServerWorld world, EntityType<?> type, BlockPos pos, BlockPos runePos) {
    Entity spawnedEntity = type.create(world, null, null, null, pos, SpawnReason.SPAWNER, false, false);
    if (spawnedEntity != null) {
      if (spawnedEntity instanceof MineCellsEntity mcEntity) {
        mcEntity.spawnRunePos = runePos;
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, runePos)) {
          ServerPlayNetworking.send(player, SpawnRuneParticlesS2CPacket.ID, new SpawnRuneParticlesS2CPacket(mcEntity.getBoundingBox().expand(0.5D)));
        }
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
