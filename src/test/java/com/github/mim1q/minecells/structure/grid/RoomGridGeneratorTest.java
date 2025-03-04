package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.structure.grid.generator.*;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.random.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoomGridGeneratorTest {
  private static final int TEST_COUNT = 20_000;

  @BeforeAll
  public static void setup() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();
  }


  @Test
  public void testPrisonGenerator() {
    testRoomGridGenerator(new PrisonGridGenerator(0, 0));
  }

  @Test
  public void testBetterPromenadeGenerator() {
    testRoomGridGenerator(new BetterPromenadeGridGenerator(0, 0));
  }

  @Test
  public void testRampartsGenerator() {
    testRoomGridGenerator(new RampartsGridGenerator(0, 0));
  }

  @Test
  public void testBlackBridgeGenerator() {
    testRoomGridGenerator(new BlackBridgeGridGenerator(0, 0));
  }

  @Test
  public void testInsufferableCryptGenerator() {
    testRoomGridGenerator(new InsufferableCryptGridGenerator(0, 0));
  }

  private static void testRoomGridGenerator(GridPiecesGenerator.RoomGridGenerator generator) {
    var random = Random.create();
    var generatorName = generator.getClass().getSimpleName();

    for (int i = 0; i < TEST_COUNT; ++i) {
      random.setSeed(new java.util.Random().nextInt(Integer.MAX_VALUE));
      generator.generateForTesting(random);
      var rooms = generator.usedPositions;
      for (var room : rooms) {
        assertTrue(
          (room.getX() >= 0 && room.getX() < 64 && room.getZ() >= 0 && room.getZ() < 64),
          "Invalid room placement in generator: " + generatorName + ", at: " + room
        );
      }
    }
  }
}
