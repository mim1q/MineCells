package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.network.c2s.RequestForgeC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestSyncMineCellsPlayerDataC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketHandler {
  public static void init() {
    ServerPlayNetworking.registerGlobalReceiver(RequestForgeC2SPacket.ID, RequestForgeC2SPacket::apply);
    ServerPlayNetworking.registerGlobalReceiver(RequestSyncMineCellsPlayerDataC2SPacket.ID, RequestSyncMineCellsPlayerDataC2SPacket::apply);
  }
}
