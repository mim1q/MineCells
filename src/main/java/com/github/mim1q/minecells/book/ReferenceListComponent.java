package com.github.mim1q.minecells.book;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class ReferenceListComponent implements ICustomComponent {
  private IVariable references;

  private transient GuiBook bookGui = null;
  private transient List<Identifier> referencesList;
  private transient List<BookEntry> entries = new ArrayList<>();

  @Override
  public void build(int componentX, int componentY, int pageNum) {
    entries.clear();
    bookGui = null;
  }

  @Override
  public void render(DrawContext graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
    if (entries.isEmpty() && context.getGui() instanceof GuiBook) {
      bookGui = (GuiBook) context.getGui();
      entries = bookGui.book.getContents().entries.values()
        .stream()
        .filter(entry -> referencesList.contains(entry.getId()))
        .toList();
    }

    if (bookGui == null || entries.isEmpty()) return;

    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);

      graphics.getMatrices().push();
      {
        graphics.getMatrices().scale(0.5f, 0.5f, 0.5f);
        entry.getIcon().render(graphics, 0, 20 + i * 40);
      }
      graphics.getMatrices().pop();

      var textRenderer = new BookTextRenderer(bookGui, entry.getName(), 10, 10 + i * 20);
      textRenderer.render(graphics, mouseX, mouseY);
    }
  }

  @Override
  public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
    referencesList = lookup.apply(references)
      .asList()
      .stream()
      .map(it -> new Identifier(it.asString()))
      .toList();
  }
}
