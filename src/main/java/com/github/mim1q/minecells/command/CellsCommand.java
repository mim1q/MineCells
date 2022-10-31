package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CellsCommand {
  @SuppressWarnings("unused")
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
    dispatcher.register(literal("cells").requires(source -> source.hasPermissionLevel(2))
      .then(literal("set")
        .then(argument("player", EntityArgumentType.player())
          .then(argument("amount", IntegerArgumentType.integer(0))
            .executes(CellsCommand::setCells)
          )
        )
      )
      .then(literal("give")
        .then(argument("player", EntityArgumentType.player())
          .then(argument("amount", IntegerArgumentType.integer(0))
            .executes(CellsCommand::giveCells)
          )
        )
      )
      .then(literal("take")
        .then(argument("player", EntityArgumentType.player())
          .then(argument("amount", IntegerArgumentType.integer(0))
            .executes(CellsCommand::takeCells)
          )
        )
      )
      .then(literal("get")
        .then(argument("player", EntityArgumentType.player())
          .executes(CellsCommand::getCells)
        )
      )
    );
  }

  public static int setCells(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
    int cells = IntegerArgumentType.getInteger(ctx, "amount");
    ((PlayerEntityAccessor) player).setCells(cells);
    ctx.getSource().sendFeedback(Text.of("Set " + player.getName().getString() + "'s cells to " + cells), false);
    return 1;
  }

  public static int giveCells(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
    PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
    int cells = IntegerArgumentType.getInteger(ctx, "amount");
    accessor.setCells(accessor.getCells() + cells);
    ctx.getSource().sendFeedback(Text.of("Gave " + cells + " cells to " + player.getName().getString()), false);
    return 1;
  }

  public static int takeCells(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
    PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
    int cells = IntegerArgumentType.getInteger(ctx, "amount");
    int currentCells = accessor.getCells();
    if (currentCells < cells) {
      ctx.getSource().sendError(Text.of(player.getName().getString() + " doesn't have enough cells"));
      return 0;
    }
    accessor.setCells(currentCells - cells);
    ctx.getSource().sendFeedback(Text.of("Took " + cells + " cells from " + player.getName().getString()), false);
    return 1;
  }

  public static int getCells(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
    int cells = ((PlayerEntityAccessor) player).getCells();
    ctx.getSource().sendFeedback(Text.of(player.getName().getString() + " has " + cells + " cells"), false);
    return 1;
  }
}
