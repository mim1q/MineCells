package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BiomeBannerBlock;
import com.github.mim1q.minecells.block.blockentity.BiomeBannerBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class BiomeBannerBlockEntityRenderer implements BlockEntityRenderer<BiomeBannerBlockEntity> {

  public static final Identifier TEXTURE = MineCells.createId("textures/blockentity/banner/promenade_of_the_condemned.png");

  private final BiomeBannerBlockEntityModel model;
  public BiomeBannerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    EntityModelLoader loader = ctx.getLayerRenderDispatcher();
    this.model = new BiomeBannerBlockEntityModel(loader.getModelPart(MineCellsRenderers.BIOME_BANNER_LAYER));
  }

  @Override
  public void render(BiomeBannerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();
    Direction dir = entity.getCachedState().get(BiomeBannerBlock.FACING);
    matrices.translate(0.5F, 1.0F, 0.5F);
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-dir.asRotation()));
    matrices.translate(0.0F, 0.0F, -0.4375F);
    matrices.scale(1.0F, -1.0F, -1.0F);
    World world = entity.getWorld();
    if (world != null && entity.getCachedState().get(BiomeBannerBlock.WAVING)) {
      float amplitude = (entity.getWorld().getLightLevel(LightType.SKY, entity.getPos()) - world.getAmbientDarkness()) * 0.067F;
      float time = world.getTime() + tickDelta;
      this.model.wave(
        (time * 0.1F) % (2.0F * MathHelper.PI),
        entity.getPos().getX() - entity.getPos().getZ(),
        amplitude
      );
    }
    this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(TEXTURE)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  public static class BiomeBannerBlockEntityModel extends Model {
    private final ModelPart main;

    private final ModelPart[] segments = new ModelPart[6];

    public BiomeBannerBlockEntityModel(ModelPart root) {
      super(RenderLayer::getEntityCutoutNoCull);
      this.main = root.getChild("main");
      this.segments[0] = this.main.getChild("segment0");
      this.segments[1] = this.segments[0].getChild("segment1");
      this.segments[2] = this.segments[1].getChild("segment2");
      this.segments[3] = this.segments[2].getChild("segment3");
      this.segments[4] = this.segments[3].getChild("segment4");
      this.segments[5] = this.segments[4].getChild("segment5");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();

      ModelPartData dMain = modelPartData.addChild("main",
        ModelPartBuilder.create()
          .uv(0, 48)
          .cuboid(-8.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F),
        ModelTransform.NONE
      );

      ModelPartData dSegment0 = dMain.addChild("segment0",
        ModelPartBuilder.create()
          .uv(0, 0)
          .cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 8.0F, 0.0F),
        ModelTransform.pivot(0.0F, 0.0F, 0.0F)
      );

      ModelPartData dSegment1 = dSegment0.addChild("segment1",
        ModelPartBuilder.create()
          .uv(0, 8)
          .cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 8.0F, 0.0F),
        ModelTransform.pivot(0.0F, 8.0F, 0.0F)
      );

      ModelPartData dSegment2 = dSegment1.addChild("segment2",
        ModelPartBuilder.create()
          .uv(0, 16)
          .cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 8.0F, 0.0F),
        ModelTransform.pivot(0.0F, 8.0F, 0.0F)
      );

      ModelPartData dSegment3 = dSegment2.addChild("segment3",
        ModelPartBuilder.create()
          .uv(0, 24)
          .cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 8.0F, 0.0F),
        ModelTransform.pivot(0.0F, 8.0F, 0.0F)
      );

      ModelPartData dSegment4 = dSegment3.addChild("segment4",
        ModelPartBuilder.create()
          .uv(0, 32)
          .cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 8.0F, 0.0F),
        ModelTransform.pivot(0.0F, 8.0F, 0.0F)
      );

      dSegment4.addChild("segment5",
        ModelPartBuilder.create()
          .uv(0, 40)
          .cuboid(-8.0F, 0.0F, 0.0F, 16.0F, 8.0F, 0.0F),
        ModelTransform.pivot(0.0F, 8.0F, 0.0F)
      );

      return TexturedModelData.of(modelData, 64, 64);
    }

    public void wave(float animationProgress, float offset, float amplitude) {
      for (int i = 0; i < this.segments.length; i++) {
        this.segments[i].pitch = MathHelper.cos(animationProgress - (float) i + offset) * 0.05F * amplitude * i;
      }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
  }
}
