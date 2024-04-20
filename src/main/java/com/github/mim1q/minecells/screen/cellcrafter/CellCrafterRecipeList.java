package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreen.TexturedButton;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreen.backgroundTexture;

public class CellCrafterRecipeList {
  public static final Identifier RECIPES_SCREEN_TEXTURE = MineCells.createId("textures/gui/cell_crafter/recipes.png");
  private final Runnable closeAction;

  public CellCrafterRecipeList(Runnable closeAction) {
    this.closeAction = closeAction;
  }

  public void build(FlowLayout rootComponent) {
    var container = Containers.verticalFlow(Sizing.fixed(160), Sizing.fixed(176));
    container
      .surface(backgroundTexture(RECIPES_SCREEN_TEXTURE, 256, 256))
      .horizontalAlignment(HorizontalAlignment.LEFT)
      .verticalAlignment(VerticalAlignment.TOP)
      .padding(Insets.of(8));

    var textBox = new NoShadowTextBox(Sizing.fixed(70));
    textBox.setDrawsBackground(false);
    textBox.setRenderTextProvider((string, firstCharacterIndex) ->
      OrderedText.styledForwardsVisitedString(string, Style.EMPTY.withFormatting(Formatting.BLACK))
    );
    textBox.positioning(Positioning.absolute(58, 18));
    container.child(textBox);

    container.child(
      new TexturedButton(
        it -> this.closeAction.run(),
        RECIPES_SCREEN_TEXTURE,
        208, 0
      )
        .sizing(Sizing.fixed(16))
        .positioning(Positioning.absolute(4, 18))
    );

    rootComponent.child(container);
  }

  //#region Absolutely disgusting workaround to disable shadows in the textbox
  private static class DisableShadowDrawContext extends DrawContext {
    public DisableShadowDrawContext(DrawContext parent) {
      super(MinecraftClient.getInstance(), parent.getVertexConsumers());
      this.getMatrices().multiplyPositionMatrix(parent.getMatrices().peek().getPositionMatrix());
    }

    @Override
    public int drawTextWithShadow(TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
      return this.drawText(textRenderer, text, x, y, color, false);
    }
  }

  private static class NoShadowTextBox extends TextBoxComponent {
    protected NoShadowTextBox(Sizing horizontalSizing) {
      super(horizontalSizing);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
      super.renderButton(new DisableShadowDrawContext(context), mouseX, mouseY, delta);
    }
  }
  //#endregion
}
