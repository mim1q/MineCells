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
  private static final Identifier BUILDING_OVERGROUND_ELEVATOR = pool("overground_elevator");
  private static final Identifier BUILDING_UNDERGROUND = pool("underground");
  private static final Identifier BUILDING_UNDERGROUND_END = pool("underground_end");

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
    addRoom(room(32, 0, 32, SPAWN).terrainFit().terrainFitOffset(8, 0, 15).offset(0, -7, 0));
    // Main road
    final var mainRoad = addPath(new Vec3i(32, 0, 33), BlockRotation.NONE, 23, random, 3);
    final var mainRoadEnd = mainRoad.getLeft();
    final var mainRoadSkipped = mainRoad.getRight();
    // Crossroads
    addRoom(room(mainRoadSkipped, PATH_CROSSROADS));
    addRoom(room(mainRoadSkipped, PATH_CROSSROADS_POST).terrainFit().terrainFitOffset(5, 0, 6));
    // Side road
    final var sideRoad = addPath(mainRoadSkipped.add(1, 0, 0), BlockRotation.COUNTERCLOCKWISE_90, 9, random, -1);
    final var sideRoadEnd = sideRoad.getLeft();
    // End
    addRoom(room(mainRoadEnd.add(0, 0, 1), RAMPARTS_TOWER).terrainFit(mainRoadEnd).terrainSampleOffset(8, 0, 15));
    addRoom(room(sideRoadEnd, VINE_RUNE).terrainFit().terrainSampleOffset(14, 0, 8).offset(0, -21, 0).rotation(BlockRotation.CLOCKWISE_90));

    // Additional buildings
    tryPlaceBuildingsBetween(new Vec3i(1, 0, 32), new Vec3i(63, 0, 58), random, 32, BlockRotation.CLOCKWISE_180);
    tryPlaceBuildingsBetween(new Vec3i(1, 0, 1), new Vec3i(63, 0, 30), random, 48, BlockRotation.NONE);
  }

  private void addBuilding(Vec3i start, int length, BlockRotation rotation, Random random) {
    if (length < 1) return;

    var underground = length > 3 && random.nextFloat() < 0.66;

    var direction = rotation.rotate(Direction.SOUTH).getVector();
    var fitPos = start.add(direction.multiply(length / 2));
    addRoom(room(start.subtract(direction), BUILDING_OVERGROUND_END).rotation(rotation).terrainFit(fitPos));
    var skipped = underground ? random.nextInt(length - 1) : -1;

    for (var i = 0; i < length; ++i) {
      if (i == skipped) {
        addRoom(room(start.add(direction.multiply(i)).down(2), BUILDING_OVERGROUND_ELEVATOR).rotation(rotation).terrainFit(fitPos));
        continue;
      }
      var randomRotation = random.nextBoolean() ? rotation : BlockRotation.CLOCKWISE_180.rotate(rotation);
      addRoom(room(start.add(direction.multiply(i)), BUILDING_OVERGROUND).rotation(randomRotation).terrainFit(fitPos));
      addRoom(room(start.add(direction.multiply(i)).down(), BUILDING_OVERGROUND_BASE).rotation(rotation).terrainFit(fitPos));

      if (underground) {
        if (i == length - 1 && random.nextFloat() < 0.67) {
          addRoom(room(start.add(direction.multiply(i).down(3)), BUILDING_UNDERGROUND_END).rotation(rotation).terrainFit(fitPos));
        } else {
          addRoom(room(start.add(direction.multiply(i)).down(2), BUILDING_UNDERGROUND).rotation(rotation).terrainFit(fitPos));
        }
      } else {
        addRoom(room(start.add(direction.multiply(i)).down(2), BUILDING_OVERGROUND_BASE).rotation(rotation).terrainFit(fitPos));
      }
    }
    addRoom(room(start.add(direction.multiply(length)), BUILDING_OVERGROUND_END).rotation(BlockRotation.CLOCKWISE_180.rotate(rotation)).terrainFit(fitPos));
  }

  private void tryPlaceBuildingsBetween(Vec3i start, Vec3i end, Random random, int count, BlockRotation excludedRotation) {
    for (int i = 0; i < count; ++i) {
      // 4 attempts per building
      var attempts = 4;
      for (int j = 0; j < attempts; ++j) {
        var x = random.nextInt(end.getX() - start.getX() + 1) + start.getX();
        var z = random.nextInt(end.getZ() - start.getZ() + 1) + start.getZ();
        var pos = new Vec3i(x, start.getY(), z);
        if (tryPlaceBuilding(pos, random, excludedRotation)) {
          break;
        }
      }
    }
  }

  private boolean tryPlaceBuilding(Vec3i start, Random random, BlockRotation excludedRotation) {
    var rotations = BlockRotation.randomRotationOrder(random);

    var maxLength = 3 + random.nextInt(6);
    var result = false;

    for (var rotation : rotations) {
      if (rotation == excludedRotation) {
        continue;
      }

      Vec3i pos = new Vec3i(start.getX(), start.getY(), start.getZ());
      var valid = true;
      var direction = rotation.rotate(Direction.SOUTH).getVector();
      for (int i = 0; i < maxLength; ++i) {
        pos = pos.add(direction);
        if (isPositionUsed(pos) || pos.getX() <= 0 || pos.getZ() <= 0 || pos.getX() >= 63 || pos.getZ() >= 56) {
          valid = false;
          break;
        }
      }
      if (valid) {
        addBuilding(start.add(direction), maxLength - 2, rotation, random);
        result = true;
        break;
      }
    }

    return result;
  }

  private Pair<Vec3i, Vec3i> addPath(Vec3i start, BlockRotation rotation, int length, Random random, int skippedTurn) {
    var direction = rotation.rotate(Direction.SOUTH).getVector();
    var sideDirection = rotation.rotate(Direction.EAST).getVector();
    Vec3i skippedVec = null;
    var pos = start;

    var turns = 0;
    var nextLength = 3 + random.nextInt(2);
    var nextLeft = random.nextBoolean();
    addBuilding(pos.add(sideDirection).add(direction.multiply(2)), nextLength - 2, rotation, random);
    addBuilding(pos.subtract(sideDirection).add(direction.multiply(2)), nextLength - 2, rotation, random);

    for (int i = 0; i < length; ++i) {
      if (nextLength <= 0) {
        if (turns == skippedTurn) {
          skippedVec = pos;
        } else {
          if (nextLeft) {
            // Left turn
            addRoom(room(pos, PATH_TURN).rotation(rotation));
            pos = pos.add(sideDirection);
            addRoom(room(pos, PATH_TURN).rotation(rotation.rotate(BlockRotation.CLOCKWISE_180)));
          } else {
            // Right turn
            addRoom(room(pos, PATH_TURN).rotation(rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
            pos = pos.subtract(sideDirection);
            addRoom(room(pos, PATH_TURN).rotation(rotation.rotate(BlockRotation.CLOCKWISE_90)));
          }
        }
        nextLength = Math.min(2 + random.nextInt(3), length - i);

        var leftMultipler = Math.max(nextLeft ? 0 : 1, skippedTurn == turns ? 1 : 0);
        var rightMultipler = Math.max(nextLeft ? 1 : 0, skippedTurn == turns ? 1 : 0);
        addBuilding(pos.add(sideDirection).add(direction.multiply(leftMultipler)), nextLength, rotation, random);
        addBuilding(pos.subtract(sideDirection).add(direction.multiply(rightMultipler)), nextLength, rotation, random);

        turns++;
        nextLeft = random.nextBoolean();
      } else {
        addRoom(room(pos, PATH_STRAIGHT).rotation(rotation));
        if (random.nextFloat() < 0.33) {
          addRoom(room(pos, PATH_BUILDING).rotation(rotation).terrainFit());
        }
        nextLength--;
      }

      pos = pos.add(direction);
    }

    return new Pair<>(pos.subtract(direction), skippedVec);
  }

  private static Identifier pool(String path) {
    return MineCells.createId("promenade/" + path);
  }
}
