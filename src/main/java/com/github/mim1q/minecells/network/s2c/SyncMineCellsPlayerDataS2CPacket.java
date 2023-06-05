package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.world.state.MineCellsData;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncMineCellsPlayerDataS2CPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("sync_minecells_data");

  public SyncMineCellsPlayerDataS2CPacket(MineCellsData.PlayerData data) {
    super(Unpooled.buffer());
    var nbt = new NbtCompound();
    data.writeNbt(nbt);
    writeNbt(nbt);
  }

  public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    var player = client.player;
    var nbt = buf.readNbt();
    if (player == null || nbt == null) {
      return;
    }
    client.execute(() -> {
      var data = new MineCellsData.PlayerData(nbt, null);
      ((PlayerEntityAccessor) player).setMineCellsData(data);
    });
  }

  public static void send(ServerPlayerEntity player, MineCellsData.PlayerData data) {
    var packet = new SyncMineCellsPlayerDataS2CPacket(data);
    ServerPlayNetworking.send(player, ID, packet);
  }
}
