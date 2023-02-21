from template_pools.template_pool import TemplatePoolGenerator


def generate_template_pools(poolgen: TemplatePoolGenerator):
    poolgen.generate_autoprefixed("minecells:promenade/chain_pile", [("0", 1), ("1", 2), ("2", 2), ("3", 2)], projection="terrain_matching")
    poolgen.generate_autoprefixed("minecells:promenade/gallows", [("0", 1), ("1", 2), ("2", 2), ("3", 2)], processors="minecells:promenade")
    poolgen.generate_single("minecells:promenade/king_statue", projection="terrain_matching", processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/overground_buildings/main", [("0", 3), ("1", 1)], processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/overground_buildings/side", [("0", 2), ["1", 1]], processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/overground_buildings/pit", [("0", 1)], processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/underground_buildings/entry", [("0", 1)], processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/underground_buildings/shaft", [("0", 1)], processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/underground_buildings/shaft_bottom", [("0", 1)], processors="minecells:promenade")
    poolgen.generate_indexed("minecells:promenade/underground_buildings/room", [3, 2, 2, 1, 2], processors="minecells:promenade")
    poolgen.generate_indexed("minecells:promenade/underground_buildings/chain_top", [1], processors="minecells:promenade")
    poolgen.generate_indexed("minecells:promenade/underground_buildings/chain_bottom", [1], processors="minecells:promenade")
    poolgen.generate_single("minecells:promenade/underground_buildings/room_end_center", processors="minecells:promenade")
    poolgen.generate_single("minecells:promenade/underground_buildings/room_end_right", processors="minecells:promenade")
    poolgen.generate_single("minecells:promenade/underground_buildings/room_end_left", processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/doorway", [("0", 2), ("1", 1), ("2", 1), ("3", 1), ("4", 1)], processors="minecells:promenade")
    poolgen.generate_autoprefixed("minecells:promenade/doorway_decoration", [("banners", 1), ("torches", 2), ("minecraft:empty", 4)], processors="minecells:promenade")
