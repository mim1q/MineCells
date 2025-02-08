package com.github.mim1q.minecells.structure.grid;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;

import java.util.*;

import static com.github.mim1q.minecells.util.MathUtils.getClosestMultiplePosition;
import static com.github.mim1q.minecells.util.MathUtils.getRotatedOffsetWithinChunk;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GridPiecesGenerator {
  public static List<GridPiece> generatePieces(BlockPos startPos, Optional<Heightmap.Type> projectStartToHeightmap, Structure.Context context, int size, RoomGridGenerator generator) {
    List<RoomData> roomDataList = generator.generate(context);
    List<GridPiece> pieces = new ArrayList<>();
    for (RoomData data : roomDataList) {
      if (data.terrainFit) {
        pieces.add(getTerrainFitPiece(data, startPos, projectStartToHeightmap, context, size));
      } else {
        pieces.add(new GridPiece(context, data.poolId, startPos.add(data.pos.multiply(size)).add(data.offset), data.rotation, size));
      }
    }
    return pieces;
  }

  public static GridPiece getTerrainFitPiece(RoomData data, BlockPos startPos, Optional<Heightmap.Type> projectStartToHeightmap, Structure.Context context, int size) {
    final var pos = startPos.add(data.terrainSamplePos.multiply(size));
    final var heightmapPos = pos.add(data.terrainSampleOffset);
    final var heightmapY = context.chunkGenerator().getHeightOnGround(heightmapPos.getX(), heightmapPos.getZ(), projectStartToHeightmap.orElse(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES), context.world(), context.noiseConfig());
    final var heightDiff = heightmapY - startPos.getY();
    return new GridPiece(context, data.poolId, startPos.add(data.pos.multiply(size)).add(data.offset).add(0, heightDiff, 0), data.rotation, size);
  }

  public static class RoomData {
    public final Vec3i pos;
    public final Identifier poolId;
    public BlockRotation rotation = BlockRotation.NONE;
    public Vec3i offset = Vec3i.ZERO;
    public boolean terrainFit = false;
    public Vec3i terrainSamplePos;
    public Vec3i terrainSampleOffset = new Vec3i(8, 0, 8);
    public RoomGridGenerator.SpecialPoint specialPoint = null;

    public RoomData(Vec3i pos, Identifier poolId) {
      this.pos = pos;
      this.poolId = poolId;
      this.terrainSamplePos = pos;
    }

    public static RoomData create(Vec3i pos, Identifier poolId) {
      return new RoomData(pos, poolId);
    }

    public static RoomData create(int x, int y, int z, Identifier poolId) {
      return create(new Vec3i(x, y, z), poolId);
    }

    public RoomData rotation(BlockRotation rotation) {
      this.rotation = rotation;
      return this;
    }

    public RoomData offset(Vec3i offset) {
      this.offset = offset;
      return this;
    }

    public RoomData offset(int x, int y, int z) {
      return offset(new Vec3i(x, y, z));
    }

    public RoomData terrainFitOffset(int x, int y, int z) {
      offset(x, y, z);
      this.terrainSampleOffset = this.offset;
      this.terrainFit();
      return this;
    }

    public RoomData terrainFit() {
      this.terrainFit = true;
      return this;
    }

    public RoomData terrainFit(Vec3i pos) {
      this.terrainFit = true;
      this.terrainSamplePos = pos;
      return this;
    }

    public RoomData terrainFit(int x, int y, int z) {
      return terrainFit(new Vec3i(x, y, z));
    }

    public RoomData terrainSampleOffset(int x, int y, int z) {
      terrainSampleOffset = new Vec3i(x, y, z);
      return this;
    }

    public RoomData specialPoint(Identifier id, Vec3i offset, BlockRotation facing) {
      if (id == null) return this;
      this.specialPoint = new RoomGridGenerator.SpecialPoint(id, offset, facing);
      return this;
    }
  }

  public static abstract class RoomGridGenerator {
    public record SpecialPoint(
      Identifier id,
      Vec3i offset,
      BlockRotation facing
    ) {
    }

    protected static final Identifier EMPTY = Identifier.of("minecraft", "empty");

    protected final List<RoomData> rooms = new ArrayList<>();
    protected final Set<Vec3i> usedPositions = new HashSet<>();
    protected final List<SpecialPoint> specialPoints = new ArrayList<>();

    protected abstract void addRooms(Random random);

    public List<RoomData> generate(Structure.Context context) {
      var seed = getClosestMultiplePosition(context.chunkPos().getStartPos(), 1024).hashCode() ^ context.seed();
      context.random().setSeed(seed);
      System.out.println(seed);

      rooms.clear();
      usedPositions.clear();
      specialPoints.clear();
      addRooms(context.random());
      return rooms;
    }

    public List<SpecialPoint> generateSpecialPoints(Structure.Context context) {
      generate(context);
      return specialPoints;
    }

    protected final void addRoom(Vec3i pos, BlockRotation rotation, Identifier poolId, Vec3i offset, boolean terrainFit) {
      var builder = RoomData.create(pos, poolId).rotation(rotation).offset(offset);
      if (terrainFit) {
        builder.terrainFit();
      }
      addRoom(builder);
    }

    protected final void addRoom(Vec3i pos, BlockRotation rotation, Identifier poolId, Vec3i offset) {
      addRoom(pos, rotation, poolId, offset, false);
    }

    protected final void addRoom(Vec3i pos, BlockRotation rotation, Identifier poolId) {
      addRoom(pos, rotation, poolId, Vec3i.ZERO);
    }

    protected final void addTerrainFitRoom(Vec3i pos, BlockRotation rotation, Identifier poolId, Vec3i offset) {
      addRoom(pos, rotation, poolId, offset, true);
    }

    protected final void addTerrainFitRoom(Vec3i pos, BlockRotation rotation, Identifier poolId) {
      addTerrainFitRoom(pos, rotation, poolId, Vec3i.ZERO);
    }

    protected void addRoom(RoomData roomData) {
      rooms.add(roomData);
      usedPositions.add(roomData.pos);
      if (roomData.specialPoint != null) {
        specialPoints.add(new SpecialPoint(
          roomData.specialPoint.id(),
          roomData.pos.multiply(16).add(getRotatedOffsetWithinChunk(roomData.specialPoint.offset, roomData.rotation)),
          roomData.specialPoint.facing().rotate(roomData.rotation)
        ));
      }
    }

    protected boolean isPositionUsed(Vec3i pos) {
      return usedPositions.contains(pos);
    }

    public abstract int getVersion();

    public static RoomGridGenerator single(Identifier roomId, int version) {
      return single(roomId, Vec3i.ZERO, version);
    }

    public static RoomGridGenerator single(Identifier roomId, Vec3i offset, int version) {
      return new Single(roomId, offset, version);
    }

    protected static RoomData room(int x, int y, int z, Identifier poolId) {
      return RoomData.create(x, y, z, poolId);
    }

    protected static RoomData room(Vec3i pos, Identifier poolId) {
      return RoomData.create(pos, poolId);
    }

    public static final class Single extends RoomGridGenerator {
      private final Identifier roomId;
      private final Vec3i offset;
      private final int version;

      Single(Identifier roomId, Vec3i offset, int version) {
        this.roomId = roomId;
        this.offset = offset;
        this.version = version;
      }

      @Override
      protected void addRooms(Random random) {
        addRoom(Vec3i.ZERO, BlockRotation.random(random), roomId, offset);
      }

      @Override
      public int getVersion() {
        return version;
      }
    }
  }
}
