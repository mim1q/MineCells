package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData.RoomDataBuilder;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;

import static com.github.mim1q.minecells.util.MathUtils.getClosestMultiplePosition;

public abstract class MultipartGridGenerator extends RoomGridGenerator {
  private final int xPart;
  private final int zPart;

  public MultipartGridGenerator(int xPart, int zPart) {
    this.xPart = xPart;
    this.zPart = zPart;
  }

  @Override
  public List<RoomData> generate(Structure.Context context) {
    var seed = getClosestMultiplePosition(context.chunkPos().getStartPos(), 1024).hashCode() + context.seed();
    context.random().setSeed(seed);
    return super.generate(context);
  }

  @Override
  protected void addRoom(RoomDataBuilder builder) {
    int x = builder.pos.getX() - 16 * xPart;
    int z = builder.pos.getZ() - 16 * zPart;
    if (x < 0 || z < 0 || x >= 16 || z >= 16) {
      return;
    }
    var newBuilder = RoomData
      .create(x - 8, builder.pos.getY(), z - 8, builder.poolId)
      .rotation(builder.rotation)
      .offset(builder.offset);

    if (builder.terrainFit) {
      newBuilder.terrainFit();
      newBuilder.terrainSamplePos = builder.terrainSamplePos;
    }
    super.addRoom(newBuilder);
  }
}
