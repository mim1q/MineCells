package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.command.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class MineCellsCommands {
  public static void init() {
    CommandRegistrationCallback.EVENT.register(CellsCommand::register);
    CommandRegistrationCallback.EVENT.register(SpawnerRuneCommand::register);
    CommandRegistrationCallback.EVENT.register(MineCellsDataCommand::register);
    CommandRegistrationCallback.EVENT.register(MineCellsTeleportCommand::register);
    CommandRegistrationCallback.EVENT.register(SpecialPointCommand::register);
  }
}
