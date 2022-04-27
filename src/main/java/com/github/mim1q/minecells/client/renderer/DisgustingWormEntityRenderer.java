package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.client.model.DisgustingWormEntityModel;
import com.github.mim1q.minecells.client.renderer.layer.DisgustingWormGlowLayer;
import com.github.mim1q.minecells.entity.DisgustingWormEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DisgustingWormEntityRenderer extends GeoEntityRenderer<DisgustingWormEntity> {
    public DisgustingWormEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DisgustingWormEntityModel());
        this.addLayer(new DisgustingWormGlowLayer(this));
        this.shadowRadius = 0.35f;
    }
}
