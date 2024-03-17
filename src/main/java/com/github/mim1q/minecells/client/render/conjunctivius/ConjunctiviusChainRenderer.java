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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class ConjunctiviusChainRenderer extends FeatureRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {

  private final static Identifier TEXTURE = MineCells.createId("textures/entity/conjunctivius/chain.png");

  private final ConjunctiviusChainModel model;

  public ConjunctiviusChainRenderer(FeatureRendererContext<ConjunctiviusEntity, ConjunctiviusEntityModel> context, ModelPart chainRoot) {
    super(context);
    this.model = new ConjunctiviusChainModel(chainRoot);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ConjunctiviusEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    if (entity.isForDisplay()) {
      return;
    }

    Vec3d startPos = entity.getLerpedPos(tickDelta);

    VertexConsumer vertices = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
    int stage = entity.getStage();
    if (stage < 2) {
      renderChain(matrices, vertices, light, startPos, Vec3d.ofBottomCenter(entity.getRightAnchor()), entity.getYaw(), new Vector3f(2.2F, 0.85F, 0.0F));
    }
    if (stage < 4) {
      renderChain(matrices, vertices, light, startPos, Vec3d.ofBottomCenter(entity.getLeftAnchor()), entity.getYaw(), new Vector3f(-2.2F, 0.85F, 0.0F));
    }
    if (stage < 6) {
      renderChain(matrices, vertices, light, startPos, Vec3d.ofBottomCenter(entity.getTopAnchor().up()), entity.getYaw(), new Vector3f(0.0F, -1.75F, 0.0F));
    }
  }

  protected void renderChain(MatrixStack matrices, VertexConsumer vertices, int light, Vec3d startPos, Vec3d targetPos, float headYaw, Vector3f offset) {
    matrices.push();
    matrices.scale(0.75F, 0.75F, 0.75F);
    startPos = startPos.add(0.0D, -offset.y * 2.0D + 3.0D, 0.01D);
    Vec3d direction = targetPos.subtract(startPos);
    direction = direction.rotateY(MathUtils.radians(headYaw));
    direction = direction.add(-offset.x * 1.5D, 0.0D, 0.0D);

    float rx = (float) -Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
    float ry = (float) -Math.atan2(direction.x, direction.z);
    MathUtils.PosRotScale posRotScale = MathUtils.PosRotScale.ofRadians(
      offset,
      new Vector3f(rx, ry, 0.0F),
      new Vector3f(1.0F, 1.0F, 1.0F)
    );
    posRotScale.apply(matrices);
    int count = (int) (direction.length());
    for (int i = 0; i < count; i++) {
      this.model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      matrices.translate(0.0F, 0.0F, -1.0F);
    }
    matrices.pop();
  }

  public static class ConjunctiviusChainModel extends EntityModel<ConjunctiviusEntity> {

    private final ModelPart main;

    public ConjunctiviusChainModel(ModelPart part) {
      super(RenderLayer::getEntityCutout);
      this.main = part.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("main",
        ModelPartBuilder.create()
          .uv(0, 0)
          .cuboid(-4.0F, -12.0F, 0.0F, 8, 12, 0)
          .uv(0, -8)
          .cuboid(0.0F, -20.0F, -4.0F, 0, 12, 8),
        ModelTransform.rotation(MathHelper.HALF_PI, 0.0F, 0.0F)
      );

      return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(ConjunctiviusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.main.pitch = MathUtils.radians(90.0F);
      this.main.render(matrices, vertices, light, overlay);
    }
  }
}
