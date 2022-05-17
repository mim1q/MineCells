package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.registry.ParticleRegistry;
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

import java.util.Random;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.CRIT, ClientPacketHandler::handleCrit);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.EXPLOSION, ClientPacketHandler::handleExplosion);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.ELEVATOR_MOVE, ClientPacketHandler::handleElevatorMove);
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

    private static void handleElevatorMove(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean up = buf.readBoolean();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        Vec3d posW = new Vec3d(x - 1.0D, y + 0.25D, z);
        Vec3d posE = new Vec3d(x + 1.0D, y + 0.25D, z);
        client.execute(() -> {
            if(client.world != null && client.player != null) {
                for (int i = 0; i < 5; i++) {
                    Random rng = client.player.getRandom();
                    double rx0 =  (rng.nextDouble() - 0.5D) * 0.5D;
                    double rz0 =  (rng.nextDouble() - 0.5D) * 0.5D;
                    double rx1 =  (rng.nextDouble() - 0.5D) * 0.5D;
                    double rz1 =  (rng.nextDouble() - 0.5D) * 0.5D;
                    ParticleHelper.addParticle(client.world, ParticleTypes.ELECTRIC_SPARK, posW, new Vec3d(rx0 * 0.2, up ? -2.0D : 1.0D, rz0));
                    ParticleHelper.addParticle(client.world, ParticleTypes.ELECTRIC_SPARK, posE, new Vec3d(rx1, up ? -2.0D : 1.0D, rz1));
                }
            }
        });
    }
}
