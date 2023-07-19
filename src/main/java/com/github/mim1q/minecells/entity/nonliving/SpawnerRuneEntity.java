package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneData;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.entity.MineCellsEntity;
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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpawnerRuneEntity extends Entity {
  private Identifier dataId = MineCells.createId("unknown");
  private SpawnerRuneData data = null;
  public boolean isVisible = true;

  public SpawnerRuneEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public void tick() {
    if (!world.isClient && age % 10 == 0 && data != null) {
      var d = data.playerDistance();
      for (var player : world.getEntitiesByClass(ServerPlayerEntity.class, Box.of(this.getPos(), d, d, d), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)) {
        if (canPlayerActivate(player)) {
          MineCellsData.getPlayerData(player, (ServerWorld) world, null).addActivatedSpawnerRune(MineCellsDimension.of(world), getBlockPos());
          MineCellsData.syncCurrentPlayerData(player, (ServerWorld) world);
          spawnEntities(player);
          break;
        }
      }
    }
    if (world.isClient && age % 5 == 0) {
      var visible = canClientPlayerActivate();
      if (isVisible != visible) {
        ParticleUtils.addInBox(
          (ClientWorld) world,
          MineCellsParticles.SPECKLE.get(0xFF6A00),
          this.getBoundingBox().offset(0.0, 1.25, 0.0),
          5,
          new Vec3d(-0.2D, -0.2D, -0.2D).multiply(random.nextDouble() * 0.5D + 0.5D)
        );
        isVisible = visible;
      }
    }
  }

  private boolean canPlayerActivate(PlayerEntity player) {
    var dimensionData = ((PlayerEntityAccessor)player).getCurrentMineCellsPlayerData();
    if (dimensionData == null) {
      return false;
    }
    return !dimensionData.hasActivatedSpawnerRune(MineCellsDimension.of(world), getBlockPos());
  }

  private boolean canClientPlayerActivate() {
    var player = MinecraftClient.getInstance().player;
    if (player == null) return false;
    var dimensionData = ((PlayerEntityAccessor)player).getCurrentMineCellsPlayerData();
    if (dimensionData == null) {
      return false;
    }
    return !dimensionData.hasActivatedSpawnerRune(MineCellsDimension.of(world), getBlockPos());
  }

  private void spawnEntities(PlayerEntity spawningPlayer) {
    var entities = data.getSelectedEntities(random);
    for (var entityType : entities) {
      var entity = spawnEntity((ServerWorld) world, entityType, findPos(world, getBlockPos(), data.spawnDistance()), getBlockPos());
      if (entity instanceof HostileEntity hostile) {
        hostile.setTarget(spawningPlayer);
      }
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
    return pos;
  }

  private static Entity spawnEntity(ServerWorld world, EntityType<?> type, BlockPos pos, BlockPos runePos) {
    Entity spawnedEntity = type.create(world, null, null, null, pos, SpawnReason.NATURAL, false, false);
    if (spawnedEntity != null) {
      if (spawnedEntity instanceof MineCellsEntity mcEntity) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, runePos)) {
          ServerPlayNetworking.send(player, SpawnRuneParticlesS2CPacket.ID, new SpawnRuneParticlesS2CPacket(mcEntity.getBoundingBox().expand(0.5D)));
        }
      }
      world.spawnEntity(spawnedEntity);
      return spawnedEntity;
    }
    return null;
  }

  @Override
  protected void initDataTracker() {
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    setDataId(Identifier.tryParse(nbt.getString("dataId")));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putString("dataId", dataId.toString());
  }

  public void setDataId(Identifier id) {
    if (world.isClient) return;
    var newData = MineCells.SPAWNER_RUNE_DATA.get(id);
    if (newData == null) {
      MineCells.LOGGER.warn("Tried to load unknown spawner rune data with id: " + id
        + " at pos " + getBlockPos().toShortString()
        + " in dimension " + world.getRegistryKey().getValue().toString()
      );
      return;
    }
    this.dataId = id;
    this.data = MineCells.SPAWNER_RUNE_DATA.get(id);
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
