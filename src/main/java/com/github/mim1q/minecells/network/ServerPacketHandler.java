package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.cc.MineCellsLevelCC;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.network.c2s.UpdateDoorwayC2SPacket;
import com.github.mim1q.minecells.network.s2c.SendUnlockedCellCrafterRecipesS2CPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.math.Vec3d;

import static com.github.mim1q.minecells.world.processor.SwitchBlockStructureProcessor.copyAllProperties;

public class ServerPacketHandler {
  public static OwoNetChannel CHANNEL = OwoNetChannel.create(MineCells.createId("main"));

  public static void init() {
    CHANNEL.registerServerbound(UpdateDoorwayC2SPacket.class, (msg, ctx) -> {
      var world = ctx.player().getWorld();
      var entity = world.getBlockEntity(msg.doorwayPos());
      if (entity instanceof DoorwayPortalBlockEntity doorway) {
        var state = world.getBlockState(msg.doorwayPos());

        var newState = MineCellsBlocks.DOORWAY_PORTALS.get(msg.dimensionId()).getDefaultState();
        world.setBlockState(msg.doorwayPos(), copyAllProperties(state, newState));
        var pos = MineCellsLevelCC.PortalsCC.getOrCreatePortal(ctx.player()).runCenter();
        doorway.update(ctx.player(), pos, msg.onlyOwnerCanUse());
      }
    });


    ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.USE_TENTACLE, (server, player, handler, buf, responseSender) -> {
      var targetPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
      var playerItem = player.getMainHandStack();

      var maxDistance = MineCells.COMMON_CONFIG.baseTentacleMaxDistance() + 2.0;

      if (!playerItem.isOf(MineCellsItems.TENTACLE)
        || targetPos.squaredDistanceTo(player.getPos()) > maxDistance * maxDistance
      ) {
        MineCells.LOGGER.warn("Invalid tentacle weapon use packet from player {}", player.getName().getString());
        return;
      }

      server.execute(() -> {
        var tentacle = TentacleWeaponEntity.create(player.getWorld(), player, targetPos, playerItem);
        player.getWorld().spawnEntity(tentacle);
        player.getItemCooldownManager().set(
          MineCellsItems.TENTACLE,
          MineCellsItems.TENTACLE.getAbilityCooldown(playerItem, player)
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
          var canCraft = cellForgeRecipe.matches(player.getInventory(), player.getWorld());
          if (!canCraft) {
            MineCells.LOGGER.warn(
              "Player {} tried to craft {} without having the required items",
              player.getName().getString(),
              cellForgeRecipe.getId().toString()
            );
            return;
          }
          var output = cellForgeRecipe.craft(player.getInventory(), player.getWorld().getRegistryManager());
          if (output == null) {
            MineCells.LOGGER.warn(
              "Player {} tried to craft {} but failed",
              player.getName().getString(),
              cellForgeRecipe.getId().toString()
            );
            return;
          }
          cellCrafter.setCooldown(10);
          cellCrafter.addStack(output);
        }
      });
    });
  }
}
