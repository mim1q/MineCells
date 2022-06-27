package com.github.mim1q.minecells.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class CellAmountHud extends DrawableHelper {
    private final MinecraftClient client;
    private ClientPlayerEntity player;

    public CellAmountHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrixStack, float deltaTick) {
        this.player = this.client.player;

        if (this.player != null) {
            float width = this.client.getWindow().getScaledWidth();
            float height = this.client.getWindow().getScaledHeight();

            RenderSystem.enableBlend();
            this.client.textRenderer.drawWithShadow(
                matrixStack,
                "test string!",
                width * 0.5F + 100.0F,
                height - 16.0F,
                0xFF0000
            );
            RenderSystem.disableBlend();
        }
    }
}
