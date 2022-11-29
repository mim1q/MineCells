from __future__ import annotations

from template import Template


class Preset:
    templates: [Template]
    presets: [Preset]

    def __init__(self, templates: [Template], presets=None):
        if presets is None:
            presets = []
        self.presets = presets
        self.templates = templates

    def apply(self, output_path: str, mod_id: str):
        for template in self.templates:
            template.apply(output_path, mod_id)
        for preset in self.presets:
            preset.apply(output_path, mod_id)


class PresetGenerator:
    mod_id: str
    output_path: str

    def __init__(self, mod_id: str, output_path: str):
        self.mod_id = mod_id
        self.output_path = output_path

    def generate_preset(self, preset: Preset):
        preset.apply(self.output_path, self.mod_id)

    def generate_template(self, template: Template):
        template.apply(self.output_path, self.mod_id)

