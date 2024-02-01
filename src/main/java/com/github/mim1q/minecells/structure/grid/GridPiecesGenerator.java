package com.github.mim1q.minecells.structure.grid;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GridPiecesGenerator {
  public static List<GridPiece> generatePieces(BlockPos startPos, Optional<Heightmap.Type> projectStartToHeightmap, Structure.Context context, int size, RoomGridGenerator generator) {
    List<RoomData> roomDataList = generator.generate(context);
    List<GridPiece> pieces = new ArrayList<>();
    for (RoomData data : roomDataList) {
      if (projectStartToHeightmap.isPresent() && data.terrainFit) {
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
    final var heightmapY = projectStartToHeightmap.map(
      type -> context.chunkGenerator().getHeightOnGround(heightmapPos.getX(), heightmapPos.getZ(), type, context.world(), context.noiseConfig())
    ).orElse(0);
    final var heightDiff = heightmapY - startPos.getY();
    return new GridPiece(context, data.poolId, startPos.add(data.pos.multiply(size)).add(data.offset).add(0, heightDiff, 0), data.rotation, size);
  }

  public static class RoomData {
    private final Vec3i pos;
    private final BlockRotation rotation;
    private final Identifier poolId;
    private final Vec3i offset;
    private final boolean terrainFit;
    private final Vec3i terrainSamplePos;
    private final Vec3i terrainSampleOffset;

    public RoomData(Vec3i pos, BlockRotation rotation, Identifier poolId, Vec3i offset, boolean terrainFit, Vec3i terrainSamplePos, Vec3i terrainSampleOffset) {
      this.pos = pos;
      this.rotation = rotation;
      this.poolId = poolId;
      this.offset = offset;
      this.terrainFit = terrainFit;
      this.terrainSamplePos = terrainSamplePos;
      this.terrainSampleOffset = terrainSampleOffset;
    }

    public static RoomDataBuilder create(Vec3i pos, Identifier poolId) {
      return new RoomDataBuilder(pos, poolId);
    }

    public static RoomDataBuilder create(int x, int y, int z, Identifier poolId) {
      return create(new Vec3i(x, y, z), poolId);
    }

    public static final class RoomDataBuilder {
      public final Vec3i pos;
      public BlockRotation rotation = BlockRotation.NONE;
      public final Identifier poolId;
      public Vec3i offset = Vec3i.ZERO;
      public boolean terrainFit = false;
      public Vec3i terrainSamplePos;
      public Vec3i terrainSampleOffset = new Vec3i(8, 0, 8);

      public RoomDataBuilder(Vec3i pos, Identifier poolId) {
        this.pos = pos;
        this.terrainSamplePos = pos;
        this.poolId = poolId;
      }

      public RoomDataBuilder rotation(BlockRotation rotation) {
        this.rotation = rotation;
        return this;
      }

      public RoomDataBuilder offset(Vec3i offset) {
        this.offset = offset;
        return this;
      }

      public RoomDataBuilder offset(int x, int y, int z) {
        return offset(new Vec3i(x, y, z));
      }

      public RoomDataBuilder terrainFitOffset(int x, int y, int z) {
        offset(x, y, z);
        this.terrainSampleOffset = this.offset;
        return this;
      }

      public RoomDataBuilder terrainFit() {
        this.terrainFit = true;
        return this;
      }

      public RoomDataBuilder terrainFit(Vec3i terrainSamplePos) {
        this.terrainFit = true;
        this.terrainSamplePos = terrainSamplePos;
        return this;
      }

      public RoomDataBuilder terrainSampleOffset(Vec3i terrainSampleOffset) {
        this.terrainSampleOffset = terrainSampleOffset;
        return this;
      }

      public RoomDataBuilder terrainFit(int x, int y, int z) {
        return terrainFit(new Vec3i(x, y, z));
      }

      public RoomData build() {
        return new RoomData(pos, rotation, poolId, offset, terrainFit, terrainSamplePos, terrainSampleOffset);
      }
    }
  }

  public static abstract class RoomGridGenerator {
    private final List<RoomData> rooms = new ArrayList<>();

    protected abstract void addRooms(Random random);

    public List<RoomData> generate(Structure.Context context) {
      rooms.clear();
      addRooms(context.random());
      return rooms;
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

    protected void addRoom(RoomData.RoomDataBuilder builder) {
      rooms.add(builder.build());
    }

    public static RoomGridGenerator single(Identifier roomId) {
      return single(roomId, Vec3i.ZERO);
    }

    public static RoomGridGenerator single(Identifier roomId, Vec3i offset) {
      return new Single(roomId, offset);
    }

    protected static RoomData.RoomDataBuilder room(int x, int y, int z, Identifier poolId) {
      return RoomData.create(x, y, z, poolId);
    }

    protected static RoomData.RoomDataBuilder room(Vec3i pos, Identifier poolId) {
      return RoomData.create(pos, poolId);
    }

    public static final class Single extends RoomGridGenerator {
      private final Identifier roomId;
      private final Vec3i offset;

      Single(Identifier roomId, Vec3i offset) {
        this.roomId = roomId;
        this.offset = offset;
      }

      @Override
      protected void addRooms(Random random) {
        addRoom(Vec3i.ZERO, BlockRotation.random(random), roomId, offset);
      }
    }
  }
}
