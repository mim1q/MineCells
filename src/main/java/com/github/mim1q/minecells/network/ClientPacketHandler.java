package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.util.MineCellsMathHelper;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.CRIT, ClientPacketHandler::handleCrit);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.EXPLOSION, ClientPacketHandler::handleExplosion);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.CONNECT, ClientPacketHandler::handleConnect);
    }

    private static void handleCrit(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        client.execute(() -> {
            if (client.player != null) {
                ParticleHelper.addAura(client.world, pos, ParticleTypes.CRIT, 8, 0.0D, 1.0D);
            }
        });
    }

    private static void handleExplosion(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        double radius = buf.readDouble();
        client.execute(() -> {
            if (client.player != null && client.world != null) {
                ParticleHelper.addParticle(client.world, ParticleRegistry.EXPLOSION, pos, new Vec3d(radius, 0.0D, 0.0D));
                ParticleHelper.addAura(client.world, pos, ParticleTypes.CRIT, 20, 0.0D, radius);
            }
        });
    }

    private static void handleConnect(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Vec3d pos0 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3d pos1 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        client.execute(() -> {
            if (client.player != null && client.world != null) {
                double amount = pos0.distanceTo(pos1);
                Vec3d vel = pos1.subtract(pos0).normalize();
                for (int i = 0; i < amount; i++) {
                    Vec3d pos = MineCellsMathHelper.lerp(pos0, pos1, i / (float)amount);
                    ParticleHelper.addParticle(client.world, ParticleTypes.ENCHANTED_HIT, pos, vel);
                }
            }
        });
    }
}
