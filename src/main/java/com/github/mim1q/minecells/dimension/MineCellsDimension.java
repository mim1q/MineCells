package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MathUtils;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.Arrays;

public enum MineCellsDimension {
  OVERWORLD(new Identifier("overworld"), 0, 0, 0, 0.0),
  PRISONERS_QUARTERS(MineCells.createId("prison"), 8, 43, 5, 1024.0),
  INSUFFERABLE_CRYPT(MineCells.createId("insufferable_crypt"), 6, 41, 3, 1024.0),
  PROMENADE_OF_THE_CONDEMNED(MineCells.createId("promenade"), 6, 200, 6, 1024.0);

  public final RegistryKey<World> key;
  public final String translationKey;
  private final Vec3i spawnOffset;
  public final double borderSize;
  private final float pitch;
  private final float yaw;

  MineCellsDimension(Identifier id, int offsetX, int offsetY, int offsetZ, double borderSize, float pitch, float yaw) {
    this.key = RegistryKey.of(Registry.WORLD_KEY, id);
    this.translationKey = (id.toTranslationKey("dimension"));
    this.spawnOffset = new Vec3i(offsetX, offsetY, offsetZ);
    this.borderSize = borderSize;
    this.pitch = pitch;
    this.yaw = yaw;
  }

  MineCellsDimension(Identifier id, int offsetX, int offsetY, int offsetZ, double borderSize) {
    this(id, offsetX, offsetY, offsetZ, borderSize, 0, 0);
  }

  public Vec3d getTeleportPosition(BlockPos pos, ServerWorld destination) {
    var runCenter = new BlockPos(MathUtils.getClosestMultiplePosition(pos, 1024));
    var tpPos = runCenter.add(spawnOffset.getX(), spawnOffset.getY(), spawnOffset.getZ());
    var y = destination.getChunk(tpPos).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, tpPos.getX(), tpPos.getZ());
    if (y < tpPos.getY()) return Vec3d.ofCenter(tpPos.withY(y + 2));
    return Vec3d.ofCenter(tpPos);
  }

  public void teleportEntity(Entity entity, ServerWorld world) {
    var destination = getWorld(world);
    var target = new TeleportTarget(getTeleportPosition(entity.getBlockPos(), destination), Vec3d.ZERO, pitch, yaw);
    FabricDimensions.teleport(entity, destination, target);
  }

  public ServerWorld getWorld(ServerWorld world) {
    return world.getServer().getWorld(key);
  }

  public static MineCellsDimension getFrom(RegistryKey<World> key) {
    for (MineCellsDimension dimension : values()) {
      if (dimension.key.equals(key)) {
        return dimension;
      }
    }
    return null;
  }

  public static MineCellsDimension getFrom(World world) {
    return getFrom(world.getRegistryKey());
  }

  public static World getWorld(World world, RegistryKey<World> key) {
    MinecraftServer server = world.getServer();
    if (server == null) {
      return null;
    }
    return server.getWorld(key);
  }

  public static boolean isMineCellsDimension(World world) {
    if (world.getRegistryKey() == OVERWORLD.key) return false;
    return Arrays.stream(values()).anyMatch(dimension -> dimension.key == world.getRegistryKey());
  }

  public static Vec3d getTeleportPos(RegistryKey<World> dimension, BlockPos pos, ServerWorld world) {
    BlockPos runCenter = new BlockPos(MathUtils.getClosestMultiplePosition(pos, 1024));
//    if (dimension.equals(PRISON)) {
//      return new Vec3d(runCenter.getX() + 8, 43, runCenter.getZ() + 5.5);
//    }
//    if (dimension.equals(INSUFFERABLE_CRYPT)) {
//      return new Vec3d(runCenter.getX() + 6, 41, runCenter.getZ() + 3.5);
//    }
//    if (dimension.equals(PROMENADE)) {
//      runCenter = runCenter.add(6, 0, 6);
//      int y = world.getChunk(runCenter).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, runCenter.getX(), runCenter.getZ());
//      BlockPos groundPos = runCenter.withY(y);
//      if (groundPos.getY() == 0) return null;
//      BlockPos tpPos = MineCellsPortal.placeUpstreamPortal(world, groundPos.down());
//      return Vec3d.ofCenter(tpPos).add(3.5D, 0.0D, 3.5D);
//    }
    return null;
  }

  public static String getTranslationKey(RegistryKey<World> dimension) {
    Identifier id = dimension.getValue();
    return "dimension." + id.getNamespace() + "." + id.getPath();
  }

  public static String getTranslationKey(String key) {
    Identifier id = new Identifier(key);
    return "dimension." + id.getNamespace() + "." + id.getPath();
  }
}
