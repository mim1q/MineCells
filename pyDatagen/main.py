import sys

from autolang.autolang import get_entries_from_directory, autolang, write_autolang_file
from presets import block_set_presets
from presets.preset_generator import PresetGenerator
from template_pools.template_pool import TemplatePoolGenerator
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

    # Custom Drops
    gen.generate_template(Template(TemplateType.BLOCK_LOOT_TABLE, "silk_touch_simple_drop", "prison_stone", {"item": "prison_cobblestone", "silk_touch_item": "prison_stone"}))

    # Custom Recipes
    gen.generate_template(Template(TemplateType.RECIPE, "smelting", "prison_cobblestone", {"input": "prison_cobblestone", "output": "prison_stone"}))
    gen.generate_template(Template(TemplateType.RECIPE, "packed_2x2", "prison_bricks", {"input": "prison_stone", "output": "prison_bricks", "count": 4}))
    gen.generate_template(Template(TemplateType.RECIPE, "packed_2x2", "small_prison_bricks", {"input": "prison_bricks", "output": "small_prison_bricks", "count": 4}))
    gen.generate_template(Template(TemplateType.RECIPE, "stonecutting", "prison_bricks_stonecutting", {"input": "prison_stone", "output": "prison_bricks", "count": 1}))
    gen.generate_template(Template(TemplateType.RECIPE, "stonecutting", "small_prison_bricks_stonecutting", {"input": "prison_bricks", "output": "small_prison_bricks", "count": 1}))
    gen.generate_template(Template(TemplateType.RECIPE, "stonecutting", "small_prison_bricks_stonecutting_from_prison_stone", {"input": "prison_stone", "output": "small_prison_bricks", "count": 1}))

    # AUTOLANG =========================================================================================================

    block_autolang = autolang(get_entries_from_directory(output_path + "\\assets\\minecells\\blockstates\\"), "block.minecells.")
    write_autolang_file(output_path, block_autolang)

    # TEMPLATE POOLS ===================================================================================================
    poolgen = TemplatePoolGenerator(output_path + "\\data\\minecells\\worldgen\\template_pool")

    # Prison - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    poolgen.generate_single("minecells:prison/spawn")
    poolgen.generate_single("minecells:prison/corridor")
    poolgen.generate_single("minecells:prison/corridor_end")
    poolgen.generate_autoprefixed("minecells:prison/main_corridor", [("0", 2), ("1", 2), ("2", 1)], processors="minecells:brick_decay")
    poolgen.generate_single("minecells:prison/main_corridor_end")
    poolgen.generate_single("minecells:prison/chain_lower")
    poolgen.generate_single("minecells:prison/chain_upper")
    poolgen.generate_single("minecells:prison/end")
    poolgen.generate_autoprefixed(
        "minecells:prison/ceiling_decoration",
        [("minecraft:empty", 2), ("broken_cage", 2), ("cage", 2), ("chains_0", 1), ("chains_1", 1), ("chains_2", 1),
         ("cobwebs_0", 1), ("cobwebs_1", 1), ("leaves_0", 3), ("leaves_1", 3), ("leaves_2", 3), ("stone_0", 3),
         ("stone_1", 3), ("stone_2", 3)]
    )
    poolgen.generate_autoprefixed(
        "minecells:prison/corridor_decoration",
        [("bars", 1), ("cobwebs", 1), ("crates_0", 1), ("crates_1", 1), ("crates_2", 1), ("crates_3", 1),
         ("shelves_0", 1), ("shelves_1", 1)]
    )
    poolgen.generate_autoprefixed("minecells:prison/spawn_decoration", [("0", 1), ("1", 1), ("2", 1), ("3", 1), ("4", 1), ("5", 1), ("6", 1)])
    poolgen.generate_autoprefixed("minecells:prison/main_corridor_doorway", [("0", 1), ("1", 1), ("2", 1), ("3", 1)])
    poolgen.generate_autoprefixed("minecells:prison/main_corridor_side_doorway", [("0", 2), ("1", 1), ("2", 1), ("3", 1)])
    poolgen.generate_autoprefixed("minecells:prison/cell", [("0", 2), ("1", 1), ("2", 1), ("3", 1)])


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
