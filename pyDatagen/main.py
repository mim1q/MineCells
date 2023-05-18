import sys

import util
from autolang.autolang import get_entries_from_directory, autolang, write_autolang_file
from presets import block_set_presets, common_presets, wood_presets
from presets.preset_generator import PresetGenerator
from template import Template, TemplateType


def generate_data(output_path: str):
    # TEMPLATES AND PRESETS ============================================================================================
    gen = PresetGenerator("minecells", output_path)

    # Block Sets
    gen.generate_preset(block_set_presets.StoneBlockSet("small_prison_bricks", "small_prison_brick"))
    gen.generate_preset(block_set_presets.StoneBlockSet("prison_bricks", "prison_brick"))
    gen.generate_preset(block_set_presets.StoneBlockSet("prison_stone"))
    gen.generate_preset(block_set_presets.StoneBlockSet("prison_cobblestone"))
    gen.generate_preset(block_set_presets.WoodBlockSet("putrid_planks", "putrid"))
    gen.generate_preset(block_set_presets.CommonBlockSet("putrid_board_block", "putrid_board"))

    # Misc Blocks
    def simple_item_and_drop(name: str):
        gen.generate_preset(common_presets.ItemBlockModel(name))
        gen.generate_preset(common_presets.SimpleDrop(name))

    gen.generate_preset(common_presets.GeneratedItemModel("king_statue"))
    gen.generate_preset(common_presets.GeneratedItemModel("barrier_rune"))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "single_state", "wilted_grass_block", {"block": "wilted_grass_block"}))
    simple_item_and_drop("wilted_grass_block")
    simple_item_and_drop("flag_pole")

    ## Torches
    for torch in ["promenade", "prison"]:
      gen.generate_template(Template(TemplateType.ITEM_MODEL, "block_model", f"{torch}_torch", {"block": f"colored_torch/{torch}_standing"}))

    # Leaves
    gen.generate_preset(wood_presets.Leaves("wilted", True))
    gen.generate_preset(wood_presets.Leaves("orange_wilted"))
    gen.generate_preset(wood_presets.Leaves("red_wilted"))
    gen.generate_template(Template(TemplateType.BLOCK_MODEL, "cross", "runic_vine", {"texture": "runic_vine"}))
    gen.generate_template(Template(TemplateType.BLOCK_MODEL, "cross", "runic_vine_top", {"texture": "runic_vine_top"}))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "single_state", "runic_vine_plant", {"block": "runic_vine_plant"}))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "single_state", "runic_vine_stone", {"block": "runic_vine_stone"}))
    gen.generate_template(Template(TemplateType.BLOCK_MODEL, "cube_all", "runic_vine_stone", {"texture": "runic_vine_stone"}))

    # Corpses
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "rotating_horizontal", "hanged_skeleton", {"block": "hanged_skeleton"}))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "rotating_horizontal", "skeleton", {"block": "skeleton"}))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "rotating_horizontal", "hanged_corpse", {"block": "hanged_corpse"}))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "rotating_horizontal", "corpse", {"block": "corpse"}))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "rotating_horizontal", "hanged_rotting_corpse", {"block": "hanged_rotting_corpse"}))
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "rotating_horizontal", "rotting_corpse", {"block": "rotting_corpse"}))
    util.generate_block_items(gen, ["skeleton", "rotting_corpse", "corpse"])

    # Other
    gen.generate_template(Template(TemplateType.BLOCKSTATE, "rotating_horizontal", "return_stone", {"block": "return_stone"}))
    gen.generate_preset(common_presets.ItemBlockModel("return_stone"))

    # Custom Drops
    util.generate_self_dropping_blocks(gen, ["prison_torch", "promenade_torch", "putrid_boards", "crate", "small_crate", "brittle_barrel"])
    gen.generate_template(Template(TemplateType.BLOCK_LOOT_TABLE, "silk_touch_simple_drop", "prison_stone",
                                   {"item": "prison_cobblestone", "silk_touch_item": "prison_stone"}))

    # Custom Recipes
    gen.generate_template(Template(TemplateType.RECIPE, "smelting", "prison_cobblestone",
                                   {"input": "prison_cobblestone", "output": "prison_stone"}))
    gen.generate_template(Template(TemplateType.RECIPE, "packed_2x2", "prison_bricks",
                                   {"input": "prison_stone", "output": "prison_bricks", "count": 4}))
    gen.generate_template(Template(TemplateType.RECIPE, "packed_2x2", "small_prison_bricks",
                                   {"input": "prison_bricks", "output": "small_prison_bricks", "count": 4}))
    gen.generate_template(Template(TemplateType.RECIPE, "stonecutting", "prison_bricks_stonecutting",
                                   {"input": "prison_stone", "output": "prison_bricks", "count": 1}))
    gen.generate_template(Template(TemplateType.RECIPE, "stonecutting", "small_prison_bricks_stonecutting",
                                   {"input": "prison_bricks", "output": "small_prison_bricks", "count": 1}))
    gen.generate_template(
        Template(TemplateType.RECIPE, "stonecutting", "small_prison_bricks_stonecutting_from_prison_stone",
                 {"input": "prison_stone", "output": "small_prison_bricks", "count": 1}))

    # AUTOLANG =========================================================================================================

    block_autolang = autolang(get_entries_from_directory(output_path + "\\assets\\minecells\\blockstates\\"),
                              "block.minecells.")
    write_autolang_file(output_path, block_autolang)


def main():
    if len(sys.argv) == 1:
        print("No output path specified")
        exit(1)
    output_path = sys.argv[1]
    print("Running Python Data Generator Script")
    print("Output Path: " + output_path)
    generate_data(output_path)


if __name__ == "__main__":
    main()
