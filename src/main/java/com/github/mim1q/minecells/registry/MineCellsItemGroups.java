package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BiomeBannerBlock;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class MineCellsItemGroups {
  private static final Identifier DISCORD_ICON = MineCells.createId("textures/gui/button/discord.png");
  private static final Identifier KOFI_ICON = MineCells.createId("textures/gui/button/kofi.png");

  private static ItemStack stack(ItemConvertible item) {
    return new ItemStack(item);
  }

  private static void mineCellsStacks(DefaultedList<ItemStack> stacks) {
    stacks.add(stack(MineCellsItems.PRISON_DOORWAY));
    stacks.addAll(MineCellsBlocks.PRISON_STONE.getStacks());
    stacks.addAll(MineCellsBlocks.PRISON_COBBLESTONE.getStacks());
    stacks.addAll(MineCellsBlocks.PRISON_BRICKS.getStacks());
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
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.TORN_KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.PROMENADE),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.INSUFFERABLE_CRYPT),
      stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_0),
      stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_1),
      stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_2),
      stack(MineCellsBlocks.PRISON_TORCH),
      stack(MineCellsBlocks.PROMENADE_TORCH),
      stack(MineCellsItems.SEWAGE_BUCKET),
      stack(MineCellsItems.ANCIENT_SEWAGE_BUCKET),
      stack(MineCellsItems.ELEVATOR_MECHANISM),
      stack(MineCellsItems.GUTS),
      stack(MineCellsItems.MONSTERS_EYE),
      stack(MineCellsItems.HEALTH_FLASK),
      stack(MineCellsItems.BLANK_RUNE),
      stack(MineCellsItems.RESET_RUNE),
      stack(MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE),
      stack(MineCellsItems.VINE_RUNE),
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
      stack(MineCellsItems.PHASER)
    ));
  }

  public static final OwoItemGroup MINECELLS = OwoItemGroup
    .builder(MineCells.createId("minecells"), MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE::getDefaultStack)
    .displaySingleTab()
    .build();

  public static final ItemGroupTab MINECELLS_TAB = new ItemGroupTab(
    Icon.of(MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE),
    OwoItemGroup.ButtonDefinition.tooltipFor(MINECELLS, "tab", "minecells"),
    MineCellsItemGroups::mineCellsStacks,
    ItemGroupTab.DEFAULT_TEXTURE,
    true
  );

  public static ItemGroup MINECELLS_DEVELOPMENT = null;

  public static void init() {
    MINECELLS.addButton(ItemGroupButton.link(Icon.of(Items.BOOK), "wiki", "https://mim1q.dev/minecells"));
    MINECELLS.addButton(ItemGroupButton.link(Icon.of(DISCORD_ICON, 0, 0, 16, 16), "discord", "https://discord.gg/6TjQbSjbuB"));
    MINECELLS.addButton(ItemGroupButton.link(Icon.of(KOFI_ICON, 0, 0, 16, 16), "kofi", "https://ko-fi.com/mim1q"));
    MINECELLS.tabs.add(MINECELLS_TAB);
    MINECELLS.initialize();
    if (MineCells.COMMON_CONFIG.items.enableDevelopmentTab) {
      MINECELLS_DEVELOPMENT = FabricItemGroupBuilder.create(MineCells.createId("development"))
        .icon(() -> new ItemStack(Blocks.BARRIER))
        .appendItems(stacks -> stacks.addAll(List.of(
          stack(Items.DEBUG_STICK),
          stack(Items.WOODEN_AXE),
          stack(Items.JIGSAW),
          stack(Items.STRUCTURE_BLOCK),
          stack(Items.STRUCTURE_VOID),
          stack(Items.BARRIER),
          stack(MineCellsBlocks.BARRIER_RUNE)
        ))).build();
    }
  }
}
