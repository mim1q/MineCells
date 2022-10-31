package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;

public class MineCellsScreenHandlerTypes {
  public static final ScreenHandlerType<CellForgeScreenHandler> CELL_FORGE = Registry.register(
    Registry.SCREEN_HANDLER,
    MineCells.createId("cell_forge"),
    new ScreenHandlerType<>(CellForgeScreenHandler::new)
  );

  public static void init() { }
}
