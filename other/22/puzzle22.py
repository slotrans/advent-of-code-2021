from dataclasses import dataclass
from collections import namedtuple
from functools import cached_property


#@dataclass(frozen=True)
#class Point3:
#    x: int
#    y: int
#    z: int
Point3 = namedtuple("Point3", ["x", "y", "z"])
Point2 = namedtuple("Point2", ["x", "y"])


@dataclass(frozen=True)
class Instruction:
    on_or_off: int # 0=off, 1=on
    x_min: int
    x_max: int
    y_min: int
    y_max: int
    z_min: int
    z_max: int
    is_within_init_region: bool

    @cached_property
    def xy_grid(self):
        grid = {}
        for x in range(self.x_min, self.x_max+1):
            for y in range(self.y_min, self.y_max+1):
                grid[Point2(x, y)] = self.on_or_off
        return grid

    @cached_property
    def z_line(self):
        line = set()
        for z in range(self.z_min, self.z_max+1):
            line.add(z)
        return line


def instructions_from_input(input_string) -> list[Instruction]:
    out = []
    for line in input_string.split("\n"):
        on_off_part, ranges_part = line.split(" ")

        on_or_off = 1 if on_off_part == "on" else 0

        x_range, y_range, z_range = ranges_part.split(",")
        x_min, x_max = [int(v) for v in x_range[2:].split("..")]
        y_min, y_max = [int(v) for v in y_range[2:].split("..")]
        z_min, z_max = [int(v) for v in z_range[2:].split("..")]
        is_within_init_region = min(x_min, y_min, z_min) >= -50 and max(x_max, y_max, z_max) <= 50

        temp = Instruction(on_or_off, x_min, x_max, y_min, y_max, z_min, z_max, is_within_init_region)
        out.append(temp)
    return out


def initialize_grid(instructions: list[Instruction]) -> dict: # {Point3: int}
    grid = {}
    for inst in instructions:
        if not inst.is_within_init_region:
            continue

        for x in range(inst.x_min, inst.x_max+1):
            for y in range(inst.y_min, inst.y_max+1):
                for z in range(inst.z_min, inst.z_max+1):
                    grid[Point3(x, y, z)] = inst.on_or_off

    return grid


def count_after_initialize_planar(instructions: list[Instruction]) -> int:
    instructions = list(filter(lambda i: i.is_within_init_region, instructions))
    overall_z_min = min([i.z_min for i in instructions if i.is_within_init_region])
    overall_z_max = max([i.z_max for i in instructions if i.is_within_init_region])

    on_count = 0
    for z in range(overall_z_min, overall_z_max+1):
        print(f"z={z}")
        grid = {}
        for inst in instructions:
            if not inst.z_min <= z <= inst.z_max:
                continue

            grid.update(inst.xy_grid)

        on_count += sum(grid.values())

    return on_count


def count_after_reboot_planar(instructions: list[Instruction]) -> int:
    overall_z_min = min([i.z_min for i in instructions])
    overall_z_max = max([i.z_max for i in instructions])

    on_count = 0
    for z in range(overall_z_min, overall_z_max+1):
        print(f"z={z}")
        grid = {}
        for inst in instructions:
            if inst.z_min <= z <= inst.z_max:
                grid.update(inst.xy_grid)

        on_count += sum(grid.values())

    return on_count


def count_after_reboot_linear(instructions: list[Instruction]) -> int:
    overall_x_min = min([i.x_min for i in instructions])
    overall_x_max = max([i.x_max for i in instructions])
    overall_y_min = min([i.y_min for i in instructions])
    overall_y_max = max([i.y_max for i in instructions])

    on_count = 0
    for x in range(overall_x_min, overall_x_max+1):
        print(f"x={x} | on_count={on_count}")
        for y in range(overall_y_min, overall_y_max+1):
            #print(f"(x={x}, y={y}) on_count={on_count}")
            line = set()
            for inst in instructions:
                if (inst.x_min <= x <= inst.x_max and inst.y_min <= y <= inst.y_max):
                    if inst.on_or_off == 1:
                        line.update(inst.z_line)
                    else:
                        line.difference_update(inst.z_line)

            on_count += len(line)

    return on_count


