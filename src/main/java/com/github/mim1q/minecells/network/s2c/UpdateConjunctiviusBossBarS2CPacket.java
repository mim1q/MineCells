package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class UpdateConjunctiviusBossBarS2CPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("conjunctivius_boss_bar_update");

  public UpdateConjunctiviusBossBarS2CPacket(UUID uuid, int tentacleCount, int maxTentacleCount) {
    super(Unpooled.buffer());
    writeUuid(uuid);
    writeShort(tentacleCount);
    writeShort(maxTentacleCount);
  }

  public static void send(ServerPlayerEntity player, UUID uuid, int tentacleCount, int maxTentacleCount) {
    var packet = new UpdateConjunctiviusBossBarS2CPacket(uuid, tentacleCount, maxTentacleCount);
    ServerPlayNetworking.send(player, ID, packet);
  }
}
