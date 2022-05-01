package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.client.model.InquisitorEntityModel;
import com.github.mim1q.minecells.entity.InquisitorEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class InquisitorEntityRenderer extends GeoEntityRenderer<InquisitorEntity> {

    public InquisitorEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new InquisitorEntityModel());
        this.shadowRadius = 0.35F;
    }
}