def apply_instruction_to_z_range(on_range: list[tuple[int, int]], instruction: Instruction) -> None: # mutates on_range
    if len(on_range) == 0:
        if instruction.on_or_off == 1:
            on_range.append((instruction.z_min, instruction.z_max))
        else:
            # not relevant in practice since first instruction is always "on"
            return

    z_range = (instruction.z_min, instruction.z_max)

    if instruction.on_or_off == 1:
        overlapped_any_subrange = False
        for i in range(len(on_range)):
            subrange = on_range[i]
            if z_range[0] <= subrange[0] and z_range[1] >= subrange[1]:
                # input range is at least as large -> replace current
                on_range[i] = z_range
                overlapped_any_subrange = True
            elif z_range[0] >= subrange[0] and z_range[1] <= subrange[1]:
                # lies entirely within -> do nothing & stop checking
                return
            elif subrange[0] <= z_range[0] <= subrange[1] or subrange[0] <= z_range[1] <= subrange[1]:
                # one end lies within -> extend
                on_range[i] = (min(subrange[0], z_range[0]), max(subrange[1], z_range[1]))
                overlapped_any_subrange = True

        if not overlapped_any_subrange:
            on_range.append(z_range)

    else: # off
        subranges_to_remove = []
        for i in range(len(on_range)):
            subrange = on_range[i]
            if z_range[1] < subrange[0] or z_range[0] > subrange[1]:
                # no overlap -> do nothing
                pass
            elif z_range[0] <= subrange[0] and z_range[1] >= subrange[1]:
                # input range is at least as large -> delete
                subranges_to_remove.append(i)
            elif z_range[0] > subrange[0] and z_range[1] < subrange[1]:
                # lies entirely within -> split
                subranges_to_remove.append(i)
                on_range.append((subrange[0], z_range[0]-1))
                on_range.append((z_range[1]+1, subrange[1]))
            elif z_range[0] <= subrange[0] and subrange[0] < z_range[1] < subrange[1]:
                # input overlaps left endpoint of this range -> shrink
                on_range[i] = (z_range[1]+1, subrange[1])
            elif subrange[0] < z_range[0] < subrange[1] and z_range[1] >= subrange[1]:
                # input overlaps right endpoint of this range -> shrink
                on_range[i] = (subrange[0], z_range[0]-1)
            else:
                raise Exception("unhandled scenario in OFF case!")

        shift = 0
        for i in subranges_to_remove:
            on_range.pop(i-shift)
            shift += 1

    on_range.sort()

    # reduce by consolidating overlapping ranges
    should_continue = True
    while(should_continue):
        should_continue = False
        for i in range(0, len(on_range)-1):
            this_one, next_one = on_range[i], on_range[i+1]
            if this_one[1] >= next_one[0]:
                on_range[i+1] = (this_one[0], next_one[1])
                on_range.pop(0)
                should_continue = True
                break


if __name__ == "__main__":
    input22 = open("input22", encoding="utf-8").read().strip()

    print("Part 1")
    instructions = instructions_from_input(input22)
    #grid = initialize_grid(instructions)
    #on_after_init = sum(grid.values())
    on_after_init = count_after_initialize_planar(instructions)
    print(f"(p1 answer) cubes on after initialization = {on_after_init}") # 577205


    print("Part 2")
    on_after_reboot = count_after_reboot_linear(instructions)
    print(f"(p2 answer) cubes on after reboot = {on_after_reboot}") #


###############################################################################

SAMPLE_INPUT_SMALL = """
on x=10..12,y=10..12,z=10..12
on x=11..13,y=11..13,z=11..13
off x=9..11,y=9..11,z=9..11
on x=10..10,y=10..10,z=10..10
""".strip()

