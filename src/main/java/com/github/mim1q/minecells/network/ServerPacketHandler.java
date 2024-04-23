package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestSyncMineCellsPlayerDataC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.network.s2c.SendUnlockedCellCrafterRecipesS2CPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.math.Vec3d;

public class ServerPacketHandler {
  public static void init() {
    ServerPlayNetworking.registerGlobalReceiver(RequestSyncMineCellsPlayerDataC2SPacket.ID, RequestSyncMineCellsPlayerDataC2SPacket::apply);

    ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.USE_TENTACLE, (server, player, handler, buf, responseSender) -> {
      var targetPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
      var playerItem = player.getMainHandStack();

      var maxDistance = MineCells.COMMON_CONFIG.baseTentacleMaxDistance + 2.0;

      if (!playerItem.isOf(MineCellsItems.TENTACLE)
        || targetPos.squaredDistanceTo(player.getPos()) > maxDistance * maxDistance
      ) {
        MineCells.LOGGER.warn("Invalid tentacle weapon use packet from player {}", player.getName().getString());
        return;
      }

      server.execute(() -> {
        var tentacle = TentacleWeaponEntity.create(player.getWorld(), player, targetPos);
        player.getWorld().spawnEntity(tentacle);
        player.getItemCooldownManager().set(
          MineCellsItems.TENTACLE,
          MineCellsItems.TENTACLE.getBaseAbilityCooldown(playerItem)
        );
      });
    });

    ServerPlayNetworking.registerGlobalReceiver(RequestUnlockedCellCrafterRecipesC2SPacket.ID, ((server, player, handler, buf, responseSender) -> {
      responseSender.sendPacket(SendUnlockedCellCrafterRecipesS2CPacket.ID, new SendUnlockedCellCrafterRecipesS2CPacket(player));
    }));

    ServerPlayNetworking.registerGlobalReceiver(CellCrafterCraftRequestC2SPacket.ID, (server, player, handler, buf, responseSender) -> {
      var pos = buf.readBlockPos();
      var recipeId = buf.readIdentifier();
      server.execute(() -> {
        var blockEntity = player.getWorld().getBlockEntity(pos);
        var recipe = server.getRecipeManager().get(recipeId);
        if (
          blockEntity instanceof CellCrafterBlockEntity cellCrafter
          && recipe.isPresent()
          && recipe.get() instanceof CellForgeRecipe cellForgeRecipe
        ) {
          cellCrafter.setCooldown(40);
          cellCrafter.addStack(cellForgeRecipe.getOutput(server.getRegistryManager()));
        }
      });
    });
  }
}
