package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.cc.MineCellsLevelCC;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.nio.file.Files;

import static net.minecraft.server.command.CommandManager.literal;

public class MineCellsDumpCommand {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
    dispatcher.register(literal("minecells:dump").requires(it -> it.hasPermissionLevel(2))
      .then(literal("portals").executes(MineCellsDumpCommand::dumpPortals))
    );
  }

  private static int dumpPortals(CommandContext<ServerCommandSource> context) {
    var world = context.getSource().getWorld();
    var component = MineCellsLevelCC.PORTALS.get(world.getScoreboard());
    var nbt = new NbtCompound();
    component.writeToNbt(nbt, world.getRegistryManager());

    var path = FabricLoader.getInstance().getGameDir().resolve("minecells_dumps/portals.json");
    try {
      var json = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, nbt);
      if (!Files.exists(path.getParent())) {
        Files.createDirectories(path.getParent());
      }
      Files.write(path, new GsonBuilder().setPrettyPrinting().create().toJson(json).getBytes());
    } catch (Exception e) {
      MineCells.LOGGER.error("Failed to dump portals data", e);
    }

    return 0;
  }
}