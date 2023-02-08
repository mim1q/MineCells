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

public class GridPiecesGenerator {
  public static List<GridPiece> generatePieces(BlockPos startPos, Structure.Context context, int size, RoomGridGenerator generator) {
    List<RoomData> roomDataList = generator.generate(context);
    List<GridPiece> pieces = new ArrayList<>();
    for (RoomData data : roomDataList) {
      pieces.add(new GridPiece(context, data.poolId(), startPos.add(data.pos().multiply(size)).add(data.offset()), data.rotation(), size));
    }
    return pieces;
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public static List<GridPiece> generateWithHeightmap(BlockPos startPos, Optional<Heightmap.Type> projectStartToHeightmap, Structure.Context context, int size, RoomGridGenerator generator) {
    List<RoomData> roomDataList = generator.generate(context);
    List<GridPiece> pieces = new ArrayList<>();
    for (RoomData data : roomDataList) {
      BlockPos pos = startPos.add(data.pos().multiply(size));
      int heightmapY = projectStartToHeightmap.map(
        type -> context.chunkGenerator().getHeightOnGround(pos.getX() + size / 2, pos.getZ() + size / 2, type, context.world(), context.noiseConfig())
      ).orElse(0);
      int heightDiff = heightmapY - startPos.getY();
      pieces.add(new GridPiece(context, data.poolId(), startPos.add(data.pos().multiply(size)).add(data.offset()).add(0, heightDiff, 0), data.rotation(), size));
    }
    return pieces;
  }

  public record RoomData(
    Vec3i pos,
    BlockRotation rotation,
    Identifier poolId,
    Vec3i offset
  ) { }

  public static abstract class RoomGridGenerator {
    private final List<RoomData> rooms = new ArrayList<>();
    protected abstract void addRooms(Random random);

    public final List<RoomData> generate(Structure.Context context) {
      rooms.clear();
      addRooms(context.random());
      return rooms;
    }
    protected final void addRoom(Vec3i pos, BlockRotation rotation, Identifier poolId, Vec3i offset) {
      rooms.add(new RoomData(pos, rotation, poolId, offset));
    }

    protected final void addRoom(Vec3i pos, BlockRotation rotation, Identifier poolId) {
      addRoom(pos, rotation, poolId, Vec3i.ZERO);
    }

    public boolean usesHeightmap() {
      return false;
    }
  }
}
