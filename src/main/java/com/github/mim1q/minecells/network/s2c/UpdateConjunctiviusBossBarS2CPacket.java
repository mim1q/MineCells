package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record UpdateConjunctiviusBossBarS2CPacket(
  UUID uuid,
  int tentacleCount,
  int maxTentacleCount
)  {
}
