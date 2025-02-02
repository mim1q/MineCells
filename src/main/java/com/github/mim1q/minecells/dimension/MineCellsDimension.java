package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator.SpecialPoint;
import com.github.mim1q.minecells.structure.grid.generator.BetterPromenadeGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PrisonGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.RampartsGridGenerator;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.TeleportUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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

import static com.github.mim1q.minecells.effect.MineCellsEffectFlags.DISARMED;

public enum MineCellsDimension {
  OVERWORLD(new Identifier("overworld"), 0.0, null),
  PRISONERS_QUARTERS(MineCells.createId("prison"), 1024.0, new PrisonGridGenerator()),
  INSUFFERABLE_CRYPT(MineCells.createId("insufferable_crypt"), 1024.0, null),
  PROMENADE_OF_THE_CONDEMNED(MineCells.createId("promenade"), 1024.0, new BetterPromenadeGridGenerator(0, 0)),
  RAMPARTS(MineCells.createId("ramparts"), -384.0, new RampartsGridGenerator(0, 0)),
  BLACK_BRIDGE(MineCells.createId("black_bridge"), 384.0, null);

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

  public Pair<Vec3d, Integer> getTeleportPosition(BlockPos pos, ServerWorld world, Identifier specialPoint) {
    return getTeleportPosition(pos, world, specialPoint, true);
  }

    public Pair<Vec3d, Integer> getTeleportPosition(BlockPos pos, ServerWorld world, Identifier specialPoint, boolean applySafeOffset) {
    var destination = getWorld(world);
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
    var destination = getWorld(world);
    Pair<Vec3d, Integer> teleportPos;
    if (this == OVERWORLD) {
      if (player.getSpawnPointDimension() == OVERWORLD.key && player.getSpawnPointPosition() != null) {
        teleportPos = new Pair<>(Vec3d.ofCenter(player.getSpawnPointPosition()), (int) player.getSpawnAngle());
      } else {
        var dimension = MineCellsDimension.of(world);
        if (dimension != null) {
          teleportPos = dimension.getTeleportPosition(player.getBlockPos(), world, specialPoint);
        } else {
          teleportPos = new Pair<>(Vec3d.ofBottomCenter(world.getSpawnPos()), 0);
        }
      }
    } else {
      teleportPos = getTeleportPosition(posOverride == null ? player.getBlockPos() : posOverride, world, specialPoint);
    }
    TeleportUtils.teleportToDimension(player, destination, teleportPos.getLeft(), teleportPos.getRight());
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

  public boolean canMusicStart(ClientPlayerEntity player) {
    return switch (this) {
      case INSUFFERABLE_CRYPT -> player != null && ((LivingEntityAccessor) player).getMineCellsFlag(DISARMED);
      case BLACK_BRIDGE -> player != null && MinecraftClient.getInstance().inGameHud.getBossBarHud() != null;
      default -> true;
    };
  }
}
