import sys

import util
from autolang.autolang import get_entries_from_directory, autolang, write_autolang_file
from presets import common_presets
from presets.preset_generator import PresetGenerator
from template import Template, TemplateType


def generate_data(output_path: str):
    # TEMPLATES AND PRESETS ============================================================================================
    gen = PresetGenerator("minecells", output_path)


    # Misc Blocks
    def simple_item_and_drop(name: str):
        gen.generate_preset(common_presets.ItemBlockModel(name))
        gen.generate_preset(common_presets.SimpleDrop(name))

    gen.generate_template(Template(TemplateType.BLOCKSTATE, "single_state", "wilted_grass_block", {"block": "wilted_grass_block"}))
    simple_item_and_drop("wilted_grass_block")
    simple_item_and_drop("flag_pole")

    ## Torches
    for torch in ["promenade", "prison"]:
      gen.generate_template(Template(TemplateType.ITEM_MODEL, "block_model", f"{torch}_torch", {"block": f"colored_torch/{torch}_standing"}))

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
