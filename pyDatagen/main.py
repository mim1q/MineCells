import sys

from presets import block_set_presets
from presets.preset_generator import PresetGenerator


def generate_data(output_path: str):
    generator = PresetGenerator("minecells", output_path)

    generator.generate_preset(block_set_presets.StoneBlockSet("small_prison_bricks", "small_prison_brick"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_bricks", "prison_brick"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_stone"))
    generator.generate_preset(block_set_presets.StoneBlockSet("prison_cobblestone"))
    generator.generate_preset(block_set_presets.CommonBlockSet("putrid_planks", "putrid"))


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
