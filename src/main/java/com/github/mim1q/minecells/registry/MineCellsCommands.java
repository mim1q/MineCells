package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.command.CellsCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class MineCellsCommands {
  public static void init() {
    CommandRegistrationCallback.EVENT.register(CellsCommand::register);
  }
}
