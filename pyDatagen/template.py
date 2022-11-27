import os
import re
from enum import Enum


class TemplateType(Enum):
    BLOCK_MODEL = "models\\block"
    ITEM_MODEL = "models\\item"
    BLOCKSTATE = "blockstates"
    RECIPE = "recipes"
    BLOCK_LOOT_TABLE = "loot_tables\\blocks"

    def __str__(self):
        return self.value


class Template:
    def __init__(self, template_type: TemplateType, template_name: str, output_name: str, variables: {}):
        self.template_type = template_type
        self.template_name = template_name
        self.output_name = output_name
        self.variables = variables

    def apply(self, output_path: str, mod_id: str):
        self.variables["id"] = mod_id
        apply_template(self.template_type, self.template_name, output_path, mod_id, self.output_name, self.variables)


def load_template_file_contents(name: str) -> str:
    template_path = os.path.join(os.path.dirname(__file__), "templates", name)
    with open(template_path, "r") as template_file:
        template_contents = template_file.read()
    return template_contents


def get_template(template_type: TemplateType, name: str) -> str:
    directory = str(template_type)
    return load_template_file_contents(directory + "\\" + name + ".json5")


def apply_variables(template: str, variables: dict) -> str:
    for key, value in variables.items():
        template = template.replace("{{" + key + "}}", value)
    return re.sub(r"//.*\n?", "", re.sub(r"/\*.*\*/\n*", "", template))


def get_save_path(output_path: str, template_type: TemplateType, mod_id: str, name: str) -> str:
    if template_type == TemplateType.RECIPE or template_type == TemplateType.BLOCK_LOOT_TABLE:
        filename = "data\\" + mod_id + "\\" + str(template_type) + "\\" + name + ".json"
    else:
        filename = "assets\\" + mod_id + "\\" + str(template_type) + "\\" + name + ".json"
    return os.path.join(output_path, filename)


def apply_template(
        template_type: TemplateType,
        template_name: str,
        output_path: str,
        mod_id: str,
        output_name: str,
        variables: {}
):
    template = get_template(template_type, template_name)
    result = apply_variables(template, variables)
    save_path = get_save_path(output_path, template_type, mod_id, output_name)
    os.makedirs(os.path.dirname(save_path), exist_ok=True)
    with open(save_path, "w") as save_file:
        save_file.write(result)
