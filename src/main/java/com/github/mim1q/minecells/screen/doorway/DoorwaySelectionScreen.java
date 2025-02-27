package com.github.mim1q.minecells.screen.doorway;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.network.ServerPacketHandler;
import com.github.mim1q.minecells.network.c2s.UpdateDoorwayC2SPacket;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DoorwaySelectionScreen extends BaseOwoScreen<FlowLayout> {
  private final BlockPos pos;
  private @Nullable MineCellsDimension selectedDimension;
  private final ArrayList<ButtonComponent> exitButtons = new ArrayList<>();
  private final ArrayList<ButtonComponent> dimensionButtons = new ArrayList<>();

  public DoorwaySelectionScreen(BlockPos pos) {
    this.pos = pos;
  }

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
      dimensions.child(createDimensionButton(dim));
    }
    rootComponent.child(dimensions);

    var exitButtons = Containers.horizontalFlow(Sizing.content(), Sizing.content());
    exitButtons.child(createExitButton(Text.of("Only me"), true));
    exitButtons.child(createExitButton(Text.of("Everyone"), false));
    rootComponent.child(exitButtons);
  }

  private ButtonComponent createDimensionButton(MineCellsDimension dim) {
    var button = Components.button(Text.translatable(dim.translationKey), b -> {
      selectedDimension = dim;
      exitButtons.forEach(it -> it.active(true));
      dimensionButtons.forEach(it -> it.setFocused(false));
      b.setFocused(true);
    });
    dimensionButtons.add(button);
    return button;
  }

  private ButtonComponent createExitButton(Text name, boolean onlyOwnerCanEnter) {
    var button = Components.button(name, b -> {
      if (selectedDimension == null) return;
      ServerPacketHandler.CHANNEL.clientHandle()
        .send(new UpdateDoorwayC2SPacket(pos, selectedDimension.key.getValue(), onlyOwnerCanEnter));
      close();
    }).active(false);
    exitButtons.add(button);
    return button;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }
}
