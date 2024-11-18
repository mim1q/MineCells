package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class MineCellsScreenHandlerTypes {
  public static final ScreenHandlerType<CellCrafterScreenHandler> CELL_FORGE_SCREEN_HANDLER = registerScreenHandler("cell_forge", CellCrafterScreenHandler::new);

  private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String path, ScreenHandlerType.Factory<T> factory) {
    return Registry.register(Registries.SCREEN_HANDLER, MineCells.createId(path), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
  }

  public static void init() { }
}
