package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.*;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.BigGrenadeEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.DisgustingWormEggEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.MagicOrbEntity;
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

    public static final EntityType<LeapingZombieEntity> LEAPING_ZOMBIE = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "leaping_zombie"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, LeapingZombieEntity::new)
            .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
            .build()
    );

    public static final EntityType<ShockerEntity> SHOCKER = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "shocker"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShockerEntity::new)
            .dimensions(EntityDimensions.fixed(0.9F, 3.0F))
            .build()
    );

    public static final EntityType<GrenadierEntity> GRENADIER = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "grenadier"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GrenadierEntity::new)
            .dimensions(EntityDimensions.fixed(0.8F, 1.9F))
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
            .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
            .build()
    );

    public static final EntityType<KamikazeEntity> KAMIKAZE = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "kamikaze"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, KamikazeEntity::new)
            .dimensions(EntityDimensions.fixed(0.75F, 0.75F))
            .build()
    );

    public static final EntityType<ProtectorEntity> PROTECTOR = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "protector"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ProtectorEntity::new)
            .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
            .build()
    );

    public static final EntityType<UndeadArcherEntity> UNDEAD_ARCHER = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "undead_archer"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, UndeadArcherEntity::new)
            .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
            .build()
    );

    public static final EntityType<ShieldbearerEntity> SHIELDBEARER = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "shieldbearer"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShieldbearerEntity::new)
            .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
            .build()
    );

    public static final EntityType<MutatedBatEntity> MUTATED_BAT = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "mutated_bat"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MutatedBatEntity::new)
            .dimensions(EntityDimensions.fixed(0.75F, 0.5F))
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

    public static final EntityType<ElevatorEntity> ELEVATOR = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MineCells.MOD_ID, "elevator"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, ElevatorEntity::new)
            .dimensions(EntityDimensions.fixed(2.0F, 0.5F))
            .build()
    );

    //endregion
    //region Spawn Egg Items

    public static final Item LEAPING_ZOMBIE_SPAWN_EGG = new SpawnEggItem(
        LEAPING_ZOMBIE,
        0x5B7B53,
        0x8DBB4E,
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

    public static final Item KAMIKAZE_SPAWN_EGG = new SpawnEggItem(
        KAMIKAZE,
        0x0A6F47,
        0x15FF4E,
        new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item PROTECTOR_SPAWN_EGG = new SpawnEggItem(
        PROTECTOR,
        0xC0861D,
        0x5FBED1,
        new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item UNDEAD_ARCHER_SPAWN_EGG = new SpawnEggItem(
        UNDEAD_ARCHER,
        0x4C854A,
        0x755240,
        new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item SHIELDBEARER_SPAWN_EGG = new SpawnEggItem(
        SHIELDBEARER,
        0x8459AA,
        0xA2A9B6,
        new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    public static final Item MUTATED_BAT_SPAWN_EGG = new SpawnEggItem(
        MUTATED_BAT,
        0xE788E1,
        0xECEDD9,
        new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
    );

    //endregion

    public static void register() {

        // Register Attributes

        FabricDefaultAttributeRegistry.register(LEAPING_ZOMBIE, LeapingZombieEntity.createLeapingZombieAttributes());
        FabricDefaultAttributeRegistry.register(SHOCKER, ShockerEntity.createShockerAttributes());
        FabricDefaultAttributeRegistry.register(GRENADIER, GrenadierEntity.createGrenadierAttributes());
        FabricDefaultAttributeRegistry.register(DISGUSTING_WORM, DisgustingWormEntity.createDisgustingWormAttributes());
        FabricDefaultAttributeRegistry.register(INQUISITOR, InquisitorEntity.createInquisitorAttributes());
        FabricDefaultAttributeRegistry.register(KAMIKAZE, KamikazeEntity.createKamikazeAttributes());
        FabricDefaultAttributeRegistry.register(PROTECTOR, ProtectorEntity.createProtectorAttributes());
        FabricDefaultAttributeRegistry.register(UNDEAD_ARCHER, UndeadArcherEntity.createUndeadArcherAttributes());
        FabricDefaultAttributeRegistry.register(SHIELDBEARER, ShieldbearerEntity.createShieldbearerAttributes());
        FabricDefaultAttributeRegistry.register(MUTATED_BAT, MutatedBatEntity.createMutatedBatAttributes());

        // Register Spawn Eggs

        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "leaping_zombie_spawn_egg"), LEAPING_ZOMBIE_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "shocker_spawn_egg"), SHOCKER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "grenadier_spawn_egg"), GRENADIER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "disgusting_worm_spawn_egg"), DISGUSTING_WORM_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "inquisitor_spawn_egg"), INQUISITOR_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "kamikaze_spawn_egg"), KAMIKAZE_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "protector_spawn_egg"), PROTECTOR_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "undead_archer_spawn_egg"), UNDEAD_ARCHER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "shieldbearer_spawn_egg"), SHIELDBEARER_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "mutated_bat_spawn_egg"), MUTATED_BAT_SPAWN_EGG);
    }
}
