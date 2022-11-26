package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.ParticleUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class SpawnRuneParticlesS2CPacket extends PacketByteBuf {

  public static final Identifier ID = MineCells.createId("spawn_rune_particles");

  public SpawnRuneParticlesS2CPacket(Box box) {
    super(Unpooled.buffer());
    writeDouble(box.minX);
    writeDouble(box.minY);
    writeDouble(box.minZ);
    writeDouble(box.maxX);
    writeDouble(box.maxY);
    writeDouble(box.maxZ);
  }

  public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    double minX = buf.readDouble();
    double minY = buf.readDouble();
    double minZ = buf.readDouble();
    double maxX = buf.readDouble();
    double maxY = buf.readDouble();
    double maxZ = buf.readDouble();
    Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
    client.execute(() -> {
      ClientWorld world = handler.getWorld();
      ParticleUtils.addInBox(world, ParticleTypes.CLOUD, box, 10, new Vec3d(0.01D, 0.01D, 0.01D));
    });
  }
}
