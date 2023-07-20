package com.github.mim1q.minecells.client.render.blockentity.portal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.portal.TeleporterBlock;
import com.github.mim1q.minecells.block.portal.TeleporterBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3f;

public class TeleporterBlockEntityRenderer implements BlockEntityRenderer<TeleporterBlockEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/blockentity/teleporter_core.png");
  private final TeleporterModel model;

  public TeleporterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    model = new TeleporterModel(ctx.getLayerModelPart(MineCellsRenderers.TELEPORTER_LAYER));
  }

  @Override
  public void render(TeleporterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
    var dir = entity.getCachedState().get(TeleporterBlock.FACING);
    matrices.push();
    matrices.scale(-1.0F, -1.0F, 1.0F);
    matrices.translate(-0.5F, -0.5F, 0.5F);
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(dir.asRotation()));
    matrices.translate(-0.5F, -1.5F, 0.0F);
    model.render(matrices, vertices, light, overlay, 0xFFFBED, 0xFF7C00);
    matrices.pop();
  }

  public static class TeleporterModel extends Model {
    private final ModelPart runesFill;
    private final ModelPart runesOutline;
    private final ModelPart portalOutline;
    private final ModelPart portalFill;

    public TeleporterModel(ModelPart root) {
      super(RenderLayer::getEntityCutout);
      runesFill = root.getChild("runes_fill");
      runesOutline = root.getChild("runes_outline");
      portalOutline = root.getChild("portal_outline");
      portalFill = root.getChild("portal_fill");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("runes_outline", ModelPartBuilder.create()
        .uv(112, 0).cuboid(-4.0F, -39.0F, -5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 0).cuboid(-26.0F, -28.0F, -5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 16).cuboid(-30.0F, -8.0F, -5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(112, 16).cuboid(25.0F, -8.0F, -5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 32).cuboid(20.0F, -27.0F, -5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 48).cuboid(-5.0F, 16.0F, -5.05F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 48).mirrored().cuboid(-5.0F, 16.0F, 5.05F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(112, 0).mirrored().cuboid(-4.0F, -39.0F, 5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(96, 0).mirrored().cuboid(18.0F, -28.0F, 5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(96, 16).mirrored().cuboid(22.0F, -8.0F, 5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(112, 16).mirrored().cuboid(-33.0F, -8.0F, 5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(96, 32).mirrored().cuboid(-28.0F, -27.0F, 5.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
      modelPartData.addChild("runes_fill", ModelPartBuilder.create()
        .uv(112, 8).cuboid(-4.0F, -39.0F, 0.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 56).cuboid(-5.0F, 16.0F, 0.05F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 8).cuboid(-26.0F, -28.0F, 0.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 24).cuboid(-30.0F, -8.0F, 0.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(112, 24).cuboid(25.0F, -8.0F, 0.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 40).cuboid(20.0F, -27.0F, 0.05F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
        .uv(96, 40).mirrored().cuboid(-28.0F, -27.0F, 10.15F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(112, 8).mirrored().cuboid(-4.0F, -39.0F, 10.15F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(96, 56).mirrored().cuboid(-5.0F, 16.0F, 10.15F, 10.0F, 5.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(96, 8).mirrored().cuboid(18.0F, -28.0F, 10.15F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(96, 24).mirrored().cuboid(22.0F, -8.0F, 10.15F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(112, 24).mirrored().cuboid(-33.0F, -8.0F, 10.15F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 24.0F, -5.1F));
      modelPartData.addChild("portal_outline", ModelPartBuilder.create().uv(0, 0).cuboid(-20.0F, -28.0F, 0.0F, 40.0F, 40.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
      modelPartData.addChild("portal_fill", ModelPartBuilder.create().uv(0, 40).cuboid(-20.0F, -27.0F, 0.0F, 40.0F, 38.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
      return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) { }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int fillColor, int outlineColor) {
      portalFill.setPivot(0, 24, 0);
      var fill = new float[] {
        ColorHelper.Argb.getRed(fillColor) / 255F,
        ColorHelper.Argb.getGreen(fillColor) / 255F,
        ColorHelper.Argb.getBlue(fillColor) / 255F
      };
      var outline = new float[] {
        ColorHelper.Argb.getRed(outlineColor) / 255F,
        ColorHelper.Argb.getGreen(outlineColor) / 255F,
        ColorHelper.Argb.getBlue(outlineColor) / 255F
      };

      portalFill.render(matrices, vertices, 0xF000F0, overlay, fill[0], fill[1], fill[2], 1.0F);
      portalOutline.render(matrices, vertices, 0xF000D0, overlay, outline[0], outline[1], outline[2], 1.0F);
      runesFill.render(matrices, vertices, 0xF000F0, overlay, fill[0], fill[1], fill[2], 1.0F);
      runesOutline.render(matrices, vertices, 0xF000D0, overlay, outline[0], outline[1], outline[2], 1.0F);
    }
  }
}
