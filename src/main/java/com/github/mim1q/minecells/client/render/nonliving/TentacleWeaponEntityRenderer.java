package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.nonliving.TentacleWeaponEntityModel;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TentacleWeaponEntityRenderer extends EntityRenderer<TentacleWeaponEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/entity/tentacle_weapon.png");
  private final TentacleWeaponEntityModel model;

  public TentacleWeaponEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
    this.model = new TentacleWeaponEntityModel(ctx.getPart(MineCellsRenderers.TENTACLE_WEAPON_LAYER));
  }

  @Override
  public void render(TentacleWeaponEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    if (entity.age < 2) {
      return;
    }
    matrices.push();
    matrices.scale(-1.0F, -1.0F, 1.0F);
    float length = entity.getLength(tickDelta);
    Vector3f normalized = entity.getEndPos(Math.max(1.0F, length)).subtract(entity.getCameraPosVec(tickDelta)).normalize().toVector3f();
    float xRot = (float) Math.asin(-normalized.y());
    float yRot = (float) Math.atan2(normalized.x(), normalized.z());
    matrices.multiply(new Quaternionf().rotationYXZ(-yRot, -xRot, 0.0F));
    renderTentacle(matrices, vertexConsumers, light, length * 0.3F, 1F);
    renderTentacle(matrices, vertexConsumers, light, length * 0.6F, 0.8F);
    renderTentacle(matrices, vertexConsumers, light, length * 0.9F, 0.6F);
    renderTentacle(matrices, vertexConsumers, light, length * 1.2F, 0.4F);
    matrices.pop();

    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
  }

  public void renderTentacle(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float length, float scale) {
    matrices.push();
    var size = MathUtils.easeInOutQuad(1.1F, 0.5F, length) * scale;
    matrices.scale(size, size, length * 15F);
    this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(TEXTURE)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  @Override
  public Identifier getTexture(TentacleWeaponEntity entity) {
    return TEXTURE;
  }
}
