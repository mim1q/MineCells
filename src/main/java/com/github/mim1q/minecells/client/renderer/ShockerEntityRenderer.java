package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.client.model.ShockerEntityModel;
import com.github.mim1q.minecells.client.renderer.layer.ShockerGlowLayer;
import com.github.mim1q.minecells.entity.ShockerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ShockerEntityRenderer extends GeoEntityRenderer<ShockerEntity> {
    public ShockerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ShockerEntityModel());
        this.addLayer(new ShockerGlowLayer(this));
        this.shadowRadius = 0.5f;
    }
}
