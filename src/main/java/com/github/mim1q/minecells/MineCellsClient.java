package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.ClientConfig;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.item.weapon.bow.LightningBoltItem;
import com.github.mim1q.minecells.item.weapon.interfaces.CritIndicator;
import com.github.mim1q.minecells.network.ClientPacketHandler;
import com.github.mim1q.minecells.registry.MineCellsItemGroups;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import dev.mim1q.gimm1q.client.highlight.HighlightDrawerCallback;
import dev.mim1q.gimm1q.client.highlight.crosshair.CrosshairTipDrawerCallback;
import dev.mim1q.gimm1q.client.item.handheld.HandheldItemModelRegistry;
import dev.mim1q.gimm1q.screenshake.ScreenShakeModifiers;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@Environment(EnvType.CLIENT)
public class MineCellsClient implements ClientModInitializer {
  private static final Identifier CRIT_CROSSHAIR = MineCells.createId("textures/gui/crosshair/crit_indicator.png");

  public static final ClientConfig CLIENT_CONFIG = OmegaConfig.register(ClientConfig.class);

  @Override
  public void onInitializeClient() {
    CLIENT_CONFIG.save();
    MineCellsRenderers.init();
    MineCellsRenderers.initBlocks();
    MineCellsItemGroups.init();
    ClientPacketHandler.init();
    MineCellsParticles.initClient();

    if (CLIENT_CONFIG.keepOriginalGuiModels) setupAllHandheldModels();
    setupShieldHandheldModels();
    if (CLIENT_CONFIG.showCritIndicator) setupCritIndicator();
    setupScreenShakeModifiers();
    loadArrowModels();
    setupTentacleWeaponHighlighting();
    setupLightningBoltHighlighting();
  }

  private void setupTentacleWeaponHighlighting() {
    HighlightDrawerCallback.EVENT.register((drawer, ctx) -> {
      var stack = ctx.player().getMainHandStack();
      if (stack.isOf(MineCellsItems.TENTACLE)) {
        var hitResult = MineCellsItems.TENTACLE.hitResult;
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
          return;
        }
        if (hitResult.getType() == HitResult.Type.ENTITY) {
          drawer.highlightEntity(((EntityHitResult) hitResult).getEntity(), 0x00000000, 0xFFAA25EB);
          return;
        }

        drawer.highlightBlock(((BlockHitResult) hitResult).getBlockPos(), 0x00000000, 0xFFAA25EB);
      }
    });
  }

  private void setupLightningBoltHighlighting() {
    HighlightDrawerCallback.EVENT.register((drawer, ctx) -> {
      var stack = ctx.player().getMainHandStack();
      if (stack.isOf(MineCellsItems.LIGHTNING_BOLT)) {
        var entity = LightningBoltItem.getTargetedEntity(stack, ctx.player().getWorld());
        if (entity != null) {
          var ticks = ctx.player().getItemUseTime();
          var color = 0xFF000000 | LightningBoltItem.getLightningColor(ticks);
          drawer.highlightEntity(entity, 0x00000000, color);
        }
      }
    });
  }

  private void setupCritIndicator() {
    CrosshairTipDrawerCallback.register((drawer, ctx) -> {
      var stack = ctx.player().getMainHandStack();
      if (stack.getItem() instanceof CritIndicator item) {
        var hitResult = MinecraftClient.getInstance().crosshairTarget;
        LivingEntity entity = null;
        if (
          hitResult instanceof EntityHitResult entityHitResult
            && entityHitResult.getEntity() instanceof LivingEntity livingEntity
        ) {
          entity = livingEntity;
        }
        if (item.shouldShowCritIndicator(ctx.player(), entity, stack)) {
          drawer.drawCrosshairTip(12, 0, 16, CRIT_CROSSHAIR);
        }
      }
    });
  }

  private void setupScreenShakeModifiers() {
    // Weapons
    ScreenShakeModifiers.setModifier("minecells:weapon_flint", CLIENT_CONFIG.screenShake.weaponFlint);
    ScreenShakeModifiers.setModifier("minecells:weapon_lightning_bolt", CLIENT_CONFIG.screenShake.weaponLightningBolt);

    // Shields
    ScreenShakeModifiers.setModifier("minecells:shield_block", CLIENT_CONFIG.screenShake.shieldBlock);
    ScreenShakeModifiers.setModifier("minecells:shield_parry", CLIENT_CONFIG.screenShake.shieldParry);

    // Conjunctivius
    ScreenShakeModifiers.setModifier("minecells:conjunctivius_smash", CLIENT_CONFIG.screenShake.conjunctiviusSmash);
    ScreenShakeModifiers.setModifier("minecells:conjunctivius_roar", CLIENT_CONFIG.screenShake.conjunctiviusRoar);
    ScreenShakeModifiers.setModifier("minecells:conjunctivius_death", CLIENT_CONFIG.screenShake.conjunctiviusDeath);

    // Concierge
    ScreenShakeModifiers.setModifier("minecells:concierge_leap", CLIENT_CONFIG.screenShake.conciergeLeap);
    ScreenShakeModifiers.setModifier("minecells:concierge_step", CLIENT_CONFIG.screenShake.conciergeStep);
    ScreenShakeModifiers.setModifier("minecells:concierge_roar", CLIENT_CONFIG.screenShake.conciergeRoar);
    ScreenShakeModifiers.setModifier("minecells:concierge_death", CLIENT_CONFIG.screenShake.conciergeDeath);

    // Explosions
    ScreenShakeModifiers.setModifier("minecells:explosion", CLIENT_CONFIG.screenShake.explosion);
  }

  private void setupAllHandheldModels() {
    setupHandheldModel(MineCellsItems.ASSASSINS_DAGGER);
    setupHandheldModel(MineCellsItems.BROADSWORD);
    setupHandheldModel(MineCellsItems.BALANCED_BLADE);
    setupHandheldModel(MineCellsItems.BLOOD_SWORD);
    setupHandheldModel(MineCellsItems.CROWBAR);
    setupHandheldModel(MineCellsItems.FLINT);
    setupHandheldModel(MineCellsItems.HATTORIS_KATANA);
    setupHandheldModel(MineCellsItems.SPITE_SWORD);
    setupHandheldModel(MineCellsItems.CURSED_SWORD);
    setupHandheldModel(MineCellsItems.PHASER);
    setupHandheldModel(MineCellsItems.FROST_BLAST);
    setupHandheldModel(MineCellsItems.NUTCRACKER);
    setupHandheldModel(MineCellsItems.TENTACLE);
  }

  private void setupHandheldModel(Item weapon) {
    var name = Registries.ITEM.getId(weapon).getPath();
    HandheldItemModelRegistry.getInstance().register(
      weapon,
      MineCells.createId("weapon/" + name),
      MineCells.createId(name)
    );
  }

  private void setupShieldHandheldModels() {
    for (var shield : MineCellsItems.SHIELDS) {
      var name = Registries.ITEM.getId(shield).getPath();
      HandheldItemModelRegistry.getInstance().register(
        shield,
        MineCells.createId(name),
        MineCells.createId("shield_3d/" + name)
      );
    }
  }

  private void loadArrowModels() {
    ModelLoadingPlugin.register(context -> {
      context.addModels(
        CustomArrowType
          .getAllNames()
          .stream()
          .map(it -> MineCells.createId("arrow/" + it))
          .toList()
      );
    });
  }
}
