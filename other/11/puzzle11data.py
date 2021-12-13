# data-structure-oriented python solution

from collections import namedtuple, defaultdict


Point = namedtuple("Point", ["x", "y"])


def grid_from_input(input_string):
    # grid cell values are the octopi's energy levels
    return [[int(c) for c in line] for line in input_string.split("\n")]


def print_grid(yxgrid):
    lines = []
    for row in yxgrid:
        lines.append("".join([str(c) if c <= 9 else "+" for c in row]))
    out = "\n".join(lines)
    print(out)


def get_adjacent_points(point, yxgrid):
    x, y = point.x, point.y
    xmax, ymax = len(yxgrid[0]), len(yxgrid)
    out = []
    for p in [Point(x-1, y-1), Point(x, y-1), Point(x+1, y-1), 
              Point(x-1, y),                  Point(x+1, y), 
              Point(x-1, y+1), Point(x, y+1), Point(x+1, y+1)]:
        if 0 <= p.x < xmax and 0 <= p.y < ymax:
            out.append(p)

    return out


def step_simulation(yxgrid, flash_counters):
    # "First, the energy level of each octopus increases by 1."
    points_to_flash = []
    for y, row in enumerate(yxgrid):
        for x, cell in enumerate(row):
            yxgrid[y][x] += 1
            if yxgrid[y][x] > 9:
                points_to_flash.append(Point(x, y))

    # "Then, any octopus with an energy level greater than 9 flashes."
    flashed_points = set()
    while len(points_to_flash) > 0:
        point = points_to_flash.pop()
        if point not in flashed_points:
            flashed_points.add(point)
            # "This increases the energy level of all adjacent octopuses by 1..."
            adjacent_points = get_adjacent_points(point, yxgrid)
            for ap in adjacent_points:
                yxgrid[ap.y][ap.x] += 1
                # If this causes an octopus to have an energy level greater than 9, it also flashes. 
                if yxgrid[ap.y][ap.x] > 9 and ap not in flashed_points:
                    points_to_flash.append(ap)

    # "Finally, any octopus that flashed during this step has its energy level set to 0, as it used all of its energy to flash."
    for fp in flashed_points:
        yxgrid[fp.y][fp.x] = 0
        flash_counters[fp] += 1
    if len(flashed_points) == (len(yxgrid) * len(yxgrid[0])): # all octopi flashed
        return True
    return False


def part1(input_string, steps) -> int: # returns flash count after steps
    grid = grid_from_input(input_string)
    counters = defaultdict(lambda: 0)
    for n in range(1,steps+1):
        print(f"simulating step {n}...")
        step_simulation(grid, counters)
    print("final grid:")
    print_grid(grid)

    total_flashes = sum(counters.values())
    return total_flashes


def part2(input_string) -> int: # returns step number of the first synchronized flash
    grid = grid_from_input(input_string)
    counters = defaultdict(lambda: 0)
    max_steps = 1024
    for n in range(1, max_steps+1):
        print(f"simulating step {n}...")
        if step_simulation(grid, counters):
            return n

    return -1


if __name__ == "__main__":
    input11 = open("input11", encoding="utf-8").read().strip()

    print("Part 1")
    part1_flashes = part1(input11, 100)
    print(f"(p1 answer) flashes after 100 steps: {part1_flashes}") # 1721

    print("Part 2")
    first_synchronized_flash = part2(input11)
    print(f"(p2 answer) all octopi flashed at step {first_synchronized_flash}") # 298


###############################################################################

SAMPLE_INPUT_SMALL = """
11111
19991
19191
19991
11111""".strip()

SAMPLE_INPUT_LARGE = """
5483143223
2745854711
5264556173
6141336146
6357385478
4167524645
2176841721
6882881134
4846848554
5283751526""".strip()


def test_grid_from_input():
    expected = [
        [1,1,1,1,1],
        [1,9,9,9,1],
        [1,9,1,9,1],
        [1,9,9,9,1],
        [1,1,1,1,1],
    ]
    computed = grid_from_input(SAMPLE_INPUT_SMALL)
    assert computed == expected


def test_step_simulation_on_small_sample():
    grid = [
        [1,1,1,1,1],
        [1,9,9,9,1],
        [1,9,1,9,1],
        [1,9,9,9,1],
        [1,1,1,1,1],
    ]
    expected_after_1 = [
        [3,4,5,4,3],
        [4,0,0,0,4],
        [5,0,0,0,5],
        [4,0,0,0,4],
        [3,4,5,4,3],
    ]
    expected_after_2 = [
        [4,5,6,5,4],
        [5,1,1,1,5],
        [6,1,1,1,6],
        [5,1,1,1,5],
        [4,5,6,5,4],
    ]
    counters = defaultdict(lambda: 0)
    step_simulation(grid, counters)
    assert grid == expected_after_1

    step_simulation(grid, counters)
    assert grid == expected_after_2


def test_step_simulation_on_large_sample():
    grid = [
        [5,4,8,3,1,4,3,2,2,3],
        [2,7,4,5,8,5,4,7,1,1],
        [5,2,6,4,5,5,6,1,7,3],
        [6,1,4,1,3,3,6,1,4,6],
        [6,3,5,7,3,8,5,4,7,8],
        [4,1,6,7,5,2,4,6,4,5],
        [2,1,7,6,8,4,1,7,2,1],
        [6,8,8,2,8,8,1,1,3,4],
        [4,8,4,6,8,4,8,5,5,4],
        [5,2,8,3,7,5,1,5,2,6],
    ]
    expected_after_1 = [
        [6,5,9,4,2,5,4,3,3,4],
        [3,8,5,6,9,6,5,8,2,2],
        [6,3,7,5,6,6,7,2,8,4],
        [7,2,5,2,4,4,7,2,5,7],
        [7,4,6,8,4,9,6,5,8,9],
        [5,2,7,8,6,3,5,7,5,6],
        [3,2,8,7,9,5,2,8,3,2],
        [7,9,9,3,9,9,2,2,4,5],
        [5,9,5,7,9,5,9,6,6,5],
        [6,3,9,4,8,6,2,6,3,7],
    ]
    expected_after_2 = [
        [8,8,0,7,4,7,6,5,5,5],
        [5,0,8,9,0,8,7,0,5,4],
        [8,5,9,7,8,8,9,6,0,8],
        [8,4,8,5,7,6,9,6,0,0],
        [8,7,0,0,9,0,8,8,0,0],
        [6,6,0,0,0,8,8,9,8,9],
        [6,8,0,0,0,0,5,9,4,3],
        [0,0,0,0,0,0,7,4,5,6],
        [9,0,0,0,0,0,0,8,7,6],
        [8,7,0,0,0,0,6,8,4,8],
    ]
    counters = defaultdict(lambda: 0)
    step_simulation(grid, counters)
    assert grid == expected_after_1

    step_simulation(grid, counters)
    assert grid == expected_after_2


def test_part1_on_small_sample():
    flashes = part1(SAMPLE_INPUT_SMALL, 2)
    assert flashes == 9


def test_part1_on_large_sample_10():
    flashes = part1(SAMPLE_INPUT_LARGE, 10)
    assert flashes == 204


def test_part1_on_large_sample_100():
    flashes = part1(SAMPLE_INPUT_LARGE, 100)
    assert flashes == 1656


def test_part2_on_large_sample():
    first_synchronized_flash = part2(SAMPLE_INPUT_LARGE)
    assert first_synchronized_flash == 195
