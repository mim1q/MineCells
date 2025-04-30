package com.github.mim1q.minecells.screen.doorway;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DimensionButton extends ButtonComponent {
  private boolean isSelected;
  private final boolean locked;

  protected DimensionButton(MineCellsDimension dimension, Consumer<ButtonComponent> onPress, boolean locked) {
    super(Text.empty(), locked ? b -> {
    } : onPress);
    this.locked = locked;

    var texture = MineCells.createId("textures/gui/doorway_selection/dimension/" + dimension.key.getValue().getPath() + ".png");
    this.renderer(new Renderer(texture));

    var tooltips = new ArrayList<Text>();
    tooltips.add(Text.translatable(dimension.translationKey).styled(it -> it.withBold(true).withColor(dimension.getColor())));
    if (dimension == MineCellsDimension.BLACK_BRIDGE || dimension == MineCellsDimension.INSUFFERABLE_CRYPT) {
      tooltips.add(Text.translatable("gui.minecells.doorway_selection.boss").styled(it -> it.withColor(Formatting.RED)));
    }
    if (locked) {
      tooltips.add(Text.translatable("gui.minecells.doorway_selection.locked").formatted(Formatting.GRAY));
      dimension.getRequirements().forEach(
        it -> tooltips.add(it.getTooltip().copy().styled(style -> style.withColor(Formatting.GRAY)))
      );
    }
    tooltip(tooltips);

    this.sizing(Sizing.fixed(32), Sizing.fixed(32));
  }

  @Override
  public CursorStyle cursorStyle() {
    return locked ? CursorStyle.NONE : super.cursorStyle();
  }

  @Override
  public boolean isSelected() {
    return super.isSelected() || isSelected;
  }


  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderWidget(context, mouseX, mouseY, delta);
  }

  public void setSelected(boolean selected) {
    this.isSelected = selected;
  }

  public record Renderer(
    Identifier texture
  ) implements ButtonComponent.Renderer {
    @Override
    public void draw(OwoUIDrawContext context, ButtonComponent button, float delta) {
      var v = 32;
      if (!button.active() || button instanceof DimensionButton dimButton && dimButton.locked) v = 0;
      else if (button.isSelected()) v = 64;
      context.drawTexture(texture, button.x() + 4, button.y() + 4, 0, v, 32, 32, 32, 96);
    }
  }
}
