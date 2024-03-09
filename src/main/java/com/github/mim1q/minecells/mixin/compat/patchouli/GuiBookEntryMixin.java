package com.github.mim1q.minecells.mixin.compat.patchouli;

import com.github.mim1q.minecells.book.ReferenceListComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "vazkii.patchouli.client.book.gui.GuiBookEntry")
public abstract class GuiBookEntryMixin {
  @Inject(method = "onPageChanged()V", at = @At("HEAD"))
  private void minecells$onPageChanged(CallbackInfo ci) {
    ReferenceListComponent.clearButtons();
  }
}
