package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.HashMap;
import java.util.function.BiPredicate;

public class MineCellsDimensionGraph {
  private final HashMap<MineCellsDimension, Node> dimensionGraph = new HashMap<>();
  private Node add(MineCellsDimension dimension, Node ...upstreamNodes) {
    var node = new Node(dimension, upstreamNodes);
    dimensionGraph.put(dimension, node);
    return node;
  }

  public MineCellsDimensionGraph() {
    var overworld = add(MineCellsDimension.OVERWORLD);
    var prisonersQuarters = add(MineCellsDimension.PRISONERS_QUARTERS,
      overworld
    );
    var promenadeOfTheCondemned = add(MineCellsDimension.PROMENADE_OF_THE_CONDEMNED,
      prisonersQuarters
    );
    var insufferableCrypt = add(MineCellsDimension.INSUFFERABLE_CRYPT,
      prisonersQuarters
    );
  }

  public boolean canTraverseToOverworld(MineCellsDimension dimension, BiPredicate<MineCellsDimension, MineCellsDimension> predicate) {
    var node = dimensionGraph.get(dimension);
    return node != null && node.canTraverseToOverworld(predicate);
  }

  private record Node(MineCellsDimension dimension, Node ...upstreamNodes) {
    public boolean canTraverseToOverworld(BiPredicate<MineCellsDimension, MineCellsDimension> predicate) {
      if (dimension == MineCellsDimension.OVERWORLD) return true;
      for (var upstream : upstreamNodes) {
        if (predicate.test(dimension, upstream.dimension) && upstream.canTraverseToOverworld(predicate)) {
          return true;
        }
      }
      return false;
    }
  }

  public void saveStuckPlayer(ServerPlayerEntity player) {
    var stuck = !canTraverseToOverworld(
      MineCellsDimension.of(player.getWorld()), (from, to) -> ((PlayerEntityAccessor) player).getCurrentMineCellsPlayerData().getPortalData(from, to).isPresent()
    );
    if (stuck) {
      player.server.execute(() -> {
        player.sendMessage(Text.literal("uh oh, you got stuck in Mine Cells!"));
        var spawnDimension = player.getSpawnPointDimension();
        var spawnPos = player.getSpawnPointPosition();
        if (spawnDimension == null || spawnPos == null) {
          spawnDimension = MineCellsDimension.OVERWORLD.key;
          spawnPos = player.getWorld().getSpawnPos();
        }
        FabricDimensions.teleport(
          player,
          player.server.getWorld(spawnDimension),
          new TeleportTarget(Vec3d.of(spawnPos), Vec3d.ZERO, 0F, 0F)
        );
      });
    }
  }
}
