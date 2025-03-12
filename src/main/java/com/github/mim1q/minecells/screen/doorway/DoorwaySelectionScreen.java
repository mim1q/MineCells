package com.github.mim1q.minecells.screen.doorway;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.cc.MineCellsLevelCC;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.network.ServerPacketHandler;
import com.github.mim1q.minecells.network.c2s.UpdateDoorwayC2SPacket;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DoorwaySelectionScreen extends BaseOwoScreen<FlowLayout> {
  private final BlockPos pos;
  private @Nullable MineCellsDimension selectedDimension;
  private final ArrayList<ButtonComponent> exitButtons = new ArrayList<>();
  private final ArrayList<ButtonComponent> dimensionButtons = new ArrayList<>();
  private final FlowLayout body;

  public DoorwaySelectionScreen(BlockPos pos) {
    this.pos = pos;
    this.body = (FlowLayout) Containers.horizontalFlow(Sizing.content(), Sizing.fixed(150))
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
    rootComponent.child(Components.label(Text.literal("Select a dimension")).margins(Insets.bottom(16)));

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
    exitButtons.child(createButton("close", b -> {
    }).active(true));
    rootComponent.child(exitButtons);
  }

  private static String getFlagNameForDimension(MineCellsDimension dim) {
    return switch (dim) {
      case PROMENADE_OF_THE_CONDEMNED -> "promenade_of_the_condemned";
      case RAMPARTS -> "ramparts";
      case BLACK_BRIDGE -> "black_bridge";
      case INSUFFERABLE_CRYPT -> "insufferable_crypt";
      default -> "torn_kings_crest";
    };
  }

  private void updateBody() {
    body.clearChildren();
    if (selectedDimension == null) return;

    body.child(
      new FlagComponent(getFlagNameForDimension(selectedDimension))
        .sizing(Sizing.fixed(48), Sizing.fixed(144))
        .margins(Insets.both(16, 8))
    );

    var text = Containers.verticalFlow(Sizing.fixed(200), Sizing.content());
    text.child(
      Components
        .label(
          Text.translatable(selectedDimension.translationKey)
            .styled(it -> it.withBold(true).withColor(selectedDimension.getColor()))
        )
        .horizontalTextAlignment(HorizontalAlignment.CENTER)
        .margins(Insets.vertical(10))
    );
    text.child(Components.label(
      Text.translatable("book.minecells.entries.dimensions." + selectedDimension.key.getValue().getPath() + ".description")
    ).horizontalSizing(Sizing.fill(100)));

    body.child(text);
  }

  private ButtonComponent createDimensionButton(MineCellsDimension dim) {
    var player = MinecraftClient.getInstance().player;
    var world = MinecraftClient.getInstance().world;
    if (player == null || world == null) return null;

    var data = MineCellsLevelCC.PortalsCC.findDataOfPosition(world, pos);

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
    }, !dim.canEnter(player, data.orElse(null)));
    dimensionButtons.add(button);
    return button;
  }

  private ButtonComponent createExitButton(String name, boolean onlyOwnerCanEnter) {
    var button = createButton(name, b -> {
      if (selectedDimension == null) return;
      ServerPacketHandler.CHANNEL.clientHandle()
        .send(new UpdateDoorwayC2SPacket(pos, selectedDimension.key.getValue(), onlyOwnerCanEnter));
      close();
    });
    exitButtons.add(button);
    return button;
  }

  private ButtonComponent createButton(String name, Consumer<ButtonComponent> consumer) {
    var button = Components.button(Text.empty(), consumer).active(false)
      .renderer(new DimensionButton.Renderer(
        MineCells.createId("textures/gui/doorway_selection/" + name + ".png")
      ));
    button.sizing(Sizing.fixed(32), Sizing.fixed(32));
    button.cursorStyle(CursorStyle.POINTER);
    return button;
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  private static class FlagComponent extends BaseComponent {
    private final Identifier texture;

    protected FlagComponent(String name) {
      texture = MineCells.createId("textures/blockentity/banner/" + name + ".png");
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      var matrices = context.getMatrices();
      matrices.push();
      matrices.translate(x, y, 0);
      var size = width / 16f;
      matrices.scale(size, size, size);
      context.drawTexture(texture, 0, 0, 2, 2, 16, 2, 64, 64);
      context.drawTexture(texture, 0, 2, 0, 16, 16, 48, 64, 64);
      matrices.pop();
    }
  }
}
