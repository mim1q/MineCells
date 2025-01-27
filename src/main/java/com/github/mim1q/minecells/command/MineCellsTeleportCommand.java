package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

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
          .executes(teleport(false, false, false))
          .then(argument("to_exit", BoolArgumentType.bool())
            .executes(teleport(false, false, true))
            .then(argument("player", EntityArgumentType.player())
              .executes(teleport(true, false, true))
              .then(argument("position", BlockPosArgumentType.blockPos())
                .executes(teleport(true, true, true))
              )
            )
          )
        )
    );
  }

  private static Command<ServerCommandSource> teleport(boolean specifiedPlayer, boolean specifiedPosition, boolean specifiedToExit) {
    return (ctx) -> {
      var dimensionType = getDimensionFromString(ctx, "dimension");
      var player = specifiedPlayer ? EntityArgumentType.getPlayer(ctx, "player") : ctx.getSource().getPlayerOrThrow();
      var position = specifiedPosition ? BlockPosArgumentType.getBlockPos(ctx, "position") : null;
      var toExit = specifiedToExit && BoolArgumentType.getBool(ctx, "to_exit");

      if (dimensionType.isEmpty()) {
        ctx.getSource().sendError(Text.of("Invalid dimension"));
        return 0;
      }

      dimensionType.get().teleportPlayer(player, ctx.getSource().getWorld(), position, toExit);

      return 0;
    };
  }

  public static Optional<MineCellsDimension> getDimensionFromString(CommandContext<ServerCommandSource> ctx, String key) {
    var dimension = StringArgumentType.getString(ctx, key);
    MineCellsDimension dimensionType = null;
    for (var type : MineCellsDimension.values()) {
      if (type.key.getValue().getPath().equals(dimension)) {
        dimensionType = type;
        break;
      }
    }
    if (dimensionType == null) {
      return Optional.empty();
    }
    return Optional.of(dimensionType);
  }

  public static SuggestionProvider<ServerCommandSource> suggestDimension() {
    return (ctx, builder) -> {
      for (var type : MineCellsDimension.values()) {
        builder.suggest(type.key.getValue().getPath());
      }
      return builder.buildFuture();
    };
  }
}
