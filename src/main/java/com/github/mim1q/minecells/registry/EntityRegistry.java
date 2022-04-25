package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.renderer.GrenadierEntityRenderer;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import com.github.mim1q.minecells.entity.ShockerEntity;
import com.github.mim1q.minecells.entity.projectile.BigGrenadeEntity;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityRegistry {

    //region EntityTypes

    public static final EntityType<JumpingZombieEntity> JUMPING_ZOMBIE = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "jumping_zombie"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, JumpingZombieEntity::new)
            .dimensions(EntityDimensions.fixed(0.75f, 2.0f))
            .build()
    );

    public static final EntityType<ShockerEntity> SHOCKER = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "shocker"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShockerEntity::new)
            .dimensions(EntityDimensions.fixed(1.0f, 3.0f))
            .build()
    );

    public static final EntityType<GrenadierEntity> GRENADIER = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "grenadier"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GrenadierEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.0f))
                    .build()
    );

    public static final EntityType<GrenadeEntity> GRENADE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "grenade"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, GrenadeEntity::new)
                .dimensions(EntityDimensions.changing(0.5F, 0.5F))
                .build()
    );

    public static final EntityType<BigGrenadeEntity> BIG_GRENADE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "big_grenade"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, BigGrenadeEntity::new)
                    .dimensions(EntityDimensions.changing(0.75F, 0.75F))
                    .build()
    );

    //endregion
    //region Spawn Egg Items

    public static final Item JUMPING_ZOMBIE_SPAWN_EGG = new SpawnEggItem(
            JUMPING_ZOMBIE,
            0x56A25F,
            0xAEEE4E,
            new Item.Settings().group(ItemRegistry.MINECELLS_EGGS)
    );

    public static final Item SHOCKER_SPAWN_EGG = new SpawnEggItem(
            SHOCKER,
            0x2B5369,
            0x5FBED1,
            new Item.Settings().group(ItemRegistry.MINECELLS_EGGS)
    );

    public static final Item GRENADIER_SPAWN_EGG = new SpawnEggItem(
            GRENADIER,
            0x8B3D56,
            0xDB7CDB,
            new Item.Settings().group(ItemRegistry.MINECELLS_EGGS)
    );
    //endregion

    public static void register() {

        // Register Attributes

        FabricDefaultAttributeRegistry.register(JUMPING_ZOMBIE, JumpingZombieEntity.createJumpingZombieAttributes());
        FabricDefaultAttributeRegistry.register(SHOCKER, ShockerEntity.createShockerAttributes());
        FabricDefaultAttributeRegistry.register(GRENADIER, GrenadierEntity.createGrenadierAttributes());

        // Register Spawn Eggs

        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "jumping_zombie_spawn_egg"), JUMPING_ZOMBIE_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "shocker_spawn_egg"), SHOCKER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "grenadier_spawn_egg"), GRENADIER_SPAWN_EGG);
    }
}
