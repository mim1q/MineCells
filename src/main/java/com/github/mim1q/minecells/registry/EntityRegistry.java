package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
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

    // Create Spawn Egg Items
    public static final Item JUMPING_ZOMBIE_SPAWN_EGG = new SpawnEggItem(
            JUMPING_ZOMBIE,
            0x56A25F,
            0xAEEE4E,
            new Item.Settings().group(ItemGroup.MISC)
    );

    public static void register() {
        // Register Attributes
        FabricDefaultAttributeRegistry.register(JUMPING_ZOMBIE, JumpingZombieEntity.createJumpingZombieAttributes());

        // Register Spawn Eggs
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "jumping_zombie_spawn_egg"), JUMPING_ZOMBIE_SPAWN_EGG);
    }
}
