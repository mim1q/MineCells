package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.MineCells;
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

    dispatcher.register(
      literal("minecells:wipe_data").requires(source -> source.hasPermissionLevel(2) || source.getServer().isSingleplayer())
        .executes(MineCellsDataCommand::wipeData)
    );
  }

  private static int syncPlayerData(CommandContext<ServerCommandSource> ctx) {
    ServerPlayerEntity player;
    try {
      player = EntityArgumentType.getPlayer(ctx, "player");
    } catch (CommandSyntaxException e) {
      return 1;
    }
    // todo
    ctx.getSource().sendFeedback(() -> Text.literal("Mine Cells data synchronized for player " + player.getName().getString()), false);
    return 0;
  }

  private static int clearPlayerData(CommandContext<ServerCommandSource> ctx) {
    ServerPlayerEntity player;
    try {
      player = EntityArgumentType.getPlayer(ctx, "player");
    } catch (CommandSyntaxException e) {
      return 1;
    }
    // todo
    MineCells.DIMENSION_GRAPH.saveStuckPlayer(player);
    ctx.getSource().sendFeedback(() -> Text.literal("Mine Cells data cleared for player " + player.getName().getString()), false);
    return 0;
  }

  private static int printServerPlayerData(CommandContext<ServerCommandSource> ctx) {
    try {
      var player = EntityArgumentType.getPlayer(ctx, "player");
      // todo
    } catch (CommandSyntaxException e) {
      return 1;
    }
    return 0;
  }

  private static int printClientPlayerData(CommandContext<ServerCommandSource> ctx) {
    ctx.getSource().sendFeedback(() -> Text.literal("Not implemented"), false);
    return 0;
  }

  private static int printServerWorldData(CommandContext<ServerCommandSource> ctx) {
    try {
      var player = EntityArgumentType.getPlayer(ctx, "player");
      // todo
    } catch (CommandSyntaxException e) {
      return 1;
    }
    return 0;
  }

  private static int wipeData(CommandContext<ServerCommandSource> ctx) {
    var world = ctx.getSource().getWorld();
    var player = ctx.getSource().getPlayer();
    if (world == null || player == null) return 1;
    // todo
    return 0;
  }
}