package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.ElevatorBlockEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class ElevatorBlockEntityRenderer implements BlockEntityRenderer<ElevatorBlockEntity> {

    private final ModelPart base;
    private final ModelPart chain;
    float offset = 0.0F;

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/block/elevator.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

    public ElevatorBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart root = ctx.getLayerModelPart(RendererRegistry.ELEVATOR_LAYER);
        this.base = root.getChild("base");
        this.chain = root.getChild("chain");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("base",
                ModelPartBuilder.create()
                        .uv(0, 5)
                        .cuboid(-15.0F, 3.0F, -15.0F, 30, 2, 30)
                        .uv(0, 0)
                        .cuboid(-16.0F, 0.0F, -16.0F, 32, 3, 32)
                        .cuboid(-16.0F, 5.0F, -16.0F, 32, 3, 32)
                        .cuboid(-13.0F, 8.0F, -13.0F, 3, 2, 3)
                        .cuboid(10.0F, 8.0F, 10.0F, 3, 2, 3),
                ModelTransform.NONE);

        modelPartData.addChild("chain",
                ModelPartBuilder.create()
                        .uv(116, 0)
                        .cuboid(-1.5F, 0.0F, 0.0F, 3, 16, 0, new Dilation(0.001F))
                        .uv(122, 0)
                        .cuboid(0.0F, 0.0F, -1.5F, 0, 16, 3, new Dilation(0.001F)),
                ModelTransform.NONE);

        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    public boolean rendersOutsideBoundingBox(ElevatorBlockEntity blockEntity) {
        return false;
    }

    @Override
    public void render(ElevatorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() != null) {
            light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        }
        matrices.push();
        this.base.render(matrices, vertexConsumers.getBuffer(LAYER), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();

        this.offset = 0.0F;
        final float LINK_OFFSET = 0.71875F;
        this.renderChain(matrices, vertexConsumers, light, overlay, new Vec3f(LINK_OFFSET, 5.0F, LINK_OFFSET), 15, this.offset);
        this.renderChain(matrices, vertexConsumers, light, overlay, new Vec3f(-LINK_OFFSET, 5.0F, -LINK_OFFSET), 15, this.offset);
    }

    public void renderChain(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3f start, int length, float offset) {
        for (int i = 0; i < length; i++) {
            matrices.push();
            matrices.translate(start.getX(), start.getY() - i, start.getZ());
            this.chain.pivotY = offset * 16;
            this.chain.render(matrices, vertexConsumers.getBuffer(LAYER), light, overlay);
            matrices.pop();
        }
    }
}
