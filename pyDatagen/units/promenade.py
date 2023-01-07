from template_pools.template_pool import TemplatePoolGenerator


def generate_template_pools(poolgen: TemplatePoolGenerator):
    poolgen.generate_autoprefixed("minecells:promenade/chain_pile", [("0", 1), ("1", 2), ("2", 2), ("3", 2)], projection="terrain_matching")
