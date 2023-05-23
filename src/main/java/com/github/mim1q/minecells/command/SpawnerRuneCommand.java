package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnerRuneCommand {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
    dispatcher.register(literal("spawner_rune").requires(source -> source.hasPermissionLevel(2))
      .then(literal("spawn")
        .then(argument("id", IdentifierArgumentType.identifier())
          .executes(SpawnerRuneCommand::spawn)
          .then(argument("pos", Vec3ArgumentType.vec3())
            .executes(SpawnerRuneCommand::spawnAtPos)
          )
        )
      )
      .then(literal("remove")
        .executes(SpawnerRuneCommand::remove)
      )
    );
  }

  private static int spawn(ServerWorld world, Vec3d pos, Identifier id) {
    if (world == null || pos == null || id == null) return 1;
    var spawnerRune = MineCellsEntities.SPAWNER_RUNE.create(world);
    if (spawnerRune == null) return 1;
    spawnerRune.setPosition(pos);
    spawnerRune.setDataId(id);
    world.spawnEntity(spawnerRune);
    return 0;
  }

  private static int spawn(CommandContext<ServerCommandSource> ctx) {
    var world = ctx.getSource().getWorld();
    var pos = ctx.getSource().getPosition();
    var id = IdentifierArgumentType.getIdentifier(ctx, "id");
    return spawn(world, pos, id);
  }

  private static int spawnAtPos(CommandContext<ServerCommandSource> ctx) {
    var world = ctx.getSource().getWorld();
    var pos = ctx.getArgument("pos", DefaultPosArgument.class).toAbsolutePos(ctx.getSource());
    var id = IdentifierArgumentType.getIdentifier(ctx, "id");
    return spawn(world, pos, id);
  }

  private static int remove(CommandContext<ServerCommandSource> ctx) {
    var world = ctx.getSource().getWorld();
    var pos = ctx.getSource().getPosition();
    if (world == null || pos == null) return 1;
    var entities = world.getEntitiesByClass(
      SpawnerRuneEntity.class,
      Box.of(pos, 3.0D, 3.0D, 3.0D),
      Objects::nonNull
    );
    for (var entity : entities) {
      entity.kill();
    }
    return 0;
  }
}
