package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MineCellsTeleportCommand {
  public static void register(
    CommandDispatcher<ServerCommandSource> dispatcher,
    CommandRegistryAccess registryAccess,
    RegistrationEnvironment environment
  ) {
    dispatcher.register(
      literal("minecells:tp").requires(source -> source.hasPermissionLevel(2))
        .then(argument("dimension", StringArgumentType.string())
          .suggests(suggestDimension())
          .executes(teleport(false, false))
          .then(argument("player", EntityArgumentType.player())
            .executes(teleport(true, false))
            .then(argument("position", BlockPosArgumentType.blockPos())
              .executes(teleport(true, true))
            )
          )
        )
    );
  }

  private static Command<ServerCommandSource> teleport(boolean specifiedPlayer, boolean specifiedPosition) {
    return (ctx) -> {
      var dimension = StringArgumentType.getString(ctx, "dimension");
      MineCellsDimension dimensionType = null;
      for (var type : MineCellsDimension.values()) {
        if (type.key.getValue().getPath().equals(dimension)) {
          dimensionType = type;
          break;
        }
      }
      if (dimensionType == null) {
        ctx.getSource().sendError(Text.of("Invalid dimension"));
        return 1;
      }
      var player = specifiedPlayer ? EntityArgumentType.getPlayer(ctx, "player") : ctx.getSource().getPlayerOrThrow();
      var position = specifiedPosition ? BlockPosArgumentType.getBlockPos(ctx, "position") : null;
      dimensionType.teleportPlayer(player, ctx.getSource().getWorld(), position);

      return 0;
    };
  }

  private static SuggestionProvider<ServerCommandSource> suggestDimension() {
    return (ctx, builder) -> {
      for (var type : MineCellsDimension.values()) {
        builder.suggest(type.key.getValue().getPath());
      }
      return builder.buildFuture();
    };
  }
}
