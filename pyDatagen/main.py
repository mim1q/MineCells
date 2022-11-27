import sys

import presets
from preset_generator import PresetGenerator


def generate_data(output_path: str):
    generator = PresetGenerator("minecells", output_path)
    generator.generate_preset(presets.Stairs("small_prison_bricks"))


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