SAMPLE_INPUT_LARGE = """
on x=-20..26,y=-36..17,z=-47..7
on x=-20..33,y=-21..23,z=-26..28
on x=-22..28,y=-29..23,z=-38..16
on x=-46..7,y=-6..46,z=-50..-1
on x=-49..1,y=-3..46,z=-24..28
on x=2..47,y=-22..22,z=-23..27
on x=-27..23,y=-28..26,z=-21..29
on x=-39..5,y=-6..47,z=-3..44
on x=-30..21,y=-8..43,z=-13..34
on x=-22..26,y=-27..20,z=-29..19
off x=-48..-32,y=26..41,z=-47..-37
on x=-12..35,y=6..50,z=-50..-2
off x=-48..-32,y=-32..-16,z=-15..-5
on x=-18..26,y=-33..15,z=-7..46
off x=-40..-22,y=-38..-28,z=23..41
on x=-16..35,y=-41..10,z=-47..6
off x=-32..-23,y=11..30,z=-14..3
on x=-49..-5,y=-3..45,z=-29..18
off x=18..30,y=-20..-8,z=-3..13
on x=-41..9,y=-7..43,z=-33..15
on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
on x=967..23432,y=45373..81175,z=27513..53682
""".strip()

SAMPLE_INPUT_P2 = """
on x=-5..47,y=-31..22,z=-19..33
on x=-44..5,y=-27..21,z=-14..35
on x=-49..-1,y=-11..42,z=-10..38
on x=-20..34,y=-40..6,z=-44..1
off x=26..39,y=40..50,z=-2..11
on x=-41..5,y=-41..6,z=-36..8
off x=-43..-33,y=-45..-28,z=7..25
on x=-33..15,y=-32..19,z=-34..11
off x=35..47,y=-46..-34,z=-11..5
on x=-14..36,y=-6..44,z=-16..29
on x=-57795..-6158,y=29564..72030,z=20435..90618
on x=36731..105352,y=-21140..28532,z=16094..90401
on x=30999..107136,y=-53464..15513,z=8553..71215
on x=13528..83982,y=-99403..-27377,z=-24141..23996
on x=-72682..-12347,y=18159..111354,z=7391..80950
on x=-1060..80757,y=-65301..-20884,z=-103788..-16709
on x=-83015..-9461,y=-72160..-8347,z=-81239..-26856
on x=-52752..22273,y=-49450..9096,z=54442..119054
on x=-29982..40483,y=-108474..-28371,z=-24328..38471
on x=-4958..62750,y=40422..118853,z=-7672..65583
on x=55694..108686,y=-43367..46958,z=-26781..48729
on x=-98497..-18186,y=-63569..3412,z=1232..88485
on x=-726..56291,y=-62629..13224,z=18033..85226
on x=-110886..-34664,y=-81338..-8658,z=8914..63723
on x=-55829..24974,y=-16897..54165,z=-121762..-28058
on x=-65152..-11147,y=22489..91432,z=-58782..1780
on x=-120100..-32970,y=-46592..27473,z=-11695..61039
on x=-18631..37533,y=-124565..-50804,z=-35667..28308
on x=-57817..18248,y=49321..117703,z=5745..55881
on x=14781..98692,y=-1341..70827,z=15753..70151
on x=-34419..55919,y=-19626..40991,z=39015..114138
on x=-60785..11593,y=-56135..2999,z=-95368..-26915
on x=-32178..58085,y=17647..101866,z=-91405..-8878
on x=-53655..12091,y=50097..105568,z=-75335..-4862
on x=-111166..-40997,y=-71714..2688,z=5609..50954
on x=-16602..70118,y=-98693..-44401,z=5197..76897
on x=16383..101554,y=4615..83635,z=-44907..18747
off x=-95822..-15171,y=-19987..48940,z=10804..104439
on x=-89813..-14614,y=16069..88491,z=-3297..45228
on x=41075..99376,y=-20427..49978,z=-52012..13762
on x=-21330..50085,y=-17944..62733,z=-112280..-30197
on x=-16478..35915,y=36008..118594,z=-7885..47086
off x=-98156..-27851,y=-49952..43171,z=-99005..-8456
off x=2032..69770,y=-71013..4824,z=7471..94418
on x=43670..120875,y=-42068..12382,z=-24787..38892
off x=37514..111226,y=-45862..25743,z=-16714..54663
off x=25699..97951,y=-30668..59918,z=-15349..69697
off x=-44271..17935,y=-9516..60759,z=49131..112598
on x=-61695..-5813,y=40978..94975,z=8655..80240
off x=-101086..-9439,y=-7088..67543,z=33935..83858
off x=18020..114017,y=-48931..32606,z=21474..89843
off x=-77139..10506,y=-89994..-18797,z=-80..59318
off x=8476..79288,y=-75520..11602,z=-96624..-24783
on x=-47488..-1262,y=24338..100707,z=16292..72967
off x=-84341..13987,y=2429..92914,z=-90671..-1318
off x=-37810..49457,y=-71013..-7894,z=-105357..-13188
off x=-27365..46395,y=31009..98017,z=15428..76570
off x=-70369..-16548,y=22648..78696,z=-1892..86821
on x=-53470..21291,y=-120233..-33476,z=-44150..38147
off x=-93533..-4276,y=-16170..68771,z=-104985..-24507
""".strip()


