package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.RunnerEntityModel;
import com.github.mim1q.minecells.entity.RunnerEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RunnerEntityRenderer extends MobEntityRenderer<RunnerEntity, RunnerEntityModel> {

    public static Identifier TEXTURE = MineCells.createId("textures/entity/runner.png");

    public RunnerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RunnerEntityModel(context.getPart(RendererRegistry.RUNNER_LAYER)), 0.4f);
    }

    @Override
    public Identifier getTexture(RunnerEntity entity) {
        return TEXTURE;
    }
}
