package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import net.minecraft.util.math.BlockPos;
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
    return super.generate(context);
  }

  @Override
  protected void addRoom(RoomData data) {
    usedPositions.add(data.pos);

    int x = data.pos.getX() - 16 * xPart;
    int z = data.pos.getZ() - 16 * zPart;

    var terrainSampleX = data.terrainSamplePos.getX() - 16 * xPart;
    var terrainSampleZ = data.terrainSamplePos.getZ() - 16 * zPart;

    var newData = RoomData
      .create(x - 8, data.pos.getY(), z - 8, data.poolId)
      .rotation(data.rotation)
      .offset(data.offset);

    var outOfBounds = x < 0 || z < 0 || x >= 16 || z >= 16;

    if (data.specialPoint != null) {
      newData.specialPoint(data.specialPoint.id(), data.specialPoint.offset(), data.specialPoint.facing());

      specialPoints.add(new SpecialPoint(
        newData.specialPoint.id(),
        newData.pos.multiply(16).add(new BlockPos(newData.specialPoint.offset()).rotate(newData.rotation)),
        newData.specialPoint.facing().rotate(newData.rotation)
      ));
    }

    if (data.terrainFit) {
      newData.terrainFit();
      newData.terrainSamplePos = new Vec3i(terrainSampleX - 8, data.terrainSamplePos.getY(), terrainSampleZ - 8);
    }

    if (outOfBounds) {
      return;
    }

    rooms.add(newData);
  }
}
