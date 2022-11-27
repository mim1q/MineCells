from presets import common_presets
from presets.preset_generator import Preset


class CommonBlockSet(Preset):
    def __init__(self, block_name: str, base_name: str = None):
        if base_name is None:
            base_name = block_name
        super().__init__(
            [],
            [
                common_presets.SimpleBlock(block_name),
                common_presets.Stairs(base_name),
                common_presets.Slab(base_name),
            ]
        )

