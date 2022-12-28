from template_pools.template_pool import TemplatePoolGenerator


def generate_template_pools(poolgen: TemplatePoolGenerator):
    poolgen.generate_single("minecells:insufferable_crypt/spawn", processors="minecells:brick_decay")
    poolgen.generate_single("minecells:insufferable_crypt/elevator_shaft", processors="minecells:brick_decay")
    poolgen.generate_single("minecells:insufferable_crypt/boss_room", processors="minecells:brick_decay")