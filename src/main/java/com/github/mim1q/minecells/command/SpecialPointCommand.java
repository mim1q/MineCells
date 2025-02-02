package com.github.mim1q.minecells.command;


import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Optional;

import static com.github.mim1q.minecells.command.MineCellsTeleportCommand.getDimensionFromString;
import static com.github.mim1q.minecells.command.MineCellsTeleportCommand.suggestDimension;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpecialPointCommand {
  public static void register(
    CommandDispatcher<ServerCommandSource> dispatcher,
    CommandRegistryAccess registryAccess,
    CommandManager.RegistrationEnvironment environment
  ) {
    dispatcher.register(literal("minecells:special_point").then(
      argument("id", IdentifierArgumentType.identifier())
        .executes(specialPointAllArgs(false, false, false)).then(
          argument("dimension", StringArgumentType.string()).suggests(suggestDimension())
            .executes(specialPointAllArgs(true, false, false)).then(
              argument("pos", BlockPosArgumentType.blockPos())
                .executes(specialPointAllArgs(true, true, false)).then(
                  argument("clear", BoolArgumentType.bool())
                    .executes(specialPointAllArgs(true, true, true))
                )
            )
        )
    ));

    dispatcher.register(literal("minecells:clear_special_point_cache").executes(ctx -> {
      GridBasedStructureUtils.clearSpecialPointsCache();
      ctx.getSource().sendFeedback(() -> Text.literal("Cleared special point cache"), true);
      return 0;
    }));
  }

  private static Command<ServerCommandSource> specialPointAllArgs(boolean hasDimension, boolean hasPos, boolean hasClear) {
    return (ctx) -> {
      var id = IdentifierArgumentType.getIdentifier(ctx, "id");

      var dimension = hasDimension
        ? getDimensionFromString(ctx, "dimension")
        : Optional.ofNullable(MineCellsDimension.of(ctx.getSource().getWorld()));

      if (dimension.isEmpty()) {
        ctx.getSource().sendError(Text.literal("Invalid dimension"));
        return 1;
      }

      var searchPos = hasPos
        ? BlockPosArgumentType.getBlockPos(ctx, "pos")
        : BlockPos.ofFloored(ctx.getSource().getPosition());

      if (!hasClear || BoolArgumentType.getBool(ctx, "clear")) {
        GridBasedStructureUtils.clearSpecialPointsCache();
        ctx.getSource().sendFeedback(() -> Text.literal("Cleared special point cache"), true);
      }

      return findSpecialPoint(ctx, dimension.get(), id, searchPos);
    };
  }

  private static int findSpecialPoint(
    CommandContext<ServerCommandSource> ctx,
    MineCellsDimension dimension,
    Identifier id,
    Vec3i searchPos
  ) {
    var posOpt = GridBasedStructureUtils.getSpecialPoint(ctx.getSource().getWorld(), searchPos, id);
    if (posOpt.isEmpty()) {
      ctx.getSource().sendError(Text.literal("Not found"));
      return 1;
    }
    var pos = posOpt.get().offset().add(MathUtils.getClosestMultiplePosition(searchPos, 1024));
    var dimensionKey = dimension.key.getValue().toString();
    var command = "/execute in " + dimensionKey + " run tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
    ctx.getSource().sendMessage(
      Text.literal("Found " + id + " at " + pos + ", rotation: " + posOpt.get().facing() + " in " + dimensionKey)
        .styled(it -> it.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)))
    );
    return 0;
  }
}
