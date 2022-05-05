package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.*;
import com.github.mim1q.minecells.entity.projectile.BigGrenadeEntity;
import com.github.mim1q.minecells.entity.projectile.DisgustingWormEggEntity;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import com.github.mim1q.minecells.entity.projectile.MagicOrbEntity;
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
            .dimensions(EntityDimensions.fixed(0.75F, 2.0F))
            .build()
    );

    public static final EntityType<ShockerEntity> SHOCKER = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "shocker"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShockerEntity::new)
            .dimensions(EntityDimensions.fixed(1.0F, 3.0F))
            .build()
    );

    public static final EntityType<GrenadierEntity> GRENADIER = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "grenadier"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GrenadierEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8F, 2.0F))
                    .build()
    );

    public static final EntityType<DisgustingWormEntity> DISGUSTING_WORM = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "disgusting_worm"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DisgustingWormEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9F, 0.6F))
                    .build()
    );

    public static final EntityType<InquisitorEntity> INQUISITOR = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "inquisitor"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InquisitorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75F, 2.0F))
                    .build()
    );

    public static final EntityType<GrenadeEntity> GRENADE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "grenade"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, GrenadeEntity::new)
                .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                .build()
    );

    public static final EntityType<BigGrenadeEntity> BIG_GRENADE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "big_grenade"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, BigGrenadeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75F, 0.75F))
                    .build()
    );

    public static final EntityType<DisgustingWormEggEntity> DISGUSTING_WORM_EGG = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "disgusting_worm_egg"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, DisgustingWormEggEntity::new)
                    .dimensions(EntityDimensions.fixed(0.375F, 0.375F))
                    .build()
    );

    public static final EntityType<MagicOrbEntity> MAGIC_ORB = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "magic_orb"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, MagicOrbEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75F, 0.75F))
                    .build()
    );

    //endregion
    //region Spawn Egg Items

    public static final Item JUMPING_ZOMBIE_SPAWN_EGG = new SpawnEggItem(
            JUMPING_ZOMBIE,
            0x56A25F,
            0xAEEE4E,
            new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item SHOCKER_SPAWN_EGG = new SpawnEggItem(
            SHOCKER,
            0x2B5369,
            0x5FBED1,
            new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item GRENADIER_SPAWN_EGG = new SpawnEggItem(
            GRENADIER,
            0x8B3D56,
            0xDB7CDB,
            new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item DISGUSTING_WORM_SPAWN_EGG = new SpawnEggItem(
            DISGUSTING_WORM,
            0x67DFCF,
            0xFF44C6,
            new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item INQUISITOR_SPAWN_EGG = new SpawnEggItem(
            INQUISITOR,
            0xFFFFFF,
            0xE52806,
            new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );
    //endregion

    public static void register() {

        // Register Attributes

        FabricDefaultAttributeRegistry.register(JUMPING_ZOMBIE, JumpingZombieEntity.createJumpingZombieAttributes());
        FabricDefaultAttributeRegistry.register(SHOCKER, ShockerEntity.createShockerAttributes());
        FabricDefaultAttributeRegistry.register(GRENADIER, GrenadierEntity.createGrenadierAttributes());
        FabricDefaultAttributeRegistry.register(DISGUSTING_WORM, DisgustingWormEntity.createDisgustingWormAttributes());
        FabricDefaultAttributeRegistry.register(INQUISITOR, InquisitorEntity.createInquisitorAttributes());

        // Register Spawn Eggs

        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "jumping_zombie_spawn_egg"), JUMPING_ZOMBIE_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "shocker_spawn_egg"), SHOCKER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "grenadier_spawn_egg"), GRENADIER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "disgusting_worm_spawn_egg"), DISGUSTING_WORM_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "inquisitor_spawn_egg"), INQUISITOR_SPAWN_EGG);
    }
}
