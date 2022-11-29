from presets.common_presets import GeneratedItemModel, SimpleDrop, ItemBlockModel
from presets.preset_generator import Preset
from template import Template, TemplateType


class DoorDrop(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.BLOCK_LOOT_TABLE, "door_drop", block, {"block": block}),
        ])


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
