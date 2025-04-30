package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.cc.MineCellsLevelCC;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.network.c2s.UpdateDoorwayC2SPacket;
import com.github.mim1q.minecells.network.c2s.UseTentacleWeaponC2SPacket;
import com.github.mim1q.minecells.network.s2c.*;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.recipe.PlayerInventoryInput;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import io.wispforest.owo.network.OwoNetChannel;

import static com.github.mim1q.minecells.world.processor.SwitchBlockStructureProcessor.copyAllProperties;

public class ServerPacketHandler {
  public static OwoNetChannel CHANNEL = OwoNetChannel.create(MineCells.createId("main"));

  public static void init() {
    CHANNEL.registerClientboundDeferred(SpawnerRuneUpdateS2CPacket.class);
    CHANNEL.registerClientboundDeferred(OpenDoorwayScreenS2CPacket.class);
    CHANNEL.registerClientboundDeferred(SpawnRuneParticlesS2CPacket.class);
    CHANNEL.registerClientboundDeferred(ObeliskActivationS2CPacket.class);
    CHANNEL.registerClientboundDeferred(ShockwaveClientEventS2CPacket.class);
    CHANNEL.registerClientboundDeferred(SendUnlockedCellCrafterRecipesS2CPacket.class);
    CHANNEL.registerClientboundDeferred(UpdateConjunctiviusBossBarS2CPacket.class);
    CHANNEL.registerClientboundDeferred(CritS2CPacket.class);
    CHANNEL.registerClientboundDeferred(ExplosionS2CPacket.class);

    CHANNEL.registerServerbound(UpdateDoorwayC2SPacket.class, (msg, ctx) -> {
      var world = ctx.player().getWorld();

      var newState = MineCellsBlocks.DOORWAY_PORTALS.get(msg.dimensionId()).getDefaultState();
      var state = world.getBlockState(msg.doorwayPos());
      if (!(state.getBlock() instanceof DoorwayPortalBlock)) {
        MineCells.LOGGER.error(
          "{} tried to modify a Doorway that doesn't exist at x={}, y={}, z={} in {}",
          ctx.player().getName().getString(),
          msg.doorwayPos().getX(), msg.doorwayPos().getY(), msg.doorwayPos().getZ(),
          world.getRegistryKey().getValue().toString()
        );
        return;
      }
      world.setBlockState(msg.doorwayPos(), copyAllProperties(state, newState));

      var entity = world.getBlockEntity(msg.doorwayPos());
      if (entity instanceof DoorwayPortalBlockEntity doorway) {
        var pos = MineCellsLevelCC.PortalsCC.getOrCreatePortal(ctx.player()).runCenter();
        if (doorway.canEdit(ctx.player())) {
          doorway.update(ctx.player(), pos, msg.onlyOwnerCanUse());
        } else {
          MineCells.LOGGER.warn(
            "{} tried to illegally edit portal at x={}, y={}, z={} in {}",
            ctx.player().getName().getString(),
            msg.doorwayPos().getX(), msg.doorwayPos().getY(), msg.doorwayPos().getZ(),
            world.getRegistryKey().getValue().toString()
          );
        }
      }
    });


    CHANNEL.registerServerbound(UseTentacleWeaponC2SPacket.class, (msg, handler) -> {
      var targetPos = msg.targetPos();
      var player = handler.player();
      var playerItem = player.getMainHandStack();

      var maxDistance = MineCells.COMMON_CONFIG.baseTentacleMaxDistance() + 2.0;

      if (!playerItem.isOf(MineCellsItems.TENTACLE)
        || targetPos.squaredDistanceTo(player.getPos()) > maxDistance * maxDistance
      ) {
        MineCells.LOGGER.warn("Invalid tentacle weapon use packet from player {}", player.getName().getString());
        return;
      }

      var server = handler.runtime();
      server.execute(() -> {
        var tentacle = TentacleWeaponEntity.create(player.getWorld(), player, targetPos, playerItem);
        player.getWorld().spawnEntity(tentacle);
        player.getItemCooldownManager().set(
          MineCellsItems.TENTACLE,
          MineCellsItems.TENTACLE.getAbilityCooldown(playerItem, player)
        );
      });
    });

    CHANNEL.registerServerbound(RequestUnlockedCellCrafterRecipesC2SPacket.class, ((msg, handler) -> {
      CHANNEL.serverHandle(handler.player()).send(new SendUnlockedCellCrafterRecipesS2CPacket(handler.player()));
    }));

    CHANNEL.registerServerbound(CellCrafterCraftRequestC2SPacket.class, (msg, handler) -> {
      var pos = msg.pos();
      var recipeId = msg.recipeId();
      var player = handler.player();
      var server = handler.runtime();

      server.execute(() -> {
        var blockEntity = player.getWorld().getBlockEntity(pos);
        var recipe = server.getRecipeManager().get(recipeId).get().value();
        if (
          blockEntity instanceof CellCrafterBlockEntity cellCrafter
            && recipe instanceof CellForgeRecipe cellForgeRecipe
        ) {
          var canCraft = cellForgeRecipe.matches(new PlayerInventoryInput(player.getInventory()), player.getWorld());
          if (!canCraft) {
            MineCells.LOGGER.warn(
              "Player {} tried to craft {} without having the required items",
              player.getName().getString(),
              cellForgeRecipe.id().toString()
            );
            return;
          }
          var output = cellForgeRecipe.craft(new PlayerInventoryInput(player.getInventory()), player.getWorld().getRegistryManager());
          if (output == null) {
            MineCells.LOGGER.warn(
              "Player {} tried to craft {} but failed",
              player.getName().getString(),
              cellForgeRecipe.id().toString()
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
