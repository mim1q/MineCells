package com.github.mim1q.minecells.mixin;

import com.github.mim1q.minecells.MineCellsClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At("TAIL"))
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MineCellsClient.cellAmountHud.renderInInventory(matrixStack);
    }
}
