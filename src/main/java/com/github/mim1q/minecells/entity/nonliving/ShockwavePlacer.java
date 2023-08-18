package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ShockwavePlacer extends Entity {
  private final Map<Integer, Set<BlockPos>> positions; // age mapped to positions
  private int maxAge;
  private BlockState block;
  @Nullable private UUID ownerUuid;

  private ShockwavePlacer(
    EntityType<?> type,
    World world,
    Map<Integer, Set<BlockPos>> positions,
    BlockState block,
    @Nullable UUID ownerUuid
  ) {
    super(type, world);
    this.positions = positions;
    this.maxAge = positions.keySet().stream().max(Integer::compareTo).orElse(5);
    this.block = block;
    this.ownerUuid = ownerUuid;
  }

  public ShockwavePlacer(EntityType<?> type, World world) {
    this(type, world, new HashMap<>(), Blocks.FIRE.getDefaultState(), null);
  }

  public static ShockwavePlacer createLine(
    World world,
    Vec3d startPos,
    Vec3d endPos,
    float interval,
    BlockState block,
    @Nullable UUID ownerUuid
  ) {
    var map = new HashMap<Integer, Set<BlockPos>>();
    var diff = endPos.subtract(startPos);
    var length = diff.length();
    var accumulatedLength = 0.0;
    var step = diff.normalize().multiply(0.5);
    var pos = startPos;

    for (var i = 1; accumulatedLength <= length + 2.0; i++) {
      var set = new HashSet<BlockPos>();
      set.add(BlockPos.ofFloored(pos));
      pos = pos.add(step);
      set.add(BlockPos.ofFloored(pos));
      pos = pos.add(step);
      map.put((int)(i * interval), set);
      accumulatedLength += 1.0;
    }

    return new ShockwavePlacer(
      MineCellsEntities.SHOCKWAVE_PLACER,
      world,
      map,
      block,
      ownerUuid
    );
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient) return;

    if (this.age > this.maxAge) {
      this.remove(RemovalReason.DISCARDED);
    }
    var posList = positions.get(this.age);
    if (posList != null) {
      for (var pos : posList) {
        var placedPos = tryPlace(pos);
        if (placedPos != null) {
          getWorld().scheduleBlockTick(placedPos, block.getBlock(), 20);
        }
      }
    }
  }

  private BlockPos tryPlace(BlockPos position)
  {
    var positions = new BlockPos[]{position, position.up(), position.down()};
    for (var pos : positions) {
      if (block.canPlaceAt(getWorld(), pos)) {
        getWorld().setBlockState(pos, block);
        return pos;
      }
    }
    return null;
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
  }
}
