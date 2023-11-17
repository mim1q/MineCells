package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Set;

import static com.github.mim1q.minecells.effect.MineCellsEffectFlags.DISARMED;

public enum MineCellsDimension {
  OVERWORLD(new Identifier("overworld"), 0, 0, 0, 0.0),
  PRISONERS_QUARTERS(MineCells.createId("prison"), 2, 43, 3, 1024.0, -90F),
  INSUFFERABLE_CRYPT(MineCells.createId("insufferable_crypt"), 6, 41, 3, 1024.0),
  PROMENADE_OF_THE_CONDEMNED(MineCells.createId("promenade"), 6, -5, 6, 1024.0),
  RAMPARTS(MineCells.createId("ramparts"), -71, 263, -259, 384.0),
  BLACK_BRIDGE(MineCells.createId("black_bridge"), 6, 100, 6, 384.0);

  private static final Set<MineCellsDimension> DIMENSIONS_WITH_SURFACE = Set.of(
    PROMENADE_OF_THE_CONDEMNED
  );

  public final RegistryKey<World> key;
  private final Identifier id;
  public final String translationKey;
  public final Vec3i spawnOffset;
  public final double borderSize;
  public final float yaw;

  MineCellsDimension(Identifier id, int offsetX, int offsetY, int offsetZ, double borderSize, float yaw) {
    this.key = RegistryKey.of(RegistryKeys.WORLD, id);
    this.id = id;
    this.translationKey = (id.toTranslationKey("dimension"));
    this.spawnOffset = new Vec3i(offsetX, offsetY, offsetZ);
    this.borderSize = borderSize;
    this.yaw = yaw;
  }

  MineCellsDimension(Identifier id, int offsetX, int offsetY, int offsetZ, double borderSize) {
    this(id, offsetX, offsetY, offsetZ, borderSize, 0F);
  }

  public Vec3d getTeleportPosition(BlockPos pos, ServerWorld world) {
    var destination = getWorld(world);
    var runCenter = new BlockPos(MathUtils.getClosestMultiplePosition(pos, 1024));
    var tpPos = runCenter.add(spawnOffset.getX(), spawnOffset.getY(), spawnOffset.getZ());
    if (DIMENSIONS_WITH_SURFACE.contains(this)) {
      var y = destination.getChunk(tpPos).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, tpPos.getX(), tpPos.getZ());
      return Vec3d.ofCenter(tpPos).add(0.0, y, 0.0);
    }
    return Vec3d.ofCenter(tpPos);
  }

  public void teleportPlayer(ServerPlayerEntity player, ServerWorld world, BlockPos posOverride) {
    var destination = getWorld(world);
    Vec3d teleportPos;
    if (this == OVERWORLD) {
      if (player.getSpawnPointDimension() == OVERWORLD.key && player.getSpawnPointPosition() != null) {
        teleportPos = Vec3d.ofCenter(player.getSpawnPointPosition());
      } else {
        teleportPos = Vec3d.ofCenter(world.getSpawnPos());
      }
    } else {
      teleportPos = getTeleportPosition(posOverride == null ? player.getBlockPos() : posOverride, world);
    }
    world.getServer().execute(() -> {
      player.teleport(destination, teleportPos.getX(), teleportPos.getY(), teleportPos.getZ(), yaw, 0F);
    });
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
      case INSUFFERABLE_CRYPT -> player != null && ((LivingEntityAccessor)player).getMineCellsFlag(DISARMED);
      default -> true;
    };
  }
}
