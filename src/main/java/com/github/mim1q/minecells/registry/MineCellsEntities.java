package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.*;
import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.nonliving.*;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ConjunctiviusObeliskEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public final class MineCellsEntities {
  public static final List<SpawnEggItem> SPAWN_EGGS = new ArrayList<>();

  public static final EntityType<LeapingZombieEntity> LEAPING_ZOMBIE = registerEntity("leaping_zombie", SpawnGroup.MONSTER, LeapingZombieEntity::new, 0.75F, 1.9F);
  public static final EntityType<ShockerEntity> SHOCKER = registerEntity("shocker", SpawnGroup.MONSTER, ShockerEntity::new, 0.9F, 3.0F);
  public static final EntityType<GrenadierEntity> GRENADIER = registerEntity("grenadier", SpawnGroup.MONSTER, GrenadierEntity::new, 0.8F, 1.9F);
  public static final EntityType<DisgustingWormEntity> DISGUSTING_WORM = registerEntity("disgusting_worm", SpawnGroup.MONSTER, DisgustingWormEntity::new, 0.9F, 0.6F);
  public static final EntityType<InquisitorEntity> INQUISITOR = registerEntity("inquisitor", SpawnGroup.MONSTER, InquisitorEntity::new, 0.75F, 1.9F);
  public static final EntityType<KamikazeEntity> KAMIKAZE = registerEntity("kamikaze", SpawnGroup.MONSTER, KamikazeEntity::new, 0.75F, 0.75F);
  public static final EntityType<ProtectorEntity> PROTECTOR = registerEntity("protector", SpawnGroup.MONSTER, ProtectorEntity::new, 0.75F, 1.9F);
  public static final EntityType<UndeadArcherEntity> UNDEAD_ARCHER = registerEntity("undead_archer", SpawnGroup.MONSTER, UndeadArcherEntity::new, 0.75F, 1.9F);
  public static final EntityType<ShieldbearerEntity> SHIELDBEARER = registerEntity("shieldbearer", SpawnGroup.MONSTER, ShieldbearerEntity::new, 0.75F, 1.9F);
  public static final EntityType<MutatedBatEntity> MUTATED_BAT = registerEntity("mutated_bat", SpawnGroup.MONSTER, MutatedBatEntity::new, 0.9F, 0.9F);
  public static final EntityType<SewersTentacleEntity> SEWERS_TENTACLE = registerEntity("sewers_tentacle", SpawnGroup.MONSTER, SewersTentacleEntity::new, EntityDimensions.changing(0.75F, 2.25F));
  public static final EntityType<RancidRatEntity> RANCID_RAT = registerEntity("rancid_rat", SpawnGroup.MONSTER, RancidRatEntity::new, 0.5F, 0.75F);
  public static final EntityType<RunnerEntity> RUNNER = registerEntity("runner", SpawnGroup.MONSTER, RunnerEntity::new,0.8F, 2.1F);
  public static final EntityType<ScorpionEntity> SCORPION = registerEntity("scorpion", SpawnGroup.MONSTER, ScorpionEntity::new, 0.8F, 1.5F);
  public static final EntityType<FlyEntity> BUZZCUTTER = registerEntity("buzzcutter", SpawnGroup.MONSTER, FlyEntity::new, 0.75F, 0.75F);
  public static final EntityType<SweeperEntity> SWEEPER = registerEntity("sweeper", SpawnGroup.MONSTER, SweeperEntity::new, 0.9F, 1.6F);
  public static final EntityType<ConjunctiviusEntity> CONJUNCTIVIUS = registerEntity("conjunctivius", SpawnGroup.MONSTER, ConjunctiviusEntity::new, 5.0F, 5.0F);
  public static final EntityType<ConciergeEntity> CONCIERGE = registerEntity("concierge", SpawnGroup.MONSTER, ConciergeEntity::new, 1.8F, 3.5F);
  public static final EntityType<GrenadeEntity> GRENADE = registerEntity("grenade", SpawnGroup.MISC, GrenadeEntity::new, 0.5F, 0.5F);
  public static final EntityType<BigGrenadeEntity> BIG_GRENADE = registerEntity("big_grenade", SpawnGroup.MISC, BigGrenadeEntity::new, 0.75F, 0.75F);
  public static final EntityType<DisgustingWormEggEntity> DISGUSTING_WORM_EGG = registerEntity("disgusting_worm_egg", SpawnGroup.MISC, DisgustingWormEggEntity::new, 0.375F, 0.375F);
  public static final EntityType<MagicOrbEntity> MAGIC_ORB = registerEntity("magic_orb", SpawnGroup.MISC, MagicOrbEntity::new, 0.75F, 0.75F);
  public static final EntityType<ScorpionSpitEntity> SCORPION_SPIT = registerEntity("scorpion_spit", SpawnGroup.MISC, ScorpionSpitEntity::new, 0.5F, 0.5F);
  public static final EntityType<ConjunctiviusProjectileEntity> CONJUNCTIVIUS_PROJECTILE = registerEntity("conjunctivius_projectile", SpawnGroup.MISC, ConjunctiviusProjectileEntity::new, 0.5F, 0.5F);
  public static final EntityType<ElevatorEntity> ELEVATOR = registerEntity("elevator", SpawnGroup.MISC, ElevatorEntity::new, 2.0F, 0.5F);
  public static final EntityType<CellEntity> CELL = registerEntity("cell", SpawnGroup.MISC, CellEntity::new, 0.5F, 0.5F);
  public static final EntityType<TentacleWeaponEntity> TENTACLE_WEAPON = registerEntity("tentacle_weapon", SpawnGroup.MISC, TentacleWeaponEntity::new, 0.1F, 0.1F);
  public static final EntityType<ConjunctiviusObeliskEntity> CONJUNCTIVIUS_OBELISK = registerEntity("conjunctivius_obelisk", SpawnGroup.MISC, ConjunctiviusObeliskEntity::new, EntityDimensions.changing(1.75F, 2.5F)  );
  public static final EntityType<SpawnerRuneEntity> SPAWNER_RUNE = registerEntity("spawner_rune", SpawnGroup.MISC, SpawnerRuneEntity::new, EntityDimensions.changing(0.5F, 0.5F)  );
  public static final EntityType<ShockwavePlacer> SHOCKWAVE_PLACER = registerEntity("shockwave_placer", SpawnGroup.MISC, ShockwavePlacer::new, 0.1F, 0.1F);

  public static final SpawnEggItem LEAPING_ZOMBIE_SPAWN_EGG = registerSpawnEgg(LEAPING_ZOMBIE, 0x5B7B53, 0x8DBB4E);
  public static final SpawnEggItem SHOCKER_SPAWN_EGG = registerSpawnEgg(SHOCKER, 0x2B5369, 0x5FBED1);
  public static final SpawnEggItem GRENADIER_SPAWN_EGG = registerSpawnEgg(GRENADIER, 0x8B3D56, 0xDB7CDB);
  public static final SpawnEggItem DISGUSTING_WORM_SPAWN_EGG = registerSpawnEgg(DISGUSTING_WORM, 0x67DFCF, 0xFF44C6);
  public static final SpawnEggItem INQUISITOR_SPAWN_EGG = registerSpawnEgg(INQUISITOR, 0xFFFFFF, 0xE52806);
  public static final SpawnEggItem KAMIKAZE_SPAWN_EGG = registerSpawnEgg(KAMIKAZE, 0x0A6F47, 0x15FF4E);
  public static final SpawnEggItem PROTECTOR_SPAWN_EGG = registerSpawnEgg(PROTECTOR, 0xC0861D, 0x5FBED1);
  public static final SpawnEggItem UNDEAD_ARCHER_SPAWN_EGG = registerSpawnEgg(UNDEAD_ARCHER, 0x4C854A, 0x755240);
  public static final SpawnEggItem SHIELDBEARER_SPAWN_EGG = registerSpawnEgg(SHIELDBEARER, 0x8459AA, 0xA2A9B6);
  public static final SpawnEggItem MUTATED_BAT_SPAWN_EGG = registerSpawnEgg(MUTATED_BAT, 0xD279D2, 0xD33D3D);
  public static final SpawnEggItem SEWERS_TENTACLE_SPAWN_EGG = registerSpawnEgg(SEWERS_TENTACLE, 0x3983B9, 0xFFF0C6);
  public static final SpawnEggItem RANCID_RAT_SPAWN_EGG = registerSpawnEgg(RANCID_RAT, 0x68607C, 0xF17E5D);
  public static final SpawnEggItem RUNNER_SPAWN_EGG = registerSpawnEgg(RUNNER, 0xE43E2C, 0xF9F9F9);
  public static final SpawnEggItem SCORPION_SPAWN_EGG = registerSpawnEgg(SCORPION, 0x6DBCD5, 0x4B3A5B);
  public static final SpawnEggItem BUZZCUTTER_SPAWN_EGG = registerSpawnEgg(BUZZCUTTER, 0xEB1F51, 0xFFCC00);
  public static final SpawnEggItem SWEEPER_SPAWN_EGG = registerSpawnEgg(SWEEPER, 0x5C73BF, 0xFFCC00);

  public static void init() {
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
    FabricDefaultAttributeRegistry.register(BUZZCUTTER, FlyEntity.createFlyAttributes());
    FabricDefaultAttributeRegistry.register(SWEEPER, SweeperEntity.createSweeperAttributes());
    FabricDefaultAttributeRegistry.register(CONJUNCTIVIUS, ConjunctiviusEntity.createConjunctiviusAttributes());
    FabricDefaultAttributeRegistry.register(CONCIERGE, ConciergeEntity.createConciergeAttributes());
  }

  private static SpawnEggItem registerSpawnEgg(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor) {
    var egg = Registry.register(
      Registries.ITEM,
      MineCells.createId(EntityType.getId(type).getPath() + "_spawn_egg"),
      new SpawnEggItem(type, 0xFFFFFF, 0xFFFFFF, new FabricItemSettings())
    );
    SPAWN_EGGS.add(egg);
    return egg;
  }

  private static <T extends Entity> EntityType<T> registerEntity(String name, SpawnGroup spawnGroup, EntityType.EntityFactory<T> factory, EntityDimensions dimensions) {
    return Registry.register(
      Registries.ENTITY_TYPE,
      MineCells.createId(name),
      FabricEntityTypeBuilder.create(spawnGroup, factory).dimensions(dimensions).build()
    );
  }

  private static <T extends Entity> EntityType<T> registerEntity(String name, SpawnGroup spawnGroup, EntityType.EntityFactory<T> factory, float width, float height) {
    return registerEntity(name, spawnGroup, factory, EntityDimensions.fixed(width, height));
  }

  public static List<ItemStack> getSpawnEggStacks() {
    return SPAWN_EGGS.stream().map(Item::getDefaultStack).toList();
  }
}
