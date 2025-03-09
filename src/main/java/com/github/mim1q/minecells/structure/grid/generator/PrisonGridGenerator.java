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

    for (int i = 0; i < 20; ++i) {
      addRoom(room(cursor.forward(), random.nextBoolean() ? STRAIGHT : TURN)
        .rotation(random.nextBoolean() ? BlockRotation.NONE : BlockRotation.CLOCKWISE_180)
      );
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
