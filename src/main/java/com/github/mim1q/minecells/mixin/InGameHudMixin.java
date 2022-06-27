package com.github.mim1q.minecells.mixin;

import com.github.mim1q.minecells.client.gui.CellAmountHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    private CellAmountHud cellAmountHud;

    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/render/item/ItemRenderer;)V", at = @At(value = "RETURN"))
    private void init(MinecraftClient client, ItemRenderer itemRenderer, CallbackInfo callbackInfo) {
        this.cellAmountHud = new CellAmountHud(client);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrixStack, float deltaTick, CallbackInfo ci) {
        this.cellAmountHud.render(matrixStack, deltaTick);
    }
}
