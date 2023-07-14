package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.world.state.MineCellsData;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;
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
    var data = MineCellsData.getPlayerData(player, ctx.getSource().getWorld());
    MineCellsData.get(ctx.getSource().getWorld()).markDirty();
    data.portals.clear();
    data.activatedSpawnerRunes.clear();
    MineCellsData.syncCurrentPlayerData(player, ctx.getSource().getWorld());
    ctx.getSource().sendFeedback(Text.literal("Mine Cells data cleared for player " + player.getName().getString()), false);
    return 0;
  }

  private static int printServerPlayerData(CommandContext<ServerCommandSource> ctx) {
    try {
      var player = EntityArgumentType.getPlayer(ctx, "player");
      var data = ((PlayerEntityAccessor)player).getMineCellsData();
      sendData(ctx.getSource(), player.getName().getString(), data);
    } catch (CommandSyntaxException e) {
      return 1;
    }
    return 0;
  }

  private static int printClientPlayerData(CommandContext<ServerCommandSource> ctx) {
    ctx.getSource().sendFeedback(Text.literal("Not implemented"), false);
    return 0;
  }

  private static int printServerWorldData(CommandContext<ServerCommandSource> ctx) {
    try {
      var player = EntityArgumentType.getPlayer(ctx, "player");
      var data = new PlayerSpecificMineCellsData(MineCellsData.get(ctx.getSource().getWorld()), player);
      sendData(ctx.getSource(), player.getName().getString(), data);
    } catch (CommandSyntaxException e) {
      return 1;
    }
    return 0;
  }

  private static void sendData(
    ServerCommandSource source,
    String playerName,
    PlayerSpecificMineCellsData data
  ) {
    source.sendMessage(Text.of("=== Mine Cells Data for " + playerName + " ==="));
    data.map.forEach((key, value) -> {
      source.sendMessage(Text.of(key));
      source.sendMessage(Text.of(" Portals:"));
      value.portals.forEach(it -> source.sendMessage(Text.of("  " + it.fromDimension() + " [" + it.fromPos().toShortString() + "] -> " + it.toDimension() + " [" + it.toPos().toShortString() + "]")));
      source.sendMessage(Text.of(" Activated Spawner Runes:"));
      value.activatedSpawnerRunes.forEach((k, v) -> {
        var runesString = new StringBuilder();
        v.forEach(it -> runesString.append("[").append(it.toShortString()).append("] "));
        source.sendMessage(Text.of("  " + k + ": " + runesString));
      });
    });
  }
}