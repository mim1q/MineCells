package com.github.mim1q.minecells.screen.doorway;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class DoorwaySelectionScreen extends BaseOwoScreen<FlowLayout> {
  @Override
  protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
    return OwoUIAdapter.create(
      this,
      (w, h) -> (FlowLayout) Containers.verticalFlow(w, h)
        .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
    );
  }

  @Override
  protected void build(FlowLayout rootComponent) {
    rootComponent.surface(Surface.VANILLA_TRANSLUCENT);
    rootComponent.child(Components.label(Text.literal("Doorway test")));

    var dimensions = Containers.horizontalFlow(Sizing.content(), Sizing.content());
    for (var dim : MineCellsDimension.values()) {
      dimensions
        .child(Components.button(Text.translatable(dim.translationKey), (b) -> {
        }));
    }
    rootComponent.child(dimensions);
  }
}
