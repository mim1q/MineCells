package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BiomeBannerBlock.BannerPattern;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;

public class MineCellsItemGroups {
  private static final Identifier DISCORD_ICON = MineCells.createId("textures/gui/button/discord.png");
  private static final Identifier KOFI_ICON = MineCells.createId("textures/gui/button/kofi.png");
  private static final Identifier BACKGROUND = MineCells.createId("textures/gui/group.png");
  private static final Identifier TABS = MineCells.createId("textures/gui/tabs.png");

  private static ItemStack stack(ItemConvertible item) {
    return new ItemStack(item);
  }

  private static void generalStacks(ItemGroup.DisplayContext ctx, ItemGroup.Entries stacks) {
    stacks.add(stack(MineCellsItems.PRISON_DOORWAY));
    stacks.addAll(MineCellsBlocks.PRISON_STONE.getStacks());
    stacks.addAll(MineCellsBlocks.PRISON_COBBLESTONE.getStacks());
    stacks.addAll(MineCellsBlocks.PRISON_BRICKS.getStacks());
    stacks.addAll(MineCellsBlocks.CRACKED_PRISON_BRICKS.getStacks());
    stacks.addAll(MineCellsBlocks.SMALL_PRISON_BRICKS.getStacks());
    stacks.add(stack(MineCellsBlocks.WILTED_GRASS_BLOCK));
    stacks.addAll(MineCellsBlocks.PUTRID_WOOD.getStacks());
    stacks.add(stack(MineCellsBlocks.PUTRID_BOARDS));
    stacks.addAll(MineCellsBlocks.PUTRID_BOARD.getStacks());
    stacks.addAll(MineCellsBlocks.WILTED_LEAVES.getStacks());
    stacks.addAll(MineCellsBlocks.ORANGE_WILTED_LEAVES.getStacks());
    stacks.addAll(MineCellsBlocks.RED_WILTED_LEAVES.getStacks());
    stacks.addAll(List.of(
      stack(MineCellsBlocks.RED_PUTRID_SAPLING),
      stack(MineCellsBlocks.CRATE),
      stack(MineCellsBlocks.SMALL_CRATE),
      stack(MineCellsBlocks.BRITTLE_BARREL),
      stack(MineCellsBlocks.KING_STATUE),
      stack(MineCellsBlocks.SKELETON),
      stack(MineCellsBlocks.ROTTING_CORPSE),
      stack(MineCellsBlocks.CORPSE),
      stack(MineCellsBlocks.ELEVATOR_ASSEMBLER),
      stack(MineCellsBlocks.CELL_FORGE),
      stack(MineCellsBlocks.HARDSTONE),
      stack(MineCellsBlocks.CHAIN_PILE_BLOCK),
      stack(MineCellsBlocks.CHAIN_PILE),
      stack(MineCellsBlocks.BIG_CHAIN),
      stack(MineCellsBlocks.CAGE),
      stack(MineCellsBlocks.BROKEN_CAGE),
      stack(MineCellsBlocks.SPIKES),
      stack(MineCellsBlocks.FLAG_POLE),
      MineCellsItems.BIOME_BANNER.stackOf(BannerPattern.KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BannerPattern.TORN_KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BannerPattern.PROMENADE),
      MineCellsItems.BIOME_BANNER.stackOf(BannerPattern.INSUFFERABLE_CRYPT),
      MineCellsItems.BIOME_BANNER.stackOf(BannerPattern.RAMPARTS),
      stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_0),
      stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_1),
      stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_2),
      stack(MineCellsBlocks.PRISON_TORCH),
      stack(MineCellsBlocks.PROMENADE_TORCH),
      stack(MineCellsBlocks.RAMPARTS_TORCH),
      stack(MineCellsItems.SEWAGE_BUCKET),
      stack(MineCellsItems.ANCIENT_SEWAGE_BUCKET),
      stack(MineCellsItems.ELEVATOR_MECHANISM),
      stack(MineCellsItems.GUTS),
      stack(MineCellsItems.MONSTERS_EYE),
      stack(MineCellsItems.HEALTH_FLASK),
      stack(MineCellsItems.BLANK_RUNE),
      stack(MineCellsItems.RESET_RUNE),
      stack(MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE),
      stack(MineCellsItems.VINE_RUNE)
    ));
  }

  private static void toolsAndWeaponsStacks(ItemGroup.DisplayContext ctx, ItemGroup.Entries stacks) {
    stacks.addAll(List.of(
      stack(MineCellsItems.ASSASSINS_DAGGER),
      stack(MineCellsItems.BLOOD_SWORD),
      stack(MineCellsItems.BROADSWORD),
      stack(MineCellsItems.BALANCED_BLADE),
      stack(MineCellsItems.CROWBAR),
      stack(MineCellsItems.NUTCRACKER),
      stack(MineCellsItems.CURSED_SWORD),
      stack(MineCellsItems.HATTORIS_KATANA),
      stack(MineCellsItems.TENTACLE),
      stack(MineCellsItems.FROST_BLAST),
      stack(MineCellsItems.FLINT),
      stack(MineCellsItems.PHASER)
    ));
  }

  private static void spawnEggStacks(ItemGroup.DisplayContext ctx, ItemGroup.Entries stacks) {
    stacks.addAll(MineCellsEntities.getSpawnEggStacks());
  }

  public static final ItemGroupTab GENERAL_TAB = new ItemGroupTab(
    Icon.of(MineCellsBlocks.WILTED_LEAVES.wallLeaves),
    getTabTitle("general"),
    MineCellsItemGroups::generalStacks,
    TABS,
    true
  );

  public static final ItemGroupTab COMBAT_TAB = new ItemGroupTab(
    Icon.of(MineCellsItems.BLOOD_SWORD),
    getTabTitle("combat"),
    MineCellsItemGroups::toolsAndWeaponsStacks,
    TABS,
    true
  );

  public static final ItemGroupTab SPAWN_EGGS_TAB = new ItemGroupTab(
    Icon.of(MineCellsEntities.GRENADIER_SPAWN_EGG),
    getTabTitle("spawn_eggs"),
    MineCellsItemGroups::spawnEggStacks,
    TABS,
    true
  );

  private static Text getTabTitle(String componentName) {
    return Text
      .translatable("itemGroup.minecells.minecells.tab." + componentName)
      .styled(style -> style.withColor(0x46D4FF));
  }

  public static final OwoItemGroup MINECELLS = OwoItemGroup
    .builder(MineCells.createId("minecells"), () -> Icon.of(MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE))
    .customTexture(BACKGROUND)
    .initializer(group -> {
      group.tabs.add(GENERAL_TAB);
      group.tabs.add(COMBAT_TAB);
      group.tabs.add(SPAWN_EGGS_TAB);
      group.addButton(linkButton(Icon.of(Items.BOOK), "wiki", "https://mim1q.dev/minecells"));
      group.addButton(linkButton(Icon.of(DISCORD_ICON, 0, 0, 16, 16), "discord", "https://discord.gg/6TjQbSjbuB"));
      group.addButton(linkButton(Icon.of(KOFI_ICON, 0, 0, 16, 16), "kofi", "https://ko-fi.com/mim1q"));
    })
    .displaySingleTab()
    .build();

  public static void init() {
    MINECELLS.initialize();
  }

  public static ItemGroupButton linkButton(Icon icon, String name, String url) {
    return new ItemGroupButton(MINECELLS, icon, name, TABS, () -> {
      final var client = MinecraftClient.getInstance();
      var screen = client.currentScreen;
      client.setScreen(new ConfirmLinkScreen(confirmed -> {
        if (confirmed) Util.getOperatingSystem().open(url);
        client.setScreen(screen);
      }, url, true));
    });
  }
}
