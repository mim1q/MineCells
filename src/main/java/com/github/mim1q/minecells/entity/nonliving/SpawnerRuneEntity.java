package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneData;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class SpawnerRuneEntity extends Entity {
  private Identifier dataId = MineCells.createId("prison_melee");
  private SpawnerRuneData data = MineCells.SPAWNER_RUNE_DATA.get(dataId);
  public boolean isVisible = true;

  private static final TrackedData<Integer> LAST_ACTIVATION_TIME = DataTracker.registerData(SpawnerRuneEntity.class, TrackedDataHandlerRegistry.INTEGER);

  public SpawnerRuneEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public void tick() {
    if (!world.isClient && age % 20 == 0 && data != null && !isCoolingDown()) {
      var d = data.playerDistance();
      for (var player : world.getEntitiesByClass(ServerPlayerEntity.class, Box.of(this.getPos(), d, d, d), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)) {
        if (canPlayerActivate(player)) {
          MineCellsData.getPlayerData(player, (ServerWorld) world).addActivatedSpawnerRune(MineCellsDimension.getFrom(world), getBlockPos());
          MineCellsData.syncCurrentPlayerData(player, (ServerWorld) world);
          spawnEntities();
          break;
        }
      }
    }
    if (world.isClient && age % 20 == 0) {
      isVisible = canClientPlayerActivate();
    }
  }

  private boolean isCoolingDown() {
    if (data == null) return false;
    return MathHelper.abs(dataTracker.get(LAST_ACTIVATION_TIME) - world.getTime()) < data.cooldown();
  }

  private boolean canPlayerActivate(PlayerEntity player) {
    var dimensionData = ((PlayerEntityAccessor)player).getCurrentMineCellsPlayerData();
    if (dimensionData == null) {
      return false;
    }
    return !dimensionData.hasActivatedSpawnerRune(MineCellsDimension.getFrom(world), getBlockPos());
  }

  private boolean canClientPlayerActivate() {
    var player = MinecraftClient.getInstance().player;
    if (player == null) return false;
    var dimensionData = ((PlayerEntityAccessor)player).getCurrentMineCellsPlayerData();
    if (dimensionData == null) {
      return false;
    }
    return !dimensionData.hasActivatedSpawnerRune(MineCellsDimension.getFrom(world), getBlockPos());
  }

  private void spawnEntities() {
    var entities = data.getSelectedEntities(random);
    for (var entityType : entities) {
      var entity = entityType.create(world);
      if (entity == null) continue;
      var dx = 2.0F * (random.nextFloat() - 0.5F) * data.spawnDistance();
      var dz = 2.0F * (random.nextFloat() - 0.5F) * data.spawnDistance();
      entity.setPosition(getX() + dx, getY(), getZ() + dz);
      if (entity instanceof HostileEntity hostile) {
        hostile.initialize((ServerWorldAccess) world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.NATURAL, null, null);
      }
      world.spawnEntity(entity);
    }
  }

  @Override
  protected void initDataTracker() {
    this.dataTracker.startTracking(LAST_ACTIVATION_TIME, 0);
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
