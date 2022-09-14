package com.github.mim1q.minecells.dimenion;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KingdomDimensionUtils {
  public static final RegistryKey<World> KINGDOM = RegistryKey.of(Registry.WORLD_KEY, MineCells.createId("kingdom"));

  public static void teleportPlayer(ServerPlayerEntity player, ServerWorld currentWorld) {
    ServerWorld newWorld = isKingdom(currentWorld) ? getOverworld(currentWorld) : getKingdom(currentWorld);
    TeleportTarget target = findTeleportTarget(player.getPos(), newWorld);
    if (target == null) {
      return;
    }
    FabricDimensions.teleport(player, newWorld, target);
  }

  @Nullable
  public static TeleportTarget findTeleportTarget(Vec3d position, ServerWorld world) {
    if (world == null) return null;
    var tag = TagKey.of(Registry.STRUCTURE_KEY, MineCells.createId("kingdom_portal"));

    var pos = world.locateBiome(
      biome -> {
        System.out.println(biome.getKey());
        return (biome.getKey().get().getValue().equals(MineCells.createId("promenade")))
          || (biome.getKey().get().getValue().equals(new Identifier("plains")));
      },
      new BlockPos(position),
      1000,
      32,
      32
    );

    if (pos == null) {
      return null;
    }
    return new TeleportTarget(Vec3d.ofCenter(pos.getFirst()), Vec3d.ZERO, 0, 0);
  }

  public static boolean isKingdom(World world) {
    return world.getRegistryKey() == KINGDOM;
  }
  public static ServerWorld getKingdom(ServerWorld world) {
    return world.getServer().getWorld(KINGDOM);
  }
  public static ServerWorld getOverworld(ServerWorld world) { return world.getServer().getWorld(World.OVERWORLD); }
}
