package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.InquisitorEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class InquisitorEntityModel extends AnimatedGeoModel<InquisitorEntity> {
    @Override
    public Identifier getModelLocation(InquisitorEntity object) {
        return new Identifier(MineCells.MOD_ID, "geo/entity/inquisitor.geo.json");
    }

    @Override
    public Identifier getTextureLocation(InquisitorEntity object) {
        return new Identifier(MineCells.MOD_ID, "textures/entity/inquisitor.png");
    }

    @Override
    public Identifier getAnimationFileLocation(InquisitorEntity animatable) {
        return new Identifier(MineCells.MOD_ID, "animations/entity/inquisitor.animation.json");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setLivingAnimations(InquisitorEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("neck");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationY((extraData.netHeadYaw) * ((float) Math.PI / 340F));
        }
    }
}
