import sys

from autolang.autolang import get_entries_from_directory, autolang, write_autolang_file
from presets import block_set_presets
from presets.preset_generator import PresetGenerator


def generate_data(output_path: str):
    generator = PresetGenerator("minecells", output_path)

    generator.generate_preset(block_set_presets.StoneBlockSet("small_prison_bricks", "small_prison_brick"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_bricks", "prison_brick"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_stone"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_cobblestone"))
    generator.generate_preset(block_set_presets.WoodBlockSet("putrid_planks", "putrid"))

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
