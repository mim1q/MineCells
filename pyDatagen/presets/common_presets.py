from presets.preset_generator import Preset
from template import Template, TemplateType


class SimpleDrop(Preset):
    def __init__(self, block_name: str):
        super().__init__([
            Template(TemplateType.BLOCK_LOOT_TABLE, "simple_drop", block_name, {"item": block_name}),
        ])


class ItemBlockModel(Preset):
    def __init__(self, block_name: str):
        super().__init__([
            Template(TemplateType.ITEM_MODEL, "block_model", block_name, {"block": block_name}),
        ])


class SimpleBlock(Preset):
    def __init__(self, block_name: str):
        super().__init__([
            Template(TemplateType.BLOCKSTATE, "single_state", block_name, {"block": block_name}),
            Template(TemplateType.BLOCK_MODEL, "cube_all", block_name, {"texture": block_name}),
            Template(TemplateType.BLOCK_LOOT_TABLE, "simple_drop", block_name, {"item": block_name}),
            Template(TemplateType.ITEM_MODEL, "block_model", block_name, {"block": block_name}),
        ])


class Stairs(Preset):
    def __init__(self, base_name: str, texture_name: str = None):
        if texture_name is None:
            texture_name = base_name
        super().__init__(
            [
                Template(TemplateType.BLOCK_MODEL, "stairs", base_name + "_stairs", {"texture": texture_name}),
                Template(TemplateType.BLOCK_MODEL, "stairs_inner", base_name + "_stairs_inner", {"texture": texture_name}),
                Template(TemplateType.BLOCK_MODEL, "stairs_outer", base_name + "_stairs_outer", {"texture": texture_name}),
                Template(TemplateType.BLOCKSTATE, "stairs", base_name + "_stairs", {"block": base_name}),
                Template(TemplateType.RECIPE, "stairs", base_name + "_stairs", {"input": base_name, "output": base_name + "_stairs"}),
                Template(TemplateType.RECIPE, "stairs_flipped", base_name + "_stairs_flipped", {"input": base_name, "output": base_name + "_stairs"}),
            ],
            [
                SimpleDrop(base_name + "_stairs"),
                ItemBlockModel(base_name + "_stairs"),
            ]
        )


class Slab(Preset):
    def __init__(self, base_name: str, texture_name: str = None):
        if texture_name is None:
            texture_name = base_name
        super().__init__(
            [
                Template(TemplateType.BLOCK_MODEL, "slab", base_name + "_slab", {"texture": texture_name}),
                Template(TemplateType.BLOCK_MODEL, "slab_top", base_name + "_slab_top", {"texture": texture_name}),
                Template(TemplateType.BLOCKSTATE, "slab", base_name + "_slab", {"block": texture_name, "base_name": base_name}),
                Template(TemplateType.RECIPE, "slab", base_name + "_slab", {"input": base_name, "output": base_name + "_slab"}),
            ],
            [
                SimpleDrop(base_name + "_slab"),
                ItemBlockModel(base_name + "_slab"),
            ]
        )
