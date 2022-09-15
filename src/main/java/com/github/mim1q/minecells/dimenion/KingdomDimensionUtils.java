package com.github.mim1q.minecells.dimenion;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
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
  public static final Identifier PORTAL_STRUCTURE = MineCells.createId("plains_portal");

  public static void teleportPlayer(ServerPlayerEntity player, ServerWorld currentWorld, KingdomPortalCoreBlockEntity portal) {
    ServerWorld newWorld = isKingdom(currentWorld) ? getOverworld(currentWorld) : getKingdom(currentWorld);
    TeleportTarget target = portal.getTeleportTarget();
    if (target == null) {
      return;
    }
    TeleportTarget movedTarget = new TeleportTarget(
      target.position.add(5.0D, 0.0D, 0.0D),
      Vec3d.ZERO,
      0.0F,
      0.0F
    );
    FabricDimensions.teleport(player, newWorld, movedTarget);
  }

  @Nullable
  public static TeleportTarget findTeleportTarget(BlockPos position, ServerWorld world) {
    if (world == null) return null;
    var pos = world.locateBiome(
      biome -> biome.getKey().get().getValue().equals(MineCells.createId("promenade")),
      position,
      1000,
      32,
      32
    );


    if (pos == null) {
      return null;
    }
    return new TeleportTarget(Vec3d.ofCenter(pos.getFirst()), Vec3d.ZERO, 0, 0);
  }

  public static void spawnPortal(ServerWorld world, BlockPos position) {
    var template = world.getStructureTemplateManager().getTemplate(PORTAL_STRUCTURE);
    BlockPos centeredPosition = position.add(-4, -1, -2);
    StructurePlacementData data = new StructurePlacementData();
    template.ifPresent(structureTemplate ->
      structureTemplate.place(world, centeredPosition, centeredPosition, data, world.getRandom(), 2)
    );
    System.out.println(template);
  }

  public static boolean isKingdom(World world) {
    return world.getRegistryKey() == KINGDOM;
  }
  public static ServerWorld getKingdom(ServerWorld world) {
    return world.getServer().getWorld(KINGDOM);
  }
  public static ServerWorld getOverworld(ServerWorld world) { return world.getServer().getWorld(World.OVERWORLD); }
}
