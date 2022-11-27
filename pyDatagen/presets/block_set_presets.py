from presets import common_presets
from presets.preset_generator import Preset


class CommonBlockSet(Preset):
    def __init__(self, block: str, base: str = None):
        if base is None:
            base = block
        super().__init__(
            [],
            [
                common_presets.SimpleBlock(block),
                common_presets.Stairs(base, block),
                common_presets.Slab(base, block),
            ]
        )

