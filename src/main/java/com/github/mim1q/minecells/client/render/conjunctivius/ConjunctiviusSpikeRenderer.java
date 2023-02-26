package com.github.mim1q.minecells.client.render.conjunctivius;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ConjunctiviusSpikeRenderer extends FeatureRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {

  private final ConjunctiviusSpikeModel model;
  private final List<MathUtils.PosRotScale> posRotScales = new ArrayList<>();

  public static final Identifier TEXTURE = MineCells.createId("textures/entity/conjunctivius/spike.png");

  public ConjunctiviusSpikeRenderer(FeatureRendererContext<ConjunctiviusEntity, ConjunctiviusEntityModel> context, ModelPart spikeRoot) {
    super(context);
    this.model = new ConjunctiviusSpikeModel(spikeRoot);
  }

  public void addPosRotScale(float px, float py, float pz, float rx, float ry, float rz, float s) {
    this.posRotScales.add(MathUtils.PosRotScale.ofDegrees(px, py, pz, rx, ry, rz, s, s, s));
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ConjunctiviusEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    for (MathUtils.PosRotScale posRotScale : this.posRotScales) {
      matrices.push();
      posRotScale.apply(matrices);
      this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
      boolean hurt = entity.hurtTime > 0;
      this.model.render(matrices, vertexConsumers.getBuffer(model.getLayer(TEXTURE)), light, OverlayTexture.getUv(0.0F, hurt), 1.0F, 1.0F, 1.0F, 1.0F);
      matrices.pop();
    }
  }

  public static class ConjunctiviusSpikeModel extends EntityModel<ConjunctiviusEntity> {

    private final ModelPart base;
    private final ModelPart spike;

    public ConjunctiviusSpikeModel(ModelPart root) {
      super(RenderLayer::getEntityCutout);
      this.base = root.getChild("base");
      this.spike = this.base.getChild("spike");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();

      ModelPartData dBase = modelPartData.addChild("base",
        ModelPartBuilder.create()
          .uv(0, 0)
          .cuboid(-2.5F, -2.0F, -2.5F, 5, 6, 5, new Dilation(0.05F)),
        ModelTransform.NONE
      );

      dBase.addChild("spike",
        ModelPartBuilder.create()
          .uv(0, 11)
          .cuboid(-2.5F, -10.0F, 0.0F, 5, 8, 0)
          .uv(0, 6)
          .cuboid(0.0F, -10.0F, -2.5F, 0, 8, 5),
        ModelTransform.NONE
      );

      return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ConjunctiviusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
      entity.spikeOffset.update(animationProgress);
      this.spike.pivotY = entity.spikeOffset.getValue();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.base.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
  }
}
