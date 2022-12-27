from presets import common_presets
from presets.preset_generator import PresetGenerator


def generate_self_dropping_blocks(generator: PresetGenerator, names: [str]):
    for name in names:
        generator.generate_preset(common_presets.SimpleDrop(name))