def test_instructions_from_input_small_sample():
    expected = [
        Instruction(1, 10, 12, 10, 12, 10, 12, True),
        Instruction(1, 11, 13, 11, 13, 11, 13, True),
        Instruction(0, 9, 11, 9, 11, 9, 11, True),
        Instruction(1, 10, 10, 10, 10, 10, 10, True),
    ]
    computed = instructions_from_input(SAMPLE_INPUT_SMALL)
    assert expected == computed


def test_initialize_grid_small_sample():
    instructions = instructions_from_input(SAMPLE_INPUT_SMALL)
    grid = initialize_grid(instructions)
    assert 39 == sum(grid.values())


def test_initialize_grid_large_sample():
    instructions = instructions_from_input(SAMPLE_INPUT_LARGE)
    grid = initialize_grid(instructions)
    assert 590784 == sum(grid.values())


def test_count_after_initialize_planar_small_sample():
    instructions = instructions_from_input(SAMPLE_INPUT_SMALL)
    on_count = count_after_initialize_planar(instructions)
    assert 39 == on_count


def test_count_after_initialize_planar_large_sample():
    instructions = instructions_from_input(SAMPLE_INPUT_LARGE)
    on_count = count_after_initialize_planar(instructions)
    assert 590784 == on_count


def test_count_after_initialize_planar_p2_sample():
    instructions = instructions_from_input(SAMPLE_INPUT_P2)
    on_count = count_after_initialize_planar(instructions)
    assert 474140 == on_count


#def test_count_after_reboot_planar_p2_sample():
#    instructions = instructions_from_input(SAMPLE_INPUT_P2)
#    on_count = count_after_reboot_planar(instructions)
#    assert 2758514936282235 == on_count

#def test_count_after_reboot_linear_p2_sample():
#    instructions = instructions_from_input(SAMPLE_INPUT_P2)
#    on_count = count_after_reboot_linear(instructions)
#    assert 2758514936282235 == on_count


def test_apply_instruction_to_z_range_small_sample():
    on_range = []
    inst_a = Instruction(1, 10, 12, 10, 12, 10, 12, True)
    inst_b = Instruction(1, 11, 13, 11, 13, 11, 13, True)
    inst_c = Instruction(0, 9, 11, 9, 11, 9, 11, True)
    inst_d = Instruction(1, 10, 10, 10, 10, 10, 10, True)

    apply_instruction_to_z_range(on_range, inst_a)
    assert on_range == [(10,12)]

    apply_instruction_to_z_range(on_range, inst_b)
    assert on_range == [(10,13)]

    apply_instruction_to_z_range(on_range, inst_c)
    assert on_range == [(12,13)]

    apply_instruction_to_z_range(on_range, inst_d)
    assert on_range == [(10,10), (12,13)]


