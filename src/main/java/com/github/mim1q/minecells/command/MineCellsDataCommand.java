package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.world.state.MineCellsData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MineCellsDataCommand {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
    dispatcher.register(
      literal("minecells:print_data").requires(source -> source.hasPermissionLevel(2))
        .then(argument("player", EntityArgumentType.player())
          .then(literal("serverplayer")
            .executes(MineCellsDataCommand::printServerPlayerData)
          )
          .then(literal("serverworld")
            .executes(MineCellsDataCommand::printServerWorldData)
          )
          .then(literal("clientplayer")
            .executes(MineCellsDataCommand::printClientPlayerData)
          )
        )
    );

    dispatcher.register(
      literal("minecells:sync_data").requires(source -> source.hasPermissionLevel(2))
        .then(argument("player", EntityArgumentType.player())
          .executes(MineCellsDataCommand::syncPlayerData)
        )
    );

    dispatcher.register(
      literal("minecells:clear_data").requires(source -> source.hasPermissionLevel(2))
        .then(argument("player", EntityArgumentType.player())
          .executes(MineCellsDataCommand::clearPlayerData)
        )
    );
  }

  private static int syncPlayerData(CommandContext<ServerCommandSource> ctx) {
    ServerPlayerEntity player;
    try {
      player = EntityArgumentType.getPlayer(ctx, "player");
    } catch (CommandSyntaxException e) {
      return 1;
    }
    MineCellsData.syncCurrentPlayerData(player, ctx.getSource().getWorld());
    ctx.getSource().sendFeedback(Text.literal("Mine Cells data synchronized for player " + player.getName().getString()), false);
    return 0;
  }

  private static int clearPlayerData(CommandContext<ServerCommandSource> ctx) {
    ServerPlayerEntity player;
    try {
      player = EntityArgumentType.getPlayer(ctx, "player");
    } catch (CommandSyntaxException e) {
      return 1;
    }
    MineCellsData.getPlayerData(player, ctx.getSource().getWorld()).portals.clear();
    MineCellsData.syncCurrentPlayerData(player, ctx.getSource().getWorld());
    ctx.getSource().sendFeedback(Text.literal("Mine Cells data cleared for player " + player.getName().getString()), false);
    return 0;
  }

  private static int printServerPlayerData(CommandContext<ServerCommandSource> ctx) {
    ctx.getSource().sendFeedback(Text.literal("Not implemented"), false);
    return 0;
  }

  private static int printClientPlayerData(CommandContext<ServerCommandSource> ctx) {
    ctx.getSource().sendFeedback(Text.literal("Not implemented"), false);
    return 0;
  }

  private static int printServerWorldData(CommandContext<ServerCommandSource> ctx) {
    try {
      var player = EntityArgumentType.getPlayer(ctx, "player");
      var data = MineCellsData.getPlayerData(player, ctx.getSource().getWorld());
      sendData(ctx.getSource().getPlayer(), player, player.getName().getString(), data);
    } catch (CommandSyntaxException e) {
      return 1;
    }
    return 0;
  }

  private static void sendData(
    ServerPlayerEntity receiver,
    ServerPlayerEntity player,
    String playerName,
    MineCellsData.PlayerData data
  ) {
    if (receiver == null) {
      return;
    }
    var runPos = "x: " + Math.round(player.getX() / 1024F) + ", y: " + Math.round(player.getY() / 1024F);
    receiver.sendMessage(Text.of("Data of player " + playerName + " for run " + runPos));
    receiver.sendMessage(Text.of("Visited portals: "));
    data.portals.forEach(it -> {
      receiver.sendMessage(Text.of("  " + it.fromDimension().name() + " [" + it.fromPos().toShortString() + "] -> " + it.toDimension().name() + " [" + it.toPos().toShortString() + "]"));
    });
  }
}