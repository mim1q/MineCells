package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityRegistry {
    // Create EntityTypes
    public static final EntityType<JumpingZombieEntity> JUMPING_ZOMBIE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "jumping_zombie"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, JumpingZombieEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 2.0f))
                    .build()
    );

    public static void register() {
        // Register Entities

        // Register Attributes
        FabricDefaultAttributeRegistry.register(JUMPING_ZOMBIE, JumpingZombieEntity.createMobAttributes());

        // Register Spawn Eggs

    }
}
