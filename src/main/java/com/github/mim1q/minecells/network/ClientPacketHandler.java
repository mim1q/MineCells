package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.block.ShockwaveBlock;
import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.client.gui.ConjunctiviusClientBossBar;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;
import com.github.mim1q.minecells.network.s2c.*;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.screen.ScreenUtils;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterRecipeList;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreen;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import io.wispforest.owo.network.ClientAccess;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {
  public static final OwoNetChannel CHANNEL = ServerPacketHandler.CHANNEL;

  public static void init() {
    CHANNEL.registerClientbound(
      SpawnerRuneUpdateS2CPacket.class,
      ClientPacketHandler::handleSpawnerRuneUpdate
    );

    CHANNEL.registerClientbound(
      OpenDoorwayScreenS2CPacket.class,
      (msg, handler) -> {
        ScreenUtils.openDoorwaySelectionScreen(msg.pos(), msg.posOverride());
      }
    );

    CHANNEL.registerClientbound(
      SpawnRuneParticlesS2CPacket.class,
      ClientPacketHandler::applySpawnRuneParticles
    );

    CHANNEL.registerClientbound(
      ObeliskActivationS2CPacket.class,
      ClientPacketHandler::applyObeliskActivation
    );

    CHANNEL.registerClientbound(
      ShockwaveClientEventS2CPacket.class,
      ClientPacketHandler::applyShockwaveClientEvent
    );

    CHANNEL.registerClientbound(
      SendUnlockedCellCrafterRecipesS2CPacket.class,
      ClientPacketHandler::applySendUnlockedCellCrafterRecipes
    );

    CHANNEL.registerClientbound(
      UpdateConjunctiviusBossBarS2CPacket.class,
      ClientPacketHandler::handleUpdateConjunctiviusBossBar
    );

    CHANNEL.registerClientbound(
      CritS2CPacket.class,
      ClientPacketHandler::handleCrit
    );

    CHANNEL.registerClientbound(
      ExplosionS2CPacket.class,
      ClientPacketHandler::handleExplosion
    );
  }

  private static void handleCrit(CritS2CPacket msg, ClientAccess handler) {
    Vec3d pos = msg.pos();
    handler.runtime().execute(() -> {
      if (handler.runtime().player != null) {
        ParticleUtils.addAura(handler.runtime().world, pos, ParticleTypes.CRIT, 8, 0.0D, 1.0D);
      }
    });
  }

  private static void handleExplosion(ExplosionS2CPacket msg, ClientAccess handler) {
    Vec3d pos = msg.pos();
    double radius = msg.radius();
    var client = handler.runtime();
    client.execute(() -> {
      if (client.player != null && client.world != null) {
        client.world.addParticle(MineCellsParticles.EXPLOSION, true, pos.x, pos.y, pos.z, 0, 0, 0);
        client.world.addParticle(ParticleTypes.EXPLOSION, true, pos.x + 0.01, pos.y + 0.01, pos.z + 0.01, 0, 0, 0);
        var random = client.world.random;
        for (int i = 0; i < 20; ++i) {
          double vx = (random.nextDouble() - 0.5) * radius;
          double vy = (random.nextDouble() - 0.5) * radius;
          double vz = (random.nextDouble() - 0.5) * radius;
          client.world.addParticle(ParticleTypes.CRIT, true, pos.x, pos.y, pos.z, vx, vy, vz);
        }
      }
    });
  }

  private static void handleConnect(ConnectS2CPacket msg, ClientAccess handler) {
    Vec3d pos0 = msg.pos0();
    Vec3d pos1 = msg.pos1();
    var client = handler.runtime();
    client.execute(() -> {
      if (client.player != null && client.world != null) {
        double amount = pos0.distanceTo(pos1);
        Vec3d vel = pos1.subtract(pos0).normalize();
        for (int i = 0; i < amount; i++) {
          Vec3d pos = MathUtils.lerp(pos0, pos1, i / (float) amount);
          ParticleUtils.addParticle(client.world, ParticleTypes.ENCHANTED_HIT, pos, vel);
        }
      }
    });
  }

  private static void handleElevatorDestroyed(ElevatorDestroyedS2CPacket msg, ClientAccess handler) {
    Vec3d pos = msg.pos();
    var client = handler.runtime();
    client.execute(() -> {
      if (client.world != null) {
        ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState());
        Box box = new Box(pos.add(-1.0D, 0.0D, -1.0D), pos.add(1.0D, 0.5D, 1.0D));
        ParticleUtils.addInBox(client.world, particle, box, 25, new Vec3d(0.1D, 0.1D, 0.1D));
      }
    });
  }

  private static void handleUpdateConjunctiviusBossBar(UpdateConjunctiviusBossBarS2CPacket msg, ClientAccess handler) {
    var barUuid = msg.uuid();
    var tentacleCount = msg.tentacleCount();
    var maxTentacleCount = msg.maxTentacleCount();

    handler.runtime().execute(() -> {
      var bar = handler.runtime().inGameHud.getBossBarHud().bossBars.get(barUuid);
      if (bar instanceof ConjunctiviusClientBossBar conjunctiviusBar) {
        conjunctiviusBar.setTentacleCount(tentacleCount, maxTentacleCount);
      }
    });
  }

  public static void applySpawnRuneParticles(SpawnRuneParticlesS2CPacket msg, ClientAccess handler) {
    double minX = msg.minX();
    double minY = msg.minY();
    double minZ = msg.minZ();
    double maxX = msg.maxX();
    double maxY = msg.maxY();
    double maxZ = msg.maxZ();

    Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
    handler.runtime().execute(() -> {
      ClientWorld world = handler.player().clientWorld;
      var color = 0xFF6A00;
      var dimension = MineCellsDimension.of(world);
      if (dimension != null) {
        color = dimension.getColor();
      }
      ParticleUtils.addInBox(
        world,
        MineCellsParticles.SPECKLE.get(color),
        box,
        10,
        new Vec3d(-0.1D, -0.1D, -0.1D).multiply(world.random.nextDouble() * 0.5D + 0.5D)
      );
      ParticleUtils.addInBox(
        world,
        ParticleTypes.CLOUD,
        box.shrink(0.1D, 0.1D, 0.1D),
        10,
        new Vec3d(-0.02D, -0.02D, -0.02D).multiply(world.random.nextDouble() * 0.5D + 0.5D)
      );
    });
  }

  public static void applyObeliskActivation(ObeliskActivationS2CPacket msg, ClientAccess handler) {
    int entityId = msg.entityId();
    handler.runtime().execute(() -> {
      Entity entity = handler.player().clientWorld.getEntityById(entityId);
      if (entity instanceof ObeliskEntity obelisk) {
        obelisk.resetActivatedTicks();
      }
    });
  }

  public static void applyShockwaveClientEvent(ShockwaveClientEventS2CPacket msg, ClientAccess handler) {
    var blockId = msg.blockId();
    var blockPos = msg.pos();
    var end = msg.end();
    handler.runtime().execute(() -> {
      var world = handler.runtime().world;
      if (world == null) {
        return;
      }
      var block = Registries.BLOCK.getEntry(blockId);
      if (
        block.isPresent() &&
          block.get().value() instanceof ShockwaveBlock shockwaveBlock
      ) {
        if (end) {
          shockwaveBlock.onClientEndShockwave(world, blockPos);
        } else {
          shockwaveBlock.onClientStartShockwave(world, blockPos);
        }
      }
    });
  }

  public static void applySendUnlockedCellCrafterRecipes(SendUnlockedCellCrafterRecipesS2CPacket msg, ClientAccess handler) {
    var recipes = msg.requiredAdvancements();
    var recipeManager = handler.netHandler().getRecipeManager();

    handler.runtime().execute(() -> {
      var screen = handler.runtime().currentScreen;
      if (screen instanceof CellCrafterScreen cellCrafterScreen) {
        cellCrafterScreen.updateRecipes(recipes.entrySet().stream().map(entry -> {
            var recipe = (CellForgeRecipe) recipeManager.get(entry.getKey()).get().value();
            recipe = recipe.withId(entry.getKey());
            return new CellCrafterRecipeList.DisplayedRecipe(recipe, entry.getValue());
          }
        ).toList());
      }
    });
  }

  private static void handleSpawnerRuneUpdate(SpawnerRuneUpdateS2CPacket msg, ClientAccess handler) {
    var world = handler.netHandler().getWorld();
    var blockEntity = world.getBlockEntity(msg.pos());
    if (blockEntity instanceof SpawnerRuneBlockEntity spawner) {
      spawner.controller.setLastActivationTime(msg.lastActivationTime());
      spawner.controller.setDummyData(msg.cooldown());
      return;
    }
    var box = Box.of(Vec3d.ofCenter(msg.pos()), 1.5, 1.5, 1.5);
    var entity = world.getEntitiesByClass(
      SpawnerRuneEntity.class,
      box,
      it -> it.getBlockPos().equals(msg.pos())
    ).stream().findFirst();

    entity.ifPresent(it -> {
      it.controller.setLastActivationTime(msg.lastActivationTime());
      it.controller.setDummyData(msg.cooldown());
    });
  }
}
