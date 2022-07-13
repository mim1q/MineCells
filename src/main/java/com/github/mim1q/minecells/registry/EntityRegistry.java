package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.*;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public final class EntityRegistry {

  private static final Set<SpawnEggItem> spawnEggs = new HashSet<>();

  //region EntityTypes

  public static final EntityType<LeapingZombieEntity> LEAPING_ZOMBIE = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("leaping_zombie"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, LeapingZombieEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
      .build()
  );

  public static final EntityType<ShockerEntity> SHOCKER = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("shocker"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShockerEntity::new)
      .dimensions(EntityDimensions.fixed(0.9F, 3.0F))
      .build()
  );

  public static final EntityType<GrenadierEntity> GRENADIER = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("grenadier"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GrenadierEntity::new)
      .dimensions(EntityDimensions.fixed(0.8F, 1.9F))
      .build()
  );

  public static final EntityType<DisgustingWormEntity> DISGUSTING_WORM = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("disgusting_worm"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, DisgustingWormEntity::new)
      .dimensions(EntityDimensions.fixed(0.9F, 0.6F))
      .build()
  );

  public static final EntityType<InquisitorEntity> INQUISITOR = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("inquisitor"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InquisitorEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
      .build()
  );

  public static final EntityType<KamikazeEntity> KAMIKAZE = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("kamikaze"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, KamikazeEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 0.75F))
      .build()
  );

  public static final EntityType<ProtectorEntity> PROTECTOR = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("protector"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ProtectorEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
      .build()
  );

  public static final EntityType<UndeadArcherEntity> UNDEAD_ARCHER = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("undead_archer"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, UndeadArcherEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
      .build()
  );

  public static final EntityType<ShieldbearerEntity> SHIELDBEARER = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("shieldbearer"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShieldbearerEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 1.9F))
      .build()
  );

  public static final EntityType<MutatedBatEntity> MUTATED_BAT = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("mutated_bat"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MutatedBatEntity::new)
      .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
      .build()
  );

  public static final EntityType<SewersTentacleEntity> SEWERS_TENTACLE = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("sewers_tentacle"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SewersTentacleEntity::new)
      .dimensions(EntityDimensions.changing(0.75F, 2.25F))
      .build()
  );

  public static final EntityType<RancidRatEntity> RANCID_RAT = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("rancid_rat"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, RancidRatEntity::new)
      .dimensions(EntityDimensions.fixed(0.5F, 0.75F))
      .build()
  );

  public static final EntityType<RunnerEntity> RUNNER = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("runner"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, RunnerEntity::new)
      .dimensions(EntityDimensions.fixed(0.8F, 2.1F))
      .build()
  );

  public static final EntityType<ScorpionEntity> SCORPION = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("scorpion"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ScorpionEntity::new)
      .dimensions(EntityDimensions.fixed(0.8F, 1.5F))
      .build()
  );

  public static final EntityType<ConjunctiviusEntity> CONJUNCTIVIUS = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("conjunctivius"),
    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ConjunctiviusEntity::new)
      .dimensions(EntityDimensions.fixed(2.5F, 2.5F))
      .build()
  );

  public static final EntityType<GrenadeEntity> GRENADE = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("grenade"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, GrenadeEntity::new)
      .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
      .build()
  );

  public static final EntityType<BigGrenadeEntity> BIG_GRENADE = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("big_grenade"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, BigGrenadeEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 0.75F))
      .build()
  );

  public static final EntityType<DisgustingWormEggEntity> DISGUSTING_WORM_EGG = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("disgusting_worm_egg"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, DisgustingWormEggEntity::new)
      .dimensions(EntityDimensions.fixed(0.375F, 0.375F))
      .build()
  );

  public static final EntityType<MagicOrbEntity> MAGIC_ORB = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("magic_orb"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, MagicOrbEntity::new)
      .dimensions(EntityDimensions.fixed(0.75F, 0.75F))
      .build()
  );

  public static final EntityType<ScorpionSpitEntity> SCORPION_SPIT = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("scorpion_spit"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, ScorpionSpitEntity::new)
      .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
      .build()
  );

  public static final EntityType<ElevatorEntity> ELEVATOR = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("elevator"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, ElevatorEntity::new)
      .dimensions(EntityDimensions.fixed(2.0F, 0.5F))
      .build()
  );

  public static final EntityType<CellEntity> CELL = Registry.register(
    Registry.ENTITY_TYPE,
    MineCells.createId("cell"),
    FabricEntityTypeBuilder.create(SpawnGroup.MISC, CellEntity::new)
      .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
      .build()
  );

  //endregion
  //region Spawn Egg Items

  public static final SpawnEggItem LEAPING_ZOMBIE_SPAWN_EGG = new SpawnEggItem(
    LEAPING_ZOMBIE,
    0x5B7B53,
    0x8DBB4E,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem SHOCKER_SPAWN_EGG = new SpawnEggItem(
    SHOCKER,
    0x2B5369,
    0x5FBED1,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem GRENADIER_SPAWN_EGG = new SpawnEggItem(
    GRENADIER,
    0x8B3D56,
    0xDB7CDB,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem DISGUSTING_WORM_SPAWN_EGG = new SpawnEggItem(
    DISGUSTING_WORM,
    0x67DFCF,
    0xFF44C6,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem INQUISITOR_SPAWN_EGG = new SpawnEggItem(
    INQUISITOR,
    0xFFFFFF,
    0xE52806,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem KAMIKAZE_SPAWN_EGG = new SpawnEggItem(
    KAMIKAZE,
    0x0A6F47,
    0x15FF4E,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem PROTECTOR_SPAWN_EGG = new SpawnEggItem(
    PROTECTOR,
    0xC0861D,
    0x5FBED1,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem UNDEAD_ARCHER_SPAWN_EGG = new SpawnEggItem(
    UNDEAD_ARCHER,
    0x4C854A,
    0x755240,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem SHIELDBEARER_SPAWN_EGG = new SpawnEggItem(
    SHIELDBEARER,
    0x8459AA,
    0xA2A9B6,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem MUTATED_BAT_SPAWN_EGG = new SpawnEggItem(
    MUTATED_BAT,
    0xD279D2,
    0xD33D3D,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem SEWERS_TENTACLE_SPAWN_EGG = new SpawnEggItem(
    SEWERS_TENTACLE,
    0x3983B9,
    0xFFF0C6,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem RANCID_RAT_SPAWN_EGG = new SpawnEggItem(
    RANCID_RAT,
    0x68607C,
    0xF17E5D,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem RUNNER_SPAWN_EGG = new SpawnEggItem(
    RUNNER,
    0xE43E2C,
    0xF9F9F9,
    new Item.Settings().group(ItemGroupRegistry.MINECELLS_EGGS)
  );

  public static final SpawnEggItem SCORPION_SPAWN_EGG = new SpawnEggItem(
    SCORPION,
    0x6DBCD5,
    0x4B3A5B,
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
    FabricDefaultAttributeRegistry.register(SEWERS_TENTACLE, SewersTentacleEntity.createSewersTentacleAttributes());
    FabricDefaultAttributeRegistry.register(RANCID_RAT, RancidRatEntity.createRancidRatAttributes());
    FabricDefaultAttributeRegistry.register(RUNNER, RunnerEntity.createRunnerAttributes());
    FabricDefaultAttributeRegistry.register(SCORPION, ScorpionEntity.createScorpionAttributes());
    FabricDefaultAttributeRegistry.register(CONJUNCTIVIUS, ConjunctiviusEntity.createConjunctiviusAttributes());

    // Register Spawn Eggs

    registerSpawnEgg("leaping_zombie", LEAPING_ZOMBIE_SPAWN_EGG);
    registerSpawnEgg("shocker", SHOCKER_SPAWN_EGG);
    registerSpawnEgg("grenadier", GRENADIER_SPAWN_EGG);
    registerSpawnEgg("disgusting_worm", DISGUSTING_WORM_SPAWN_EGG);
    registerSpawnEgg("inquisitor", INQUISITOR_SPAWN_EGG);
    registerSpawnEgg("kamikaze", KAMIKAZE_SPAWN_EGG);
    registerSpawnEgg("protector", PROTECTOR_SPAWN_EGG);
    registerSpawnEgg("undead_archer", UNDEAD_ARCHER_SPAWN_EGG);
    registerSpawnEgg("shieldbearer", SHIELDBEARER_SPAWN_EGG);
    registerSpawnEgg("mutated_bat", MUTATED_BAT_SPAWN_EGG);
    registerSpawnEgg("sewers_tentacle", SEWERS_TENTACLE_SPAWN_EGG);
    registerSpawnEgg("rancid_rat", RANCID_RAT_SPAWN_EGG);
    registerSpawnEgg("runner", RUNNER_SPAWN_EGG);
    registerSpawnEgg("scorpion", SCORPION_SPAWN_EGG);
  }

  public static void registerSpawnEgg(String entityName, SpawnEggItem item) {
    spawnEggs.add(item);
    Registry.register(Registry.ITEM, MineCells.createId(entityName + "_spawn_egg"), item);
  }

  public static Set<SpawnEggItem> getSpawnEggs() {
    return spawnEggs;
  }
}
