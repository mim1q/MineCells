package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.network.c2s.RequestSyncMineCellsPlayerDataC2SPacket;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.math.Vec3d;

public class ServerPacketHandler {
  public static void init() {
    ServerPlayNetworking.registerGlobalReceiver(RequestSyncMineCellsPlayerDataC2SPacket.ID, RequestSyncMineCellsPlayerDataC2SPacket::apply);

    ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.USE_TENTACLE, (server, player, handler, buf, responseSender) -> {
      var targetPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
      var playerItem = player.getMainHandStack();

      if (!playerItem.isOf(MineCellsItems.TENTACLE) || targetPos.squaredDistanceTo(player.getPos()) > 16.0 * 16.0) {
        MineCells.LOGGER.warn("Invalid tentacle weapon use packet from player {}", player.getName().getString());
        return;
      }

      server.execute(() -> {
        var tentacle = TentacleWeaponEntity.create(player.getWorld(), player, targetPos);
        player.getWorld().spawnEntity(tentacle);
        player.getItemCooldownManager().set(MineCellsItems.TENTACLE, 40);
      });
    });
  }
}
