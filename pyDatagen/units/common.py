from template_pools.template_pool import TemplatePoolGenerator


def generate_template_pools(poolgen: TemplatePoolGenerator):
    poolgen.generate_autoprefixed("minecells:common/corpse", [("skeleton", 1), ("rotting_corpse", 1), ("corpse", 1)])
    poolgen.generate_autoprefixed("minecells:common/hanged_corpse", [("skeleton", 1), ("rotting_corpse", 1), ("corpse", 1)])
    poolgen.generate_autoprefixed("minecells:common/standing_cage", [("cage", 1), ("broken_cage", 1)])
