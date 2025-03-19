package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.structure.grid.util.Vec3iCursor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PrisonGridGenerator extends MultipartGridGenerator {
  private static final Identifier SPAWN = id("spawn/spawn");
  private static final Identifier SPAWN_OUTSIDE_NEAR = id("spawn/outside_near");
  private static final Identifier SPAWN_OUTSIDE_FAR = id("spawn/outside_far");

  private static final Identifier STAIRS = id("stairs");
  private static final Identifier STRAIGHT = id("straight");
  private static final Identifier TURN = id("turn");

  private static final Identifier TERMINAL = id("terminal");

  public PrisonGridGenerator(int xPart, int zPart) {
    super(xPart, zPart);
  }

  @Override
  protected void addRooms(Random random) {
    var cursor = new Vec3iCursor(new Vec3i(32, 15, 32), Direction.SOUTH);

    var spawnCursor = cursor.split();
    addRoom(room(spawnCursor, SPAWN).offset(0, -2, 0).specialPoint(
      SpecialPointIds.ENTRANCE, new Vec3i(0, 0, 0), BlockRotation.NONE
    ));
    addRoom(room(spawnCursor.stepLeft(), SPAWN_OUTSIDE_NEAR).offset(0, -2, 0));
    addRoom(room(spawnCursor.stepLeft(), SPAWN_OUTSIDE_FAR).offset(0, -2, 0));

    addFloor(random, cursor, 4);
  }

  private Vec3iCursor addCorridor(Random random, Vec3iCursor cursor, boolean allowTurns, boolean hasEnd, int length) {
    for (int i = 0; i < length; ++i) {
      var turn = allowTurns && random.nextBoolean();
      var rotation = (turn ? BlockRotation.NONE : BlockRotation.CLOCKWISE_180).rotate(cursor.getRotation());
      addRoom(room(cursor.forward(), turn ? TURN : STRAIGHT)
        .rotation(rotation)
      );

      if (turn) {
        addCorridor(random, cursor.split().turn(rotation).turnLeft(), false, true, 5);
      }
    }
    if (hasEnd) {
      addRoom(room(cursor.forward(), TERMINAL).rotation(cursor.getRotation().rotate(BlockRotation.CLOCKWISE_180)));
    }

    return cursor.split();
  }

  private void addFloor(Random random, Vec3iCursor cursor, int index) {
    var endCursor = addCorridor(random, cursor, true, false, 10);
    if (index > 0) {
      var newCursor = endCursor.split();
      addRoom(room(newCursor.down(), STAIRS));
      addCorridor(random, newCursor, true, true, 3);
      addFloor(random, newCursor.turn(BlockRotation.CLOCKWISE_180), index - 1);
    }
    addCorridor(random, endCursor.forward(), true, true, 10);
  }

  @Override
  public int getVersion() {
    return 1;
  }

  private static Identifier id(String path) {
    return MineCells.createId("better_prison/" + path);
  }
}
