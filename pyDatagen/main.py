import sys

from autolang.autolang import get_entries_from_directory, autolang, write_autolang_file
from presets import block_set_presets
from presets.preset_generator import PresetGenerator
from template import Template, TemplateType


def generate_data(output_path: str):
    generator = PresetGenerator("minecells", output_path)

    # Block Sets
    generator.generate_preset(block_set_presets.StoneBlockSet("small_prison_bricks", "small_prison_brick"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_bricks", "prison_brick"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_stone"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_cobblestone"))
    generator.generate_preset(block_set_presets.WoodBlockSet("putrid_planks", "putrid"))

    # Custom Drops
    generator.generate_template(Template(TemplateType.BLOCK_LOOT_TABLE, "silk_touch_simple_drop", "prison_stone", {"item": "prison_cobblestone", "silk_touch_item": "prison_stone"}))

    # Custom Recipes
    generator.generate_template(Template(TemplateType.RECIPE, "smelting", "prison_cobblestone", {"input": "prison_cobblestone", "output": "prison_stone"}))
    generator.generate_template(Template(TemplateType.RECIPE, "packed_2x2", "prison_bricks", {"input": "prison_stone", "output": "prison_bricks", "count": 4}))
    generator.generate_template(Template(TemplateType.RECIPE, "packed_2x2", "small_prison_bricks", {"input": "prison_bricks", "output": "small_prison_bricks", "count": 4}))
    generator.generate_template(Template(TemplateType.RECIPE, "stonecutting", "prison_bricks_stonecutting", {"input": "prison_stone", "output": "prison_bricks", "count": 1}))
    generator.generate_template(Template(TemplateType.RECIPE, "stonecutting", "small_prison_bricks_stonecutting", {"input": "prison_bricks", "output": "small_prison_bricks", "count": 1}))
    generator.generate_template(Template(TemplateType.RECIPE, "stonecutting", "small_prison_bricks_stonecutting_from_prison_stone", {"input": "prison_stone", "output": "small_prison_bricks", "count": 1}))

    block_autolang = autolang(get_entries_from_directory(output_path + "\\assets\\minecells\\blockstates\\"), "block.minecells.")
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
