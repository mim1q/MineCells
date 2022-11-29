from presets.preset_generator import Preset
from template import Template, TemplateType


class SimpleDrop(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.BLOCK_LOOT_TABLE, "simple_drop", block, {"item": block}),
        ])


class DoorDrop(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.BLOCK_LOOT_TABLE, "door_drop", block, {"block": block}),
        ])


class ItemBlockModel(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.ITEM_MODEL, "block_model", block, {"block": block}),
        ])


class GeneratedItemModel(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.ITEM_MODEL, "generated", block, {"texture": block}),
        ])


class SimpleBlock(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.BLOCKSTATE, "single_state", block, {"block": block}),
            Template(TemplateType.BLOCK_MODEL, "cube_all", block, {"texture": block}),
            Template(TemplateType.BLOCK_LOOT_TABLE, "simple_drop", block, {"item": block}),
            Template(TemplateType.ITEM_MODEL, "block_model", block, {"block": block}),
        ])


class Stairs(Preset):
    def __init__(self, base: str, block: str = None):
        if block is None:
            block = base
        super().__init__(
            [
                Template(TemplateType.BLOCKSTATE, "stairs", base + "_stairs", {"base": base}),
                Template(TemplateType.BLOCK_MODEL, "stairs", base + "_stairs", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "stairs_inner", base + "_stairs_inner", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "stairs_outer", base + "_stairs_outer", {"texture": block}),
                Template(TemplateType.RECIPE, "stairs", base + "_stairs", {"input": block, "output": base + "_stairs"}),
                Template(TemplateType.RECIPE, "stairs_flipped", base + "_stairs_flipped", {"input": block, "output": base + "_stairs"}),
            ],
            [
                SimpleDrop(base + "_stairs"),
                ItemBlockModel(base + "_stairs"),
            ]
        )


class Slab(Preset):
    def __init__(self, base: str, block: str = None):
        if block is None:
            block = base
        super().__init__(
            [
                Template(TemplateType.BLOCKSTATE, "slab", base + "_slab", {"block": block, "base": base}),
                Template(TemplateType.BLOCK_MODEL, "slab", base + "_slab", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "slab_top", base + "_slab_top", {"texture": block}),
                Template(TemplateType.RECIPE, "slab", base + "_slab", {"input": block, "output": base + "_slab"}),
            ],
            [
                SimpleDrop(base + "_slab"),
                ItemBlockModel(base + "_slab"),
            ]
        )


class Wall(Preset):
    def __init__(self, base: str, block: str = None):
        if block is None:
            block = base
        super().__init__(
            [
                Template(TemplateType.BLOCKSTATE, "wall", base + "_wall", {"base": base}),
                Template(TemplateType.BLOCK_MODEL, "wall_post", base + "_wall_post", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "wall_side", base + "_wall_side", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "wall_side_tall", base + "_wall_side_tall", {"texture": block}),
                Template(TemplateType.ITEM_MODEL, "wall", base + "_wall", {"texture": block}),
                Template(TemplateType.RECIPE, "packed_3x2", base + "_wall", {"input": block, "output": base + "_wall"}),
            ],
            [
                SimpleDrop(base + "_wall")
            ]
        )


class Fence(Preset):
    def __init__(self, base: str, block: str = None):
        if block is None:
            block = base
        super().__init__(
            [
                Template(TemplateType.BLOCKSTATE, "fence", base + "_fence", {"base": base}),
                Template(TemplateType.BLOCK_MODEL, "fence_post", base + "_fence_post", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "fence_side", base + "_fence_side", {"texture": block}),
                Template(TemplateType.ITEM_MODEL, "fence", base + "_fence", {"texture": block}),
                Template(TemplateType.RECIPE, "fence", base + "_fence", {"input": block, "output": base + "_fence"}),
            ],
            [
                SimpleDrop(base + "_fence")
            ]
        )


class FenceGate(Preset):
    def __init__(self, base: str, block: str = None):
        if block is None:
            block = base
        super().__init__(
            [
                Template(TemplateType.BLOCKSTATE, "fence_gate", base + "_fence_gate", {"base": base}),
                Template(TemplateType.BLOCK_MODEL, "fence_gate", base + "_fence_gate", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "fence_gate_open", base + "_fence_gate_open", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "fence_gate_wall", base + "_fence_gate_wall", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "fence_gate_wall_open", base + "_fence_gate_wall_open", {"texture": block}),
                Template(TemplateType.RECIPE, "fence_gate", base + "_fence_gate", {"input": block, "output": base + "_fence_gate"}),
            ],
            [
                SimpleDrop(base + "_fence_gate"),
                ItemBlockModel(base + "_fence_gate"),
            ]
        )


class Door(Preset):
    def __init__(self, base: str, block: str = None):
        if block is None:
            block = base
        super().__init__(
            [
                Template(TemplateType.BLOCKSTATE, "door", base + "_door", {"base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_bottom_left", {"variant": "bottom_left", "base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_bottom_right", {"variant": "bottom_right", "base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_bottom_left_open", {"variant": "bottom_left_open", "base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_bottom_right_open", {"variant": "bottom_right_open", "base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_top_left", {"variant": "top_left", "base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_top_right", {"variant": "top_right", "base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_top_left_open", {"variant": "top_left_open", "base": base}),
                Template(TemplateType.BLOCK_MODEL, "door", base + "_door_top_right_open", {"variant": "top_right_open", "base": base}),
                Template(TemplateType.RECIPE, "packed_2x3", base + "_door", {"input": block, "output": base + "_door"}),
            ],
            [
                DoorDrop(base + "_door"),
                GeneratedItemModel(base + "_door"),
            ]
        )

