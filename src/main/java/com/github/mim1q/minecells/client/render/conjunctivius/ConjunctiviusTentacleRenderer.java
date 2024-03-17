package com.github.mim1q.minecells.client.render.conjunctivius;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.SewersTentacleEntityModel;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
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

public class ConjunctiviusTentacleRenderer extends FeatureRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {

  private final ConjunctiviusTentacleModel model;
  private final List<MathUtils.PosRotScale> posRotScales = new ArrayList<>();

  private static final Identifier TEXTURE = MineCells.createId("textures/entity/sewers_tentacle/purple.png");
  private final RenderLayer layer;

  public ConjunctiviusTentacleRenderer(FeatureRendererContext<ConjunctiviusEntity, ConjunctiviusEntityModel> context, ModelPart tentacleRoot) {
    super(context);
    this.model = new ConjunctiviusTentacleModel(tentacleRoot);
    this.layer = this.model.getLayer(TEXTURE);
  }

  public void addPosRotScale(float px, float py, float pz, float rx, float ry, float rz, float s) {
    this.posRotScales.add(MathUtils.PosRotScale.ofDegrees(px, py, pz, rx, ry, rz, s, s, s));
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ConjunctiviusEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    int offset = 0;
    for (MathUtils.PosRotScale posRotScale : posRotScales) {
      matrices.push();
      posRotScale.apply(matrices);
      matrices.scale(-1.0F, -1.0F, 1.0F);
      this.model.setOffset(offset * 256);
      offset++;
      this.model.setAngles(entity, limbAngle, limbDistance, entity.isForDisplay() ? 140F : (animationProgress * 0.75F), headYaw, headPitch);
      boolean hurt = entity.hurtTime > 0;
      this.model.render(matrices, vertexConsumers.getBuffer(layer), light, OverlayTexture.getUv(0.0F, hurt), 1.0F, 1.0F, 1.0F, 1.0F);
      matrices.pop();
    }
  }

  public static class ConjunctiviusTentacleModel extends EntityModel<ConjunctiviusEntity> {

    private final ModelPart root;
    private final ModelPart[] segments = new ModelPart[5];
    private int offset = 0;

    public ConjunctiviusTentacleModel(ModelPart root) {
      this.root = root.getChild("root");
      this.segments[0] = this.root.getChild("segment_0");
      this.segments[1] = this.segments[0].getChild("segment_1");
      this.segments[2] = this.segments[1].getChild("segment_2");
      this.segments[3] = this.segments[2].getChild("segment_3");
      this.segments[4] = this.segments[3].getChild("segment_4");
    }

    public static TexturedModelData getTexturedModelData() {
      return SewersTentacleEntityModel.getTexturedModelData();
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override
    public void setAngles(ConjunctiviusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
      SewersTentacleEntityModel.wiggleTentacle(this.segments, animationProgress, 10, this.offset, 0);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
  }
}
