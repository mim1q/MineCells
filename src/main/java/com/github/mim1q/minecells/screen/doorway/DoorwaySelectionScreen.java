package com.github.mim1q.minecells.screen.doorway;

import com.github.mim1q.minecells.MineCells;
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
  private final FlowLayout body;

  public DoorwaySelectionScreen(BlockPos pos) {
    this.pos = pos;
    this.body = (FlowLayout) Containers.verticalFlow(Sizing.fixed(160), Sizing.fixed(150))
      .horizontalAlignment(HorizontalAlignment.CENTER);
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
    rootComponent.child(Components.label(Text.literal("Select a dimension")));

    var dimensions = Containers.horizontalFlow(Sizing.content(), Sizing.content());

    dimensions.child(createDimensionButton(MineCellsDimension.PRISONERS_QUARTERS));
    dimensions.child(createDimensionButton(MineCellsDimension.PROMENADE_OF_THE_CONDEMNED));
    dimensions.child(createDimensionButton(MineCellsDimension.RAMPARTS));
    dimensions.child(createDimensionButton(MineCellsDimension.INSUFFERABLE_CRYPT));
    dimensions.child(createDimensionButton(MineCellsDimension.BLACK_BRIDGE));

    rootComponent.child(dimensions);
    rootComponent.child(body);

    var exitButtons = Containers.horizontalFlow(Sizing.content(), Sizing.content());
    exitButtons.child(createExitButton("apply_only_me", true));
    exitButtons.child(createExitButton("apply_everyone", false));
    exitButtons.child(createExitButton("close", false));
    rootComponent.child(exitButtons);
  }

  private void updateBody() {
    body.clearChildren();
    if (selectedDimension == null) return;

    body.child(
      Components.label(Text.literal("Ramparts").styled(it -> it.withBold(true).withColor(0xffcd2c)))
        .horizontalTextAlignment(HorizontalAlignment.CENTER)
        .margins(Insets.vertical(10))
    );
    body.child(Components.label(Text.literal("The tower type area")).horizontalSizing(Sizing.fill(100)));
    body.child(Components.label(Text.literal("Proper description goes here")).horizontalSizing(Sizing.fill(100)));
  }

  private ButtonComponent createDimensionButton(MineCellsDimension dim) {
    var button = new DimensionButton(dim, b -> {
      selectedDimension = dim;
      exitButtons.forEach(it -> it.active(true));
      dimensionButtons.forEach(it -> {
        it.setFocused(false);
        ((DimensionButton) it).setSelected(false);
      });
      b.setFocused(true);
      ((DimensionButton) b).setSelected(true);
      updateBody();
    });
    dimensionButtons.add(button);
    return button;
  }

  private ButtonComponent createExitButton(String name, boolean onlyOwnerCanEnter) {
    var button = Components.button(Text.empty(), b -> {
        if (selectedDimension == null) return;
        ServerPacketHandler.CHANNEL.clientHandle()
          .send(new UpdateDoorwayC2SPacket(pos, selectedDimension.key.getValue(), onlyOwnerCanEnter));
        close();
      }).active(false)
      .renderer(new DimensionButton.Renderer(
        MineCells.createId("textures/gui/doorway_selection/" + name + ".png")
      ));
    button.sizing(Sizing.fixed(32), Sizing.fixed(32));
    button.cursorStyle(CursorStyle.POINTER);
    exitButtons.add(button);
    return button;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }
}
