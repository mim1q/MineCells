package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class GrenadierEntityModel extends AnimatedGeoModel<GrenadierEntity> {
    @Override
    public Identifier getModelLocation(GrenadierEntity object) {
        return new Identifier(MineCells.MOD_ID, "geo/entity/grenadier.geo.json");
    }

    @Override
    public Identifier getTextureLocation(GrenadierEntity object) {
        return new Identifier(MineCells.MOD_ID, "textures/entity/grenadier/grenadier.png");
    }

    @Override
    public Identifier getAnimationFileLocation(GrenadierEntity object) {
        return new Identifier(MineCells.MOD_ID, "animations/entity/grenadier.animation.json");
    }

    @Override
    public void setLivingAnimations(GrenadierEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("neck");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationY((extraData.netHeadYaw) * ((float) Math.PI / 340F));
        }
    }
}
