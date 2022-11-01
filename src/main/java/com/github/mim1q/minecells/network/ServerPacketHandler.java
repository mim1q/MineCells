package com.github.mim1q.minecells.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketHandler {
  public static void init() {
    ServerPlayNetworking.registerGlobalReceiver(RequestForgeC2SPacket.ID, RequestForgeC2SPacket::apply);
  }
}
