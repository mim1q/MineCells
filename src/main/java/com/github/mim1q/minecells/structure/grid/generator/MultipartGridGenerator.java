package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import net.minecraft.util.math.Vec3i;
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
  protected void addRoom(RoomData data) {
    int x = data.pos.getX() - 16 * xPart;
    int z = data.pos.getZ() - 16 * zPart;

    var terrainSampleX = data.terrainSamplePos.getX() - 16 * xPart;
    var terrainSampleZ = data.terrainSamplePos.getZ() - 16 * zPart;

    if (x < 0 || z < 0 || x >= 16 || z >= 16) {
      return;
    }
    var newData = RoomData
      .create(x - 8, data.pos.getY(), z - 8, data.poolId)
      .rotation(data.rotation)
      .offset(data.offset);

    if (data.terrainFit) {
      newData.terrainFit();
      newData.terrainSamplePos = new Vec3i(terrainSampleX - 8, data.terrainSamplePos.getY(), terrainSampleZ - 8);
    }
    super.addRoom(newData);
  }
}
