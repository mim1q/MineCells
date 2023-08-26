package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.network.s2c.ShockwaveClientEventS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ShockwavePlacer extends Entity {
  private static final double STEPS_PER_POS = 2;

  private final Map<Integer, Set<BlockPos>> positions; // age mapped to positions
  private int maxAge;
  private BlockState block;
  @Nullable private UUID ownerUuid;
  private float damage;
  private int blockAge;

  private ShockwavePlacer(
    EntityType<?> type,
    World world,
    Map<Integer, Set<BlockPos>> positions,
    BlockState block,
    @Nullable UUID ownerUuid,
    float damage,
    int blockAge
  ) {
    super(type, world);
    this.positions = positions;
    this.maxAge = positions.keySet().stream().max(Integer::compareTo).orElse(5);
    this.block = block;
    this.ownerUuid = ownerUuid;
    this.damage = damage;
    this.blockAge = blockAge;
  }

  public ShockwavePlacer(EntityType<?> type, World world) {
    this(type, world, new HashMap<>(), Blocks.FIRE.getDefaultState(), null, 0, 20);
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient()) return;

    var posList = positions.get(age);
    if (posList != null) {
      for (var pos : posList) {
        var placedPos = tryPlace(pos);
        if (placedPos != null) {
          getWorld().scheduleBlockTick(placedPos, block.getBlock(), blockAge);
          PlayerLookup
            .tracking((ServerWorld) getWorld(), placedPos)
            .forEach(player -> ShockwaveClientEventS2CPacket.send(player, block.getBlock(), placedPos, false));
          damageEntities(placedPos);
        }
      }
    }

    if (age > maxAge) {
      this.remove(RemovalReason.DISCARDED);
    }
  }

  private BlockPos tryPlace(BlockPos position) {
    var positions = new BlockPos[]{position, position.up(), position.down()};
    for (var pos : positions) {
      if (block.canPlaceAt(getWorld(), pos)) {
        getWorld().setBlockState(pos, block);
        return pos;
      }
    }
    return null;
  }

  private Box getDamageBox(BlockPos position) {
    return new Box(position)
      .expand(0.1, 0.0, 0.1)
      .offset(0, -0.25, 0);
  }

  private void damageEntities(BlockPos position) {
    var isPlayer = getWorld().getPlayerByUuid(ownerUuid) != null;
    Predicate<LivingEntity> predicate = isPlayer ? e -> e.getUuid() != ownerUuid : e -> !(e instanceof HostileEntity);
    var entities = getWorld().getEntitiesByClass(LivingEntity.class, getDamageBox(position), predicate);
    for (var entity : entities) {
      entity.damage(getWorld().getDamageSources().onFire(), damage);
    }
  }

  @Override
  public boolean shouldRender(double distance) {
    return false;
  }

  @Override
  protected void initDataTracker() {

  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    var nbtPositions = nbt.getCompound("positions");
    positions.clear();
    for (var k : nbtPositions.getKeys()) {
      var v = nbtPositions.getLongArray(k);
      positions.put(Integer.parseInt(k), Arrays.stream(v).mapToObj(BlockPos::fromLong).collect(Collectors.toSet()));
    }
    maxAge = nbt.getInt("maxAge");
    if (nbt.contains("block")) {
      var blockNbt = nbt.getCompound("block");
      block = BlockState.CODEC
        .decode(NbtOps.INSTANCE, blockNbt)
        .result()
        .orElse(new Pair<>(MineCellsBlocks.SHOCKWAVE_FLAME.getDefaultState(), null))
        .getFirst();
    }
    if (nbt.containsUuid("ownerUuid")) {
      ownerUuid = nbt.getUuid("ownerUuid");
    }
    damage = nbt.getFloat("damage");
    blockAge = nbt.getInt("blockAge");
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    var nbtPositions = new NbtCompound();
    positions.forEach((k, v) -> {
      nbtPositions.putLongArray(k.toString(), v.stream().map(BlockPos::asLong).toList());
    });
    nbt.put("positions", nbtPositions);
    nbt.putInt("maxAge", maxAge);
    BlockState.CODEC
      .encodeStart(NbtOps.INSTANCE, block)
      .result()
      .ifPresent(blockNbt -> nbt.put("block", blockNbt));
    if (ownerUuid != null) {
      nbt.putUuid("ownerUuid", ownerUuid);
    }
    nbt.putFloat("damage", damage);
    nbt.putFloat("blockAge", blockAge);
  }

  public static ShockwavePlacer createLine(
    World world,
    Vec3d startPos,
    Vec3d endPos,
    float interval,
    BlockState block,
    @Nullable UUID ownerUuid,
    float damage
  ) {
    var map = new HashMap<Integer, Set<BlockPos>>();
    var diff = endPos.subtract(startPos);
    var stepLength = 1 / STEPS_PER_POS;
    var step = diff.normalize().multiply(stepLength);
    var pos = startPos;

    var accumulatedLength = 0;
    for (var i = 1; accumulatedLength <= diff.length() + 2; i++) {
      var set = new HashSet<BlockPos>();
      for (var j = 0; j < STEPS_PER_POS; j++) {
        set.add(BlockPos.ofFloored(pos));
        pos = pos.add(step);
      }
      var index = (int) (i * interval);
      if (map.containsKey(index)) {
        map.get(index).addAll(set);
      } else {
        map.put((int) (i * interval), set);
      }
      accumulatedLength += 1;
    }

    return new ShockwavePlacer(MineCellsEntities.SHOCKWAVE_PLACER, world, map, block, ownerUuid, damage, (int) interval + 10);
  }

  public static ShockwavePlacer createCircle(
    World world,
    Vec3d origin,
    int radius,
    float interval,
    BlockState block,
    @Nullable UUID ownerUuid,
    float damage
  ) {
    var map = new HashMap<Integer, Set<BlockPos>>();
    for (var i = 1; i < radius; i++) {
      var set = new HashSet<BlockPos>();
      for (var angle = 0; angle < i * 8; angle++) {
        var x = (float) Math.cos(angle * Math.PI / (i * 4)) * i;
        var z = (float) Math.sin(angle * Math.PI / (i * 4)) * i;
        set.add(BlockPos.ofFloored(origin).add(new BlockPos(Math.round(x), 0, Math.round(z))));
      }
      var index = (int) (i * interval);
      if (map.containsKey(index)) {
        map.get(index).addAll(set);
      } else {
        map.put((int) (i * interval), set);
      }
    }
    return new ShockwavePlacer(MineCellsEntities.SHOCKWAVE_PLACER, world, map, block, ownerUuid, damage, (int) interval * 2);
  }
}
