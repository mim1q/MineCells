package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.block.FlagBlock.Placement;
import com.github.mim1q.minecells.block.blockentity.FlagBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Math;
import org.joml.Quaternionf;

import java.util.ArrayList;

import static com.github.mim1q.minecells.util.RenderUtils.getGlobalAnimationProgress;

public class FlagBlockEntityRenderer implements BlockEntityRenderer<FlagBlockEntity> {
  private final BiomeBannerBlockEntityModel model;
  private final BiomeBannerBlockEntityModel largeModel;

  public FlagBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    EntityModelLoader loader = ctx.getLayerRenderDispatcher();
    this.model = new BiomeBannerBlockEntityModel(loader.getModelPart(MineCellsRenderers.FLAG_LAYER));
    this.largeModel = new BiomeBannerBlockEntityModel(loader.getModelPart(MineCellsRenderers.FLAG_LARGE_LAYER));
  }

  @Override
  public int getRenderDistance() {
    return 256;
  }

  @Override
  public void render(FlagBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    var block = entity.getCachedState().getBlock();
    if (!(block instanceof FlagBlock)) {
      return;
    }
    var dir = entity.getCachedState().get(FlagBlock.FACING);
    var placement = entity.getCachedState().get(FlagBlock.PLACEMENT);
    var world = entity.getWorld();
    var offset = entity.getPos().hashCode();
    var large = ((FlagBlock) block).large;
    var usedModel = large ? largeModel : model;
    var strength = (placement == Placement.HORIZONTAL ? 0.2F : 0.125F) * (large ? 0.3F : 1.0F);

    if (world != null && entity.getCachedState().get(FlagBlock.WAVING)) {
      float time = getGlobalAnimationProgress();
      usedModel.wave(
        time * (large ? 0.15F : 0.1F),
        offset % 100,
        strength * (1.0F + 0.15F * Math.cos(time * 0.13F)),
        true
      );
    } else {
      usedModel.resetSegments();
    }

    matrices.push();
    var offsetZ = 0F;
    switch (placement) {
      case SIDE:
        offsetZ = 7 / 16F;
      case CENTERED:
        matrices.translate(0.5F, 15 / 16F, 0.5F);
        matrices.scale(1F, -1F, -1F);
        matrices.multiply(new Quaternionf().rotationY(MathUtils.radians(dir.asRotation())));
        matrices.translate(0F, 0F, offsetZ);
        break;
      case HORIZONTAL:
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.scale(1F, -1F, -1F);
        matrices.multiply(new Quaternionf().rotationZ(MathUtils.radians(90F)));
        matrices.multiply(new Quaternionf().rotationX(MathUtils.radians(-90F + dir.asRotation())));
        matrices.translate(0F, -13 / 16F, 0F);
    }

    Identifier texture = ((FlagBlock) block).texture;
    usedModel.render(matrices, vertexConsumers.getBuffer(usedModel.getLayer(texture)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  @Override
  public boolean rendersOutsideBoundingBox(FlagBlockEntity blockEntity) {
    return true;
  }

  public static class BiomeBannerBlockEntityModel extends Model {
    private final ModelPart main;

    private final ModelPart[] segments;

    public BiomeBannerBlockEntityModel(ModelPart root) {
      super(RenderLayer::getEntityCutout);
      this.main = root.getChild("main");
      var segments = new ArrayList<ModelPart>();
      var parent = this.main;
      for (int i = 0; parent.hasChild("segment" + i); ++i) {
        segments.add(parent.getChild("segment" + i));
        parent = segments.get(i);
      }
      this.segments = segments.toArray(new ModelPart[0]);
    }

    public static TexturedModelData getTexturedModelData(boolean large) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();

      var width = large ? 24 : 16;
      var height = 8;
      var segmentCount = large ? 14 : 6;
      var segmentDatas = new ModelPartData[segmentCount];

      ModelPartData dMain = modelPartData.addChild("main",
        ModelPartBuilder.create()
          .uv(0, 0)
          .cuboid(-width / 2F, -1.0F, -1.0F, width, 2.0F, 2.0F),
        ModelTransform.NONE
      );

      for (int i = 0; i < segmentCount; i++) {
        var parent = i == 0 ? dMain : segmentDatas[i - 1];
        segmentDatas[i] = parent.addChild("segment" + i,
          ModelPartBuilder.create()
            .uv(0, 16 + i * height)
            .cuboid(-width / 2F, 0F, 0F, width, height, 0F),
          ModelTransform.pivot(0F, i == 0 ? 0F : height, 0F)
        );
      }

      return TexturedModelData.of(modelData, 64, large ? 128 : 64);
    }

    public void setupLargeItemModel() {
      for (int i = 5; i < segments.length; ++i) {
        segments[i].hidden = true;
      }
    }

    public void wave(float animationProgress, float offset, float strength, boolean tapered) {
      for (int i = 0; i < this.segments.length; i++) {
        this.segments[i].pitch = Math.sin(animationProgress - i + offset) * strength * (tapered ? i : 1.0F);
      }
    }

    public void resetSegments() {
      for (ModelPart part : segments) {
        part.pitch = 0.0F;
        part.hidden = false;
      }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
  }
}
