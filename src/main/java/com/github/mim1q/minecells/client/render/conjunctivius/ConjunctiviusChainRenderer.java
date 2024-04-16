package com.github.mim1q.minecells.client.render.conjunctivius;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class ConjunctiviusChainRenderer extends FeatureRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {
  private final static Identifier MODEL = MineCells.createId("misc/conjunctivius/chain");

  private final BakedModel model;

  public ConjunctiviusChainRenderer(
    FeatureRendererContext<ConjunctiviusEntity, ConjunctiviusEntityModel> context,
     BakedModelManager modelManager
  ) {
    super(context);
    model = modelManager.getModel(MODEL);
  }

  @Override
  public void render(
    MatrixStack matrices,
    VertexConsumerProvider vertexConsumers,
    int light,
    ConjunctiviusEntity entity,
    float limbAngle,
    float limbDistance,
    float tickDelta,
    float animationProgress,
    float headYaw,
    float headPitch
  ) {
    if (entity.isForDisplay()) {
      return;
    }

    Vec3d startPos = entity.getLerpedPos(tickDelta);

    VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getCutout());
    int stage = entity.getStage();
    if (stage < 2) {
      renderChain(
        entity, matrices, vertices, light, startPos,
        Vec3d.ofBottomCenter(entity.getRightAnchor()),
        entity.getYaw(), new Vector3f(2.2F, 0.85F, 0.0F)
      );
    }
    if (stage < 4) {
      renderChain(
        entity, matrices, vertices, light, startPos,
        Vec3d.ofBottomCenter(entity.getLeftAnchor()),
        entity.getYaw(), new Vector3f(-2.2F, 0.85F, 0.0F)
      );
    }
    if (stage < 6) {
      renderChain(
        entity, matrices, vertices, light, startPos,
        Vec3d.ofBottomCenter(entity.getTopAnchor().up()),
        entity.getYaw(), new Vector3f(0.0F, -1.75F, 0.0F)
      );
    }
  }

  protected void renderChain(
    ConjunctiviusEntity entity,
    MatrixStack matrices,
    VertexConsumer vertices,
    int light,
    Vec3d startPos,
    Vec3d targetPos,
    float headYaw,
    Vector3f offset
  ) {
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
    matrices.translate(-0.5, -0.5, -1.25);
    for (int i = 0; i < count; i++) {
      RenderUtils.renderBakedModel(model, entity.getRandom(), light, matrices, vertices);
      matrices.translate(0.0F, 0.0F, -1.0F);
    }
    matrices.pop();
  }
}
