package com.github.mim1q.minecells.screen.cellcrafter;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class CellCrafterScreen extends BaseOwoHandledScreen<FlowLayout, CellCrafterScreenHandler> {
  protected CellCrafterScreen(CellCrafterScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
  }

  @Override
  @NotNull
  protected OwoUIAdapter<FlowLayout> createAdapter() {
    return OwoUIAdapter.create(this, Containers::verticalFlow);
  }

  @Override protected void build(FlowLayout rootComponent) {
    rootComponent
      .surface(Surface.VANILLA_TRANSLUCENT)
      .horizontalAlignment(HorizontalAlignment.CENTER)
      .verticalAlignment(VerticalAlignment.CENTER);

    rootComponent.child(
      Containers.verticalFlow(Sizing.content(), Sizing.content())
        .child(Components.button(Text.of("test"), it -> System.out.println("test button clicked")))
        .padding(Insets.of(10))
        .surface(Surface.DARK_PANEL)
        .verticalAlignment(VerticalAlignment.CENTER)
        .horizontalAlignment(HorizontalAlignment.CENTER)
    );
  }
}
