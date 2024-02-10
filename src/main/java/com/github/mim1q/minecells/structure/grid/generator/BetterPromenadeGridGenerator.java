package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class BetterPromenadeGridGenerator extends MultipartGridGenerator {
  // Paths
  private static final Identifier PATH_STRAIGHT = pool("path/straight");
  private static final Identifier PATH_TURN = pool("path/turn");
  private static final Identifier PATH_CROSSROADS = pool("path/crossroads");
  private static final Identifier PATH_CROSSROADS_POST = pool("path/crossroads_post");
  private static final Identifier PATH_BUILDING = pool("path/building");
  private static final Identifier PATH_HALF = pool("path/half");

  // Buildings
  private static final Identifier BUILDING_OVERGROUND = pool("overground");
  private static final Identifier BUILDING_OVERGROUND_END = pool("overground_end");
  private static final Identifier BUILDING_OVERGROUND_BASE = pool("overground_base");
  private static final Identifier BUILDING_UNDERGROUND = pool("underground");

  // Special buildings
  private static final Identifier RAMPARTS_TOWER = pool("ramparts_tower");
  private static final Identifier VINE_RUNE = pool("special/vine_rune");
  private static final Identifier SPAWN = pool("spawn");

  public BetterPromenadeGridGenerator(int xPart, int zPart) {
    super(xPart, zPart);
  }

  @Override
  protected void addRooms(Random random) {
    addRoom(room(32, 0, 32, PATH_HALF).terrainFit().rotation(BlockRotation.CLOCKWISE_180));
    addRoom(room(32, 0, 32, SPAWN).terrainFit().offset(0, -7, 0));
    // Main road
    final var mainRoad = addPath(new Vec3i(32, 0, 32), BlockRotation.NONE, 23, random, 8);
    final var mainRoadEnd = mainRoad.getLeft();
    final var mainRoadSkipped = mainRoad.getRight();
    // Crossroads
    addRoom(room(mainRoadSkipped, PATH_CROSSROADS));
    addRoom(room(mainRoadSkipped, PATH_CROSSROADS_POST).terrainFit().terrainFitOffset(5, 0, 6));
    // Side road
    final var sideRoad = addPath(mainRoadSkipped, BlockRotation.COUNTERCLOCKWISE_90, 8, random, -1);
    final var sideRoadEnd = sideRoad.getLeft();
    // Buildings
    addBuilding(mainRoadSkipped.add(-2, 0, 1), 7, BlockRotation.NONE, random, false);
    // End
    addRoom(room(mainRoadEnd.add(0, 0, 1), RAMPARTS_TOWER).terrainFit(mainRoadEnd.getX(), mainRoadEnd.getY(), mainRoadEnd.getZ()).terrainSampleOffset(8, 0, 16));
    addRoom(room(sideRoadEnd, VINE_RUNE).terrainFit().terrainSampleOffset(14, 0, 8).offset(0, -21, 0).rotation(BlockRotation.CLOCKWISE_90));
  }

  private void addBuilding(Vec3i start, int length, BlockRotation rotation, Random random, boolean underground) {
    var direction = rotation.rotate(Direction.SOUTH).getVector();
    var fitPos = start.add(direction.multiply(length / 2));
    addRoom(room(start.subtract(direction), BUILDING_OVERGROUND_END).terrainFit(fitPos));
    for (var i = 0; i < length; ++i) {
      addRoom(room(start.add(direction.multiply(i)), BUILDING_OVERGROUND).terrainFit(fitPos));
      addRoom(room(start.add(direction.multiply(i)).down(), BUILDING_OVERGROUND_BASE).terrainFit(fitPos));
    }
    addRoom(room(start.add(direction.multiply(length)), BUILDING_OVERGROUND_END).rotation(BlockRotation.CLOCKWISE_180).terrainFit(fitPos));
  }

  private Pair<Vec3i, Vec3i> addPath(Vec3i start, BlockRotation rotation, int length, Random random, int skippedPos) {
    final var direction = rotation.rotate(Direction.SOUTH);
    final var sideDirection = direction.rotateYCounterclockwise();
    Vec3i skippedVec = null;
    var pos = start;
    for (var i = 1; i <= length; ++i) {
      pos = pos.add(direction.getVector());

      if (i == skippedPos) {
        skippedVec = pos;
        continue;
      }

      final var turn = random.nextFloat() < 0.25f;

      if (turn) {
        if (random.nextBoolean()) {
          addRoom(room(pos, PATH_TURN).rotation(BlockRotation.COUNTERCLOCKWISE_90.rotate(rotation)));
          pos = pos.subtract(sideDirection.getVector());
          addRoom(room(pos, PATH_TURN).rotation(BlockRotation.CLOCKWISE_90.rotate(rotation)));
        } else {
          addRoom(room(pos, PATH_TURN).rotation(BlockRotation.NONE.rotate(rotation)));
          pos = pos.add(sideDirection.getVector());
          addRoom(room(pos, PATH_TURN).rotation(BlockRotation.CLOCKWISE_180.rotate(rotation)));
        }
      } else {
        addRoom(room(pos, PATH_STRAIGHT).rotation(BlockRotation.NONE.rotate(rotation)));
//        addRoom(room(pos.add(sideDirection.getVector()), MAIN).terrainFit().rotation(random.nextBoolean() ? BlockRotation.NONE.rotate(rotation) : BlockRotation.CLOCKWISE_180.rotate(rotation)));
//        addRoom(room(pos.subtract(sideDirection.getVector()), MAIN).terrainFit().rotation(random.nextBoolean() ? BlockRotation.NONE.rotate(rotation) : BlockRotation.CLOCKWISE_180.rotate(rotation)));
      }
    }
    return new Pair<>(pos, skippedVec);
  }

  private static Identifier pool(String path) {
    return MineCells.createId("promenade/" + path);
  }
}
