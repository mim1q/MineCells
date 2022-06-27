package com.github.mim1q.minecells.client.gui;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CellAmountHud extends DrawableHelper {

    public static final Identifier CELL_TEXTURE = MineCells.createId("textures/entity/cell.png");

    private final MinecraftClient client;
    private ClientPlayerEntity player;
    private int lastChange = 0;
    private int lastAmount = 0;
    private final AnimationProperty offset = new AnimationProperty(16.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

    public CellAmountHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrixStack, float deltaTick) {
        this.player = this.client.player;

        if (this.player != null) {
            float width = this.client.getWindow().getScaledWidth();
            float height = this.client.getWindow().getScaledHeight();
            int cellAmount = ((PlayerEntityAccessor)this.player).getCells();

            if (cellAmount != this.lastAmount) {
                this.lastAmount = cellAmount;
                this.lastChange = this.player.age;
            }

            int changeTicks = this.player.age - this.lastChange;
            if (changeTicks < 100 && cellAmount > 0) {
                this.offset.setupTransitionTo(-16.0F, 5.0F);
            } else {
                this.offset.setupTransitionTo(0.0F, 10.0F);
            }
            this.offset.update(player.age + deltaTick);

            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, CELL_TEXTURE);
            this.drawTexture(matrixStack, 0, 0, 0, 0, 16, 16);
            this.client.textRenderer.drawWithShadow(
                matrixStack,
                String.valueOf(cellAmount),
                width * 0.5F + 100.0F,
                height + this.offset.getValue(),
                0x5FC8EF
            );
            RenderSystem.disableBlend();
        }
    }
}
