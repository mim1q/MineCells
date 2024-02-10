package com.github.mim1q.minecells.data.spawner_runes;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneData.EntitySpawnData;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.network.s2c.SpawnRuneParticlesS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpawnerRuneController {

  private Identifier dataId = null;
  private SpawnerRuneData data = null;
  private boolean isVisible = false;

  public void tick(BlockPos pos, World world) {
    if (!world.isClient && data != null) {
      var d = data.playerDistance();
      for (var player : world.getEntitiesByClass(ServerPlayerEntity.class, Box.of(Vec3d.ofCenter(pos), d, d, d), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)) {
        if (canPlayerActivate(player, world, pos)) {
          MineCellsData.getPlayerData(player, (ServerWorld) world, null).addActivatedSpawnerRune(MineCellsDimension.of(world), pos);
          MineCellsData.syncCurrentPlayerData(player, (ServerWorld) world);
          spawnEntities(data, pos, player);
          break;
        }
      }
    }
    if (world.isClient) {
      var visible = canClientPlayerActivate(world, pos);
      if (isVisible != visible) {
        ParticleUtils.addInBox(
          (ClientWorld) world,
          MineCellsParticles.SPECKLE.get(0xFF6A00),
          Box.of(Vec3d.ofCenter(pos), 0.5, 0.5, 0.5),
          15,
          new Vec3d(-0.2D, -0.2D, -0.2D).multiply(world.getRandom().nextDouble() * 0.5D + 0.5D)
        );
        isVisible = visible;
      }
    }
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean visible) {
    isVisible = visible;
  }

  private void spawnEntities(SpawnerRuneData data, BlockPos pos, PlayerEntity spawningPlayer) {
    var world = spawningPlayer.getWorld();
    var entities = data.getSelectedEntities(world.getRandom());
    for (var entityData : entities) {
      var entity = spawnEntity(
        (ServerWorld) world,
        entityData,
        findPos(world, pos, data.spawnDistance()),
        pos,
        e -> {
        }
      );
      if (entity instanceof HostileEntity hostile && hostile.canSee(spawningPlayer)) {
        hostile.setTarget(spawningPlayer);
      }
    }
  }

  public static List<Entity> spawnEntities(ServerWorld world, Identifier dataId, BlockPos pos, Consumer<Entity> entityConsumer) {
    var data = MineCells.SPAWNER_RUNE_DATA.get(dataId);
    if (data == null) return List.of();
    var entities = data.getSelectedEntities(world.random);
    var result = new ArrayList<Entity>();
    for (var entityData : entities) {
      var entity = spawnEntity(world, entityData, findPos(world, pos, data.spawnDistance()), pos, entityConsumer);
      result.add(entity);
    }
    return result;
  }

  private boolean canPlayerActivate(PlayerEntity player, World world, BlockPos pos) {
    var dimensionData = ((PlayerEntityAccessor) player).getCurrentMineCellsPlayerData();
    if (dimensionData == null) {
      return false;
    }
    return !dimensionData.hasActivatedSpawnerRune(MineCellsDimension.of(world), pos);
  }

  private boolean canClientPlayerActivate(World world, BlockPos pos) {
    var player = MinecraftClient.getInstance().player;
    if (player == null) return false;
    var dimensionData = ((PlayerEntityAccessor) player).getCurrentMineCellsPlayerData();
    if (dimensionData == null) {
      return false;
    }
    return !dimensionData.hasActivatedSpawnerRune(MineCellsDimension.of(world), pos);
  }

  private static Entity spawnEntity(ServerWorld world, EntitySpawnData entityData, BlockPos pos, BlockPos runePos, Consumer<Entity> entityConsumer) {
    Entity spawnedEntity = entityData.entityType().create(world, null, null, pos, SpawnReason.NATURAL, false, false);
    if (spawnedEntity == null) return null;
    if (spawnedEntity instanceof LivingEntity livingEntity) {
      for (ServerPlayerEntity player : PlayerLookup.tracking(world, runePos)) {
        ServerPlayNetworking.send(player, SpawnRuneParticlesS2CPacket.ID, new SpawnRuneParticlesS2CPacket(livingEntity.getBoundingBox().expand(0.5D)));
      }
      entityData.attributeOverrides().forEach((attribute, value) -> {
        var instance = livingEntity.getAttributeInstance(attribute);
        if (instance != null) {
          instance.setBaseValue(value);
        }
      });
      livingEntity.setHealth(livingEntity.getMaxHealth());
      final var currentEntityNbt = new NbtCompound();
      livingEntity.writeCustomDataToNbt(currentEntityNbt);
      for (var entry : entityData.nbt().getKeys()) {
        currentEntityNbt.put(entry, entityData.nbt().get(entry));
      }
      livingEntity.readCustomDataFromNbt(currentEntityNbt);
      entityConsumer.accept(livingEntity);
    }
    world.spawnEntity(spawnedEntity);
    return spawnedEntity;
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
    return pos;
  }

  public void setDataId(World world, BlockPos pos, Identifier id) {
    var newData = MineCells.SPAWNER_RUNE_DATA.get(id);
    this.dataId = id;
    this.data = MineCells.SPAWNER_RUNE_DATA.get(id);

    if (world == null || world.isClient) return;
    if (newData == null) {
      MineCells.LOGGER.warn("Tried to load unknown spawner rune data with id: " + id
        + " at pos " + pos.toShortString()
        + " in dimension " + world.getRegistryKey().getValue().toString()
      );
    }
  }

  public Identifier getDataId() {
    return dataId;
  }
}
