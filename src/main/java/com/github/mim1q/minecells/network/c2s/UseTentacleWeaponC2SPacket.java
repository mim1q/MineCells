package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.network.PacketIdentifiers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class UseTentacleWeaponC2SPacket extends PacketByteBuf {
  public UseTentacleWeaponC2SPacket(Vec3d targetPos) {
    super(Unpooled.buffer());
    this.writeDouble(targetPos.x);
    this.writeDouble(targetPos.y);
    this.writeDouble(targetPos.z);
  }

  public void send() {
    ClientPlayNetworking.send(PacketIdentifiers.USE_TENTACLE, this);
  }
}
