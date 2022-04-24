package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.client.model.GrenadierEntityModel;
import com.github.mim1q.minecells.client.renderer.layer.GrenadierGlowLayer;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GrenadierEntityRenderer extends GeoEntityRenderer<GrenadierEntity> {
    public GrenadierEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new GrenadierEntityModel());
        this.addLayer(new GrenadierGlowLayer(this));
        this.shadowRadius = 0.35f;
    }
}
