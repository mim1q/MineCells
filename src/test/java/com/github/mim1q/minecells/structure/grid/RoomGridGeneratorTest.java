package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.structure.grid.generator.*;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.random.Random;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoomGridGeneratorTest {
  private static final int TEST_COUNT = 20_000;

  @Test
  public void testAllGenerators() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();

    var generators = List.of(
      new PrisonGridGenerator(0, 0),
      new BetterPromenadeGridGenerator(0, 0),
      new RampartsGridGenerator(0, 0),
      new BlackBridgeGridGenerator(0, 0),
      new InsufferableCryptGridGenerator(0, 0)
    );

    generators.forEach(RoomGridGeneratorTest::testRoomGridGenerator);
  }

  private static void testRoomGridGenerator(GridPiecesGenerator.RoomGridGenerator generator) {
    var random = Random.create();
    var generatorName = generator.getClass().getSimpleName();

    for (int i = 0; i < TEST_COUNT; ++i) {
      random.setSeed(i);
      generator.generateForTesting(random);
      var rooms = generator.usedPositions;
      for (var room : rooms) {
        assertTrue(
          (room.getX() >= 0 && room.getX() < 64 && room.getZ() >= 0 && room.getZ() < 64),
          "Invalid room placement in generator: " + generatorName
        );
      }
    }

    System.out.println("Successfully tested " + generatorName + " for invalid room placement");
  }
}
