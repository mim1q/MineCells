package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.structure.grid.util.Vec3iCursor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class PrisonGridGenerator extends MultipartGridGenerator {
  private static final Identifier SPAWN = id("spawn/spawn");
  private static final Identifier SPAWN_OUTSIDE_NEAR = id("spawn/outside_near");
  private static final Identifier SPAWN_OUTSIDE_FAR = id("spawn/outside_far");

  private static final Identifier STAIRS = id("stairs");
  private static final Identifier STRAIGHT = id("straight");
  private static final Identifier TURN = id("turn");
  private static final Identifier CURVE = id("curve");

  private static final Identifier TERMINAL = id("terminal");
  private static final Identifier PROMENADE = id("promenade");

  public PrisonGridGenerator(int xPart, int zPart) {
    super(xPart, zPart);
  }

  @Override
  protected void addRooms(Random random) {
    var cursor = new Vec3iCursor(new Vec3i(32, 15, 32), Direction.SOUTH);

    var spawnCursor = cursor.split();
    addRoom(room(spawnCursor, SPAWN).offset(0, -2, 0).specialPoint(
      SpecialPointIds.ENTRANCE, new Vec3i(12, 1, 1), BlockRotation.NONE
    ));
    addRoom(room(spawnCursor.stepLeft(), SPAWN_OUTSIDE_NEAR).offset(0, -2, 0));
    addRoom(room(spawnCursor.stepLeft(), SPAWN_OUTSIDE_FAR).offset(0, -2, 0));

    addFloor(random, cursor, 3);
  }

  private Vec3iCursor addCorridor(Random random, Vec3iCursor cursor, boolean allowTurns, @Nullable Identifier end, int length) {
    for (int i = 0; i < length; ++i) {
      if (random.nextFloat() < 0.2f && allowTurns && i < length - 1 && i > 0) {
        var left = random.nextBoolean();
        if (left) {
          addRoom(room(cursor.forward(), CURVE).rotation(cursor.getRotation().rotate(BlockRotation.COUNTERCLOCKWISE_90)));
          for (var j = 0; j < random.nextInt(3); ++j) {
            addRoom(room(cursor.stepLeft(), STRAIGHT).rotation(cursor.getRotation().rotate(BlockRotation.CLOCKWISE_90)));
          }
          addRoom(room(cursor.stepLeft(), CURVE).rotation(cursor.getRotation().rotate(BlockRotation.CLOCKWISE_90)));
        } else {
          addRoom(room(cursor.forward(), CURVE).rotation(cursor.getRotation().rotate(BlockRotation.CLOCKWISE_180)));
          for (var j = 0; j < random.nextInt(3); ++j) {
            addRoom(room(cursor.stepRight(), STRAIGHT).rotation(cursor.getRotation().rotate(BlockRotation.COUNTERCLOCKWISE_90)));
          }
          addRoom(room(cursor.stepRight(), CURVE).rotation(cursor.getRotation().rotate(BlockRotation.NONE)));
        }
        continue;
      }

      var turn = allowTurns && random.nextBoolean();
      var rotation = (random.nextBoolean() ? BlockRotation.NONE : BlockRotation.CLOCKWISE_180);
      addRoom(room(cursor.forward(), turn ? TURN : STRAIGHT)
        .rotation(rotation.rotate(cursor.getRotation()))
      );

      if (turn) {
        addCorridor(random, cursor.split().turn(rotation).turnRight(), false, TERMINAL, random.nextBetween(2, 4));
      }
    }
    if (end != null) {
      var endRoom = room(cursor.forward(), end).rotation(cursor.getRotation().rotate(BlockRotation.CLOCKWISE_180));
      if (end == PROMENADE) {
        endRoom.specialPoint(SpecialPointIds.EXIT, new Vec3i(9, 0, 14), BlockRotation.CLOCKWISE_180);
      }
      addRoom(endRoom);
    }

    return cursor.split();
  }

  private void addFloor(Random random, Vec3iCursor cursor, int index) {
    var endCursor = addCorridor(random, cursor, true, null, random.nextBetween(3, 5));
    endCursor.forward();
    if (index > 0) {
      var newCursor = endCursor.split();
      addRoom(room(newCursor.down(), STAIRS));
      addCorridor(random, newCursor.split(), true, TERMINAL, random.nextBetween(0, 2));
      addFloor(random, newCursor.turn(BlockRotation.CLOCKWISE_180), index - 1);
      addCorridor(random, endCursor, true, TERMINAL, random.nextBetween(0, 2));
    } else {
      addCorridor(random, endCursor.back(), true, PROMENADE, random.nextBetween(0, 2));
    }
  }

  @Override
  public int getVersion() {
    return 1;
  }

  private static Identifier id(String path) {
    return MineCells.createId("better_prison/" + path);
  }
}
