package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
    var ramparts = add(MineCellsDimension.RAMPARTS,
      promenadeOfTheCondemned
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
    if (!MineCellsDimension.isMineCellsDimension(player.getWorld())) {
      return;
    }
    var stuck = !canTraverseToOverworld(
      MineCellsDimension.of(player.getWorld()), (from, to) -> ((PlayerEntityAccessor) player).getCurrentMineCellsPlayerData().getPortalData(from, to).isPresent()
    );
    if (stuck) {
      player.server.execute(() -> {
        player.sendMessage(Text.translatable("chat.minecells.stuck_message"));
        var spawnDimension = player.getSpawnPointDimension();
        var spawnPos = player.getSpawnPointPosition();
        if (spawnDimension == null || spawnPos == null) {
          spawnDimension = MineCellsDimension.OVERWORLD.key;
          spawnPos = player.getWorld().getSpawnPos();
        }
        player.teleport(player.server.getWorld(spawnDimension), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
      });
    }
  }
}
