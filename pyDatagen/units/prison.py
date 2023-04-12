from template_pools.template_pool import TemplatePoolGenerator


def generate_template_pools(poolgen: TemplatePoolGenerator):
    poolgen.generate_single("minecells:prison/spawn", processors="minecells:brick_decay")
    poolgen.generate_autoprefixed("minecells:prison/corridor", [("0", 1), ("1", 2), ("2", 2)], processors="minecells:brick_decay")
    poolgen.generate_autoprefixed("minecells:prison/main_corridor", [("0", 2), ("1", 2), ("2", 1)], processors="minecells:brick_decay")
    poolgen.generate_autoprefixed("minecells:prison/main_corridor_end", [("0", 1)], processors="minecells:brick_decay")
    poolgen.generate("minecells:prison/corridor_end", [("minecells:prison/main_corridor_end/0", 1)], processors="minecells:brick_decay")
    poolgen.generate_autoprefixed("minecells:prison/chain_lower", [("0", 1)], processors="minecells:brick_decay")
    poolgen.generate_autoprefixed("minecells:prison/chain_upper", [("0", 1)], processors="minecells:brick_decay")
    poolgen.generate_single("minecells:prison/end", processors="minecells:brick_decay")
    poolgen.generate_single("minecells:prison/end_sewers", processors="minecells:brick_decay")
    poolgen.generate_autoprefixed(
        "minecells:prison/ceiling_decoration",
        [("minecraft:empty", 4), ("broken_cage", 4), ("cage", 4), ("chains_0", 2), ("chains_1", 2), ("chains_2", 2),
         ("cobwebs_0", 1), ("cobwebs_1", 1), ("leaves_0", 6), ("leaves_1", 6), ("leaves_2", 6), ("stone_0", 6),
         ("stone_1", 6), ("stone_2", 6)]
    )
    poolgen.generate_autoprefixed(
        "minecells:prison/corridor_decoration",
        [("bars", 2), ("cobwebs", 1), ("crates_0", 2), ("crates_1", 2), ("crates_2", 2), ("crates_3", 2),
         ("shelves_0", 2), ("shelves_1", 2), ("chest", 2)]
    )
    poolgen.generate_autoprefixed("minecells:prison/spawn_decoration", [("0", 1), ("1", 1), ("2", 1), ("3", 1), ("4", 1), ("5", 1), ("6", 1)])
    poolgen.generate_autoprefixed("minecells:prison/main_corridor_doorway", [("0", 1), ("1", 1), ("2", 1), ("3", 1)])
    poolgen.generate_autoprefixed("minecells:prison/main_corridor_side_doorway", [("0", 2), ("1", 1), ("2", 1), ("3", 1)])
    poolgen.generate(
        "minecells:prison/corridor_hole",
        [("minecells:prison/main_corridor_doorway/1", 1), ("minecells:prison/main_corridor_doorway/2", 1), ("minecells:prison/main_corridor_doorway/3", 1)]
    )
    poolgen.generate_autoprefixed("minecells:prison/cell", [("0", 2), ("1", 1), ("2", 1), ("3", 1)])
    poolgen.generate_autoprefixed("minecells:prison/corridor_hole_cell", [("0", 1), ("1", 1), ("2", 1), ("3", 1), ("4", 1), ("5", 1)], processors="minecells:brick_decay")
    poolgen.generate_single("minecells:prison/spawn_rune_and_chest")
    poolgen.generate_single("minecells:prison/spawn_rune")
    poolgen.generate_single("minecells:prison/chest")

