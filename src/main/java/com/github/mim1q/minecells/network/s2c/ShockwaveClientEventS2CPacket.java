package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ShockwaveBlock;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ShockwaveClientEventS2CPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("shockwave_client_event");

  public ShockwaveClientEventS2CPacket(Block block, BlockPos postiion, boolean end) {
    super(Unpooled.buffer());
    writeInt(Registries.BLOCK.getRawId(block));
    writeBlockPos(postiion);
    writeBoolean(end);
  }

  public static void send(ServerPlayerEntity player, Block block, BlockPos pos, boolean end) {
    var packet = new ShockwaveClientEventS2CPacket(block, pos, end);
    ServerPlayNetworking.send(player, ID, packet);
  }
}
