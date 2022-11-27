from preset_generator import Preset
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
            Template(TemplateType.BLOCKSTATE, "simple_block", block_name, {"block": block_name}),
            Template(TemplateType.BLOCK_MODEL, "simple_block", block_name, {"block": block_name}),
            Template(TemplateType.BLOCK_LOOT_TABLE, "simple_drop", block_name, {"item": block_name}),
            Template(TemplateType.ITEM_MODEL, "block_model", block_name, {"block": block_name}),
        ])


class Stairs(Preset):
    def __init__(self, block_name: str):
        super().__init__(
            [
                Template(TemplateType.BLOCK_MODEL, "stairs", block_name + "_stairs",  {"texture": block_name}),
                Template(TemplateType.BLOCK_MODEL, "stairs_inner", block_name + "_stairs_inner",  {"texture": block_name}),
                Template(TemplateType.BLOCK_MODEL, "stairs_outer", block_name + "_stairs_outer",  {"texture": block_name}),
                Template(TemplateType.BLOCKSTATE, "stairs", block_name + "_stairs", {"block": block_name}),
                Template(TemplateType.RECIPE, "stairs", block_name + "_stairs", {"block": block_name}),
                Template(TemplateType.RECIPE, "stairs_flipped", block_name + "_stairs_flipped", {"block": block_name}),
            ],
            [
                SimpleDrop(block_name + "_stairs"),
                ItemBlockModel(block_name + "_stairs"),
            ]
        )
