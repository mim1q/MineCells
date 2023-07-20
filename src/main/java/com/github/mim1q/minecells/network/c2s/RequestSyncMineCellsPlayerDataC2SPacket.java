package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.state.MineCellsData;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RequestSyncMineCellsPlayerDataC2SPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("request_sync_minecells_data");

  public RequestSyncMineCellsPlayerDataC2SPacket() {
    super(Unpooled.buffer());
  }

  public static void apply(
    MinecraftServer minecraftServer,
    ServerPlayerEntity serverPlayerEntity,
    ServerPlayNetworkHandler serverPlayNetworkHandler,
    PacketByteBuf packetByteBuf,
    PacketSender packetSender
  ) {
    minecraftServer.execute(() -> {
      MineCellsData.syncCurrentPlayerData(serverPlayerEntity, minecraftServer.getOverworld());
    });
  }

  public static void send() {
    var packet = new RequestSyncMineCellsPlayerDataC2SPacket();
    ClientPlayNetworking.send(ID, packet);
  }
}