from presets import common_presets, wood_presets
from presets.preset_generator import Preset


class CommonBlockSet(Preset):
    def __init__(self, block: str, base: str = None, stone: bool = False):
        if base is None:
            base = block
        super().__init__(
            [],
            [
                common_presets.SimpleBlock(block),
                common_presets.Stairs(base, block, stone),
                common_presets.Slab(base, block, stone),
            ]
        )


class StoneBlockSet(Preset):
    def __init__(self, block: str, base: str = None):
        if base is None:
            base = block
        super().__init__(
            [],
            [
                CommonBlockSet(block, base, True),
                common_presets.Wall(base, block),
            ]
        )


class WoodBlockSet(Preset):
    def __init__(self, block: str, base: str = None):
        if base is None:
            base = block
        super().__init__(
            [],
            [
                CommonBlockSet(block, base),
                wood_presets.Fence(base, block),
                wood_presets.FenceGate(base, block),
                wood_presets.Door(base, block),
                wood_presets.Trapdoor(base, block),
            ]
        )
