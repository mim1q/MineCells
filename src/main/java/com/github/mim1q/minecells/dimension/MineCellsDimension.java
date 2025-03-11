package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.cc.MineCellsLevelCC;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator.SpecialPoint;
import com.github.mim1q.minecells.structure.grid.generator.*;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.TeleportUtils;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public enum MineCellsDimension {
  OVERWORLD(new Identifier("overworld"), 0.0, null),
  PRISONERS_QUARTERS(MineCells.createId("prison"), 1024.0, new PrisonGridGenerator(0, 0)),
  INSUFFERABLE_CRYPT(MineCells.createId("insufferable_crypt"), 1024.0, new InsufferableCryptGridGenerator(0, 0)),
  PROMENADE_OF_THE_CONDEMNED(MineCells.createId("promenade"), 1024.0, new BetterPromenadeGridGenerator(0, 0)),
  RAMPARTS(MineCells.createId("ramparts"), -384.0, new RampartsGridGenerator(0, 0)),
  BLACK_BRIDGE(MineCells.createId("black_bridge"), 384.0, new BlackBridgeGridGenerator(0, 0));

  public final RegistryKey<World> key;
  private final Identifier id;
  public final String translationKey;
  public final double borderSize;
  public final GridPiecesGenerator.RoomGridGenerator baseGenerator;

  MineCellsDimension(Identifier id, double borderSize, GridPiecesGenerator.RoomGridGenerator baseGenerator) {
    this.key = RegistryKey.of(RegistryKeys.WORLD, id);
    this.id = id;
    this.translationKey = (id.toTranslationKey("dimension"));
    this.borderSize = borderSize;
    this.baseGenerator = baseGenerator;
  }

  public Pair<Vec3d, Integer> getNonOverworldTeleportPosition(BlockPos pos, ServerWorld world, Identifier specialPoint) {
    return getNonOverworldTeleportPosition(pos, world, specialPoint, true);
  }

  public Pair<Vec3d, Integer> getNonOverworldTeleportPosition(BlockPos pos, ServerWorld destination, Identifier specialPoint, boolean applySafeOffset) {
    Optional<SpecialPoint> point = Optional.empty();
    try {
      point = GridBasedStructureUtils.getSpecialPoint(destination, pos, specialPoint);
    } catch (Exception e) {
      MineCells.LOGGER.error("Failed to get entrance point", e);
    }
    var runCenter = new BlockPos(MathUtils.getClosestMultiplePosition(pos, 1024));

    if (point.isPresent()) {
      var tpPos = Vec3d.ofBottomCenter(runCenter.add((point.get().offset())));
      if (applySafeOffset) {
        tpPos = tpPos.add(Vec3d.of(point.get().facing().rotate(Direction.NORTH).getVector()).multiply(-0.5));
      }
      return new Pair<>(
        tpPos,
        point.get().facing().rotate(0, 360)
      );
    }

    var spawnOffset = getOffset();
    var tpPos = runCenter.add(spawnOffset.getLeft());
    return new Pair<>(Vec3d.ofBottomCenter(tpPos), spawnOffset.getRight().intValue());
  }

  public void teleportPlayer(ServerPlayerEntity player, ServerWorld world, @Nullable BlockPos posOverride, Identifier specialPoint) {
    var teleportPos = getNonOverworldTeleportPosition(player, posOverride, world, specialPoint);
    var destination = getWorld(world);
    TeleportUtils.teleportToDimension(player, destination, teleportPos.getLeft(), teleportPos.getRight());
  }

  private Pair<Vec3d, Integer> getNonOverworldTeleportPosition(ServerPlayerEntity player, BlockPos pos, ServerWorld world, Identifier specialPoint) {
    var destination = getWorld(world);
    if (this == OVERWORLD) {
      var data = MineCellsLevelCC.OverworldEntriesCC.getPlayerEntrancePoint(player, pos, world);
      if (data.isPresent()) {
        return new Pair<>(Vec3d.ofCenter(data.get().entrancePos()), (int) data.get().entranceRotation());
      }
      if (player.getSpawnPointDimension() == OVERWORLD.key && player.getSpawnPointPosition() != null) {
        return new Pair<>(Vec3d.ofCenter(player.getSpawnPointPosition()), (int) player.getSpawnAngle());
      }

      return new Pair<>(Vec3d.ofBottomCenter(destination.getSpawnPos()), 0);
    }

    return getNonOverworldTeleportPosition(pos, destination, specialPoint);
  }

  private Pair<Vec3i, Float> getOffset() {
    return new Pair<>(Vec3i.ZERO, 0f);
  }

  public ServerWorld getWorld(ServerWorld world) {
    return world.getServer().getWorld(key);
  }

  public static MineCellsDimension of(RegistryKey<World> key) {
    for (MineCellsDimension dimension : values()) {
      if (dimension.key.equals(key)) {
        return dimension;
      }
    }
    return null;
  }

  public static MineCellsDimension of(World world) {
    return of(world.getRegistryKey());
  }

  public static World getWorld(World world, RegistryKey<World> key) {
    MinecraftServer server = world.getServer();
    if (server == null) {
      return null;
    }
    return server.getWorld(key);
  }

  public static boolean isMineCellsDimension(World world) {
    return world != null && world.getRegistryKey().getValue().getNamespace().equals("minecells");
  }

  public static String getTranslationKey(RegistryKey<World> dimension) {
    Identifier id = dimension.getValue();
    return "dimension." + id.getNamespace() + "." + id.getPath();
  }

  public static String getTranslationKey(String key) {
    Identifier id = new Identifier(key);
    return "dimension." + id.getNamespace() + "." + id.getPath();
  }

  public static MineCellsDimension of(Identifier id) {
    return Arrays.stream(values()).filter(value -> value.id.equals(id)).findFirst().orElse(null);
  }

  public static Double getFallResetHeight(World world) {
    if (!isMineCellsDimension(world)) return null;
    return switch (of(world)) {
      case RAMPARTS -> 180.0;
      default -> null;
    };
  }

  public int getDimensionLevel() {
    return switch (this) {
      case PROMENADE_OF_THE_CONDEMNED -> 1;
      case RAMPARTS -> 2;
      default -> 0;
    };
  }

  public MusicSound getMusic() {
    return switch (this) {
      case PRISONERS_QUARTERS -> MineCellsSounds.PRISONERS_QUARTERS;
      case PROMENADE_OF_THE_CONDEMNED -> MineCellsSounds.PROMENADE;
      case RAMPARTS -> MineCellsSounds.RAMPARTS;
      case INSUFFERABLE_CRYPT -> MineCellsSounds.INSUFFERABLE_CRYPT;
      default -> null;
    };
  }

  public int getColor() {
    return switch (this) {
      case OVERWORLD -> 0x8EF96D;
      case PRISONERS_QUARTERS -> 0x54EF88;
      case PROMENADE_OF_THE_CONDEMNED -> 0x93FFF7;
      case INSUFFERABLE_CRYPT -> 0xFF4CF4;
      case RAMPARTS -> 0xFFC540;
      case BLACK_BRIDGE -> 0x623cc9;
    };
  }

  public static int getColor(@Nullable World world, int defaultColor) {
    if (world == null) return defaultColor;
    var dim = of(world);
    if (dim == null) return defaultColor;
    return dim.getColor();
  }

//  public boolean canMusicStart(ClientPlayerEntity player) {
//    return switch (this) {
//      case INSUFFERABLE_CRYPT -> player != null && ((LivingEntityAccessor) player).getMineCellsFlag(DISARMED);
//      case BLACK_BRIDGE -> player != null && MinecraftClient.getInstance().inGameHud.getBossBarHud() != null;
//      default -> true;
//    };
//  }
}
