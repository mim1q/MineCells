import sys

from autolang.autolang import get_entries_from_directory, autolang, write_autolang_file
from presets.preset_generator import PresetGenerator


def generate_data(output_path: str):
    # TEMPLATES AND PRESETS ============================================================================================
    gen = PresetGenerator("minecells", output_path)
    # Custom Drops

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
