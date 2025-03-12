package com.github.mim1q.minecells.screen.doorway;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DimensionButton extends ButtonComponent {
  private boolean isSelected;

  protected DimensionButton(MineCellsDimension dimension, Consumer<ButtonComponent> onPress) {
    super(Text.empty(), onPress);
    var texture = MineCells.createId("textures/gui/doorway_selection/dimension/" + dimension.key.getValue().getPath() + ".png");
    this.renderer(new Renderer(texture));

    var tooltips = new ArrayList<Text>();
    tooltips.add(Text.translatable(dimension.translationKey).styled(it -> it.withBold(true).withColor(dimension.getColor())));
    if (dimension == MineCellsDimension.BLACK_BRIDGE || dimension == MineCellsDimension.INSUFFERABLE_CRYPT) {
      tooltips.add(Text.translatable("gui.minecells.doorway_selection.boss").styled(it -> it.withColor(Formatting.RED)));
    }


    tooltip(tooltips);


    this.sizing(Sizing.fixed(32), Sizing.fixed(32));
  }

  @Override
  public boolean isSelected() {
    return super.isSelected() || isSelected;
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
      if (!button.active()) v = 0;
      else if (button.isSelected()) v = 64;
      context.drawTexture(texture, button.x() + 4, button.y() + 4, 0, v, 32, 32, 32, 96);
    }
  }
}
