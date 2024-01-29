package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class BetterPromenadeGridGenerator extends MultipartGridGenerator {
  private static final Identifier MAIN = MineCells.createId("promenade/overground_buildings");

  private static final Identifier PATH_STRAIGHT = MineCells.createId("promenade/path/straight");
  private static final Identifier PATH_TURN = MineCells.createId("promenade/path/turn");
  private static final Identifier PATH_CROSSROADS = MineCells.createId("promenade/path/crossroads");
  private static final Identifier PATH_CROSSROADS_POST = MineCells.createId("promenade/path/crossroads_post");

  public BetterPromenadeGridGenerator(int xPart, int zPart) {
    super(xPart, zPart);
  }

  @Override
  protected void addRooms(Random random) {
    for (var i = 0; i < 64; ++i) {
      addRoom(RoomData
        .create(0, 0, i, i == 10 ? PATH_CROSSROADS : PATH_STRAIGHT)
        .terrainFit()
        .rotation(random.nextBoolean() ? BlockRotation.NONE : BlockRotation.CLOCKWISE_180)
      );
      if (i == 10) {
        addRoom(RoomData
          .create(0, 0, i, PATH_CROSSROADS_POST)
          .terrainFit()
          .offset(new Vec3i(5, 0, 2))
        );
      }
    }
  }
}
