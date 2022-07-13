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
import net.minecraft.util.math.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class ConjunctiviusTentacleFeatureRenderer extends FeatureRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {

  private final ConjunctiviusTentacleModel model;
  private final List<MathUtils.PosRotScale> posRotScales = new ArrayList<>();

  private static final Identifier TEXTURE = MineCells.createId("textures/entity/sewers_tentacle/purple.png");

  public ConjunctiviusTentacleFeatureRenderer(FeatureRendererContext<ConjunctiviusEntity, ConjunctiviusEntityModel> context, ModelPart tentacleRoot) {
    super(context);
    this.model = new ConjunctiviusTentacleModel(tentacleRoot);
  }

  public void addPosRotScale(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
    this.posRotScales.add(MathUtils.PosRotScale.ofDegrees(px, py, pz, rx, ry, rz, sx, sy, sz));
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ConjunctiviusEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    int offset = 0;
    this.posRotScales.clear();
    this.addPosRotScale(0.7F, 1.9F, 0.725F, 0.0F, 45.0F, 0.0F, 0.5F, 0.5F, 0.5F);
    this.addPosRotScale(-0.7F, 1.9F, 0.725F, 0.0F, -45.0F, 0.0F, 0.5F, 0.5F, 0.5F);
    this.addPosRotScale(1.1F, 1.5F, 0.55F, 15.0F, 60.0F, -15.0F, 0.75F, 0.75F, 0.75F);
    this.addPosRotScale(-1.1F, 1.5F, 0.55F, 15.0F, -60.0F, 15.0F, 0.75F, 0.75F, 0.75F);
    this.addPosRotScale(1.0F, 1.75F, 0.25F, 5.0F, 80.0F, -5.0F, 0.66F, 0.66F, 0.66F);
    this.addPosRotScale(-1.0F, 1.75F, 0.25F, -5.0F, -80.0F, -5.0F, 0.66F, 0.66F, 0.66F);

    for (MathUtils.PosRotScale posRotScale : posRotScales) {
      matrices.push();
      matrices.translate(posRotScale.getPos().getX(), posRotScale.getPos().getY(), posRotScale.getPos().getZ());
      matrices.multiply(Quaternion.fromEulerXyz(posRotScale.getRot()));
      matrices.scale(-1.0F, -1.0F, 1.0F);
      matrices.scale(posRotScale.getScale().getX(), posRotScale.getScale().getY(), posRotScale.getScale().getZ());
      RenderLayer renderLayer = this.model.getLayer(TEXTURE);
      this.model.setOffset(offset * 255);
      offset++;
      this.model.setAngles(entity, limbAngle, limbDistance, (entity.age + tickDelta) * 0.75F, headYaw, headPitch);
      boolean hurt = entity.hurtTime > 0;
      this.model.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.getUv(0.0F, hurt), 1.0F, 1.0F, 1.0F, 1.0F);
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
      SewersTentacleEntityModel.wiggleTentacle(this.segments, animationProgress, this.offset);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
  }
}