def test_apply_instruction_to_z_range_large_sample():
    instructions = instructions_from_input(SAMPLE_INPUT_LARGE)
    on_range = []

    apply_instruction_to_z_range(on_range, instructions[0]) # z=-47..7
    assert on_range == [(-47,7)]

    apply_instruction_to_z_range(on_range, instructions[1]) # z=-26..28
    assert on_range == [(-47,28)]

    apply_instruction_to_z_range(on_range, instructions[2]) # z=-38..16
    assert on_range == [(-47,28)]

    apply_instruction_to_z_range(on_range, instructions[3]) # z=-50..-1
    assert on_range == [(-50,28)]

    apply_instruction_to_z_range(on_range, instructions[4]) # z=-24..28
    assert on_range == [(-50,28)]

    apply_instruction_to_z_range(on_range, instructions[5]) # z=-23..27
    assert on_range == [(-50,28)]

    apply_instruction_to_z_range(on_range, instructions[6]) # z=-21..29
    assert on_range == [(-50,29)]

    apply_instruction_to_z_range(on_range, instructions[7]) # z=-3..44
    assert on_range == [(-50,44)]

    apply_instruction_to_z_range(on_range, instructions[8]) # z=-13..34
    assert on_range == [(-50,44)]

    apply_instruction_to_z_range(on_range, instructions[9]) # z=-29..19
    assert on_range == [(-50,44)]

    apply_instruction_to_z_range(on_range, instructions[10]) # z=-47..-37 OFF
    assert on_range == [(-50,-48), (-36,44)]

    apply_instruction_to_z_range(on_range, instructions[11]) # z=-50..-2
    assert on_range == [(-50,44)]

    apply_instruction_to_z_range(on_range, instructions[12]) # z=-15..-5 OFF
    assert on_range == [(-50,-16), (-4,44)]

    apply_instruction_to_z_range(on_range, instructions[13]) # z=-7..46
    assert on_range == [(-50,-16), (-7,46)]

    apply_instruction_to_z_range(on_range, instructions[14]) # z=23..41 OFF
    assert on_range == [(-50,-16), (-7,22), (42,46)]

    apply_instruction_to_z_range(on_range, instructions[15]) # z=-47..6
    assert on_range == [(-50,22), (42,46)]

    apply_instruction_to_z_range(on_range, instructions[16]) # z=-14..3 OFF
    assert on_range == [(-50,-15), (4,22), (42,46)]

    apply_instruction_to_z_range(on_range, instructions[17]) # z=-29..18
    assert on_range == [(-50,22), (42,46)]

    apply_instruction_to_z_range(on_range, instructions[18]) # z=-3..13 OFF
    assert on_range == [(-50,-4), (14,22), (42,46)]

    apply_instruction_to_z_range(on_range, instructions[19]) # z=-33..15
    assert on_range == [(-50,22), (42,46)]


def test_apply_instruction_to_z_range_p2_sample():
    instructions = instructions_from_input(SAMPLE_INPUT_P2)
    on_range = []

    apply_instruction_to_z_range(on_range, instructions[0]) # z=-19..33
    assert on_range == [(-19,33)]

    apply_instruction_to_z_range(on_range, instructions[1]) # z=-14..35
    assert on_range == [(-19,35)]

    apply_instruction_to_z_range(on_range, instructions[2]) # z=-10..38
    assert on_range == [(-19,38)]

    apply_instruction_to_z_range(on_range, instructions[3]) # z=-44..1
    assert on_range == [(-44,38)]

    apply_instruction_to_z_range(on_range, instructions[4]) # z=-2..11 OFF
    assert on_range == [(-44,-3), (12,38)]

    apply_instruction_to_z_range(on_range, instructions[5]) # z=-36..8
    assert on_range == [(-44,8), (12,38)]

    apply_instruction_to_z_range(on_range, instructions[6]) # z=7..25 OFF
    assert on_range == [(-44,6), (26,38)]

    apply_instruction_to_z_range(on_range, instructions[7]) # z=-34..11
    assert on_range == [(-44,11), (26,38)]

    apply_instruction_to_z_range(on_range, instructions[8]) # z=-11..5 OFF
    assert on_range == [(-44,-12), (6,11), (26,38)]

    apply_instruction_to_z_range(on_range, instructions[9]) # z=-16..29
    assert on_range == [(-44,38)]
