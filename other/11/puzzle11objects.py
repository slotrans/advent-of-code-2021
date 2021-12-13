from collections import namedtuple


Point = namedtuple("Point", ["x", "y"])


class Octopus:
    def __init__(self, energy):
        self.energy = energy

        self.flashes = 0
        self.has_flashed = False

    def add_energy(self) -> None:
        self.energy += 1

    def flash(self) -> None:
        self.has_flashed = True

    def should_flash(self) -> bool:
        return self.energy > 9 and not self.has_flashed

    def reset_if_needed(self) -> bool:
        if self.has_flashed:
            self.flashes += 1
            self.energy = 0
            self.has_flashed = False
            return True
        return False


class OctopusGrid:
    def __init__(self, x_size, y_size):
        self.x_size = x_size
        self.y_size = y_size

        self.sparse_grid = {}

    @staticmethod
    def from_input_string(input_string):
        input_lines = input_string.split("\n")
        x_size = len(input_lines[0])
        y_size = len(input_lines)
        octo_grid = OctopusGrid(x_size, y_size)

        for y, line in enumerate(input_lines):
            for x, cell in enumerate(line):
                octo_grid.sparse_grid[Point(x, y)] = Octopus(int(cell))

        return octo_grid

    def nearby_points(self, ref_point): # -> list of points
        x, y = ref_point
        points = [
            Point(x-1, y-1), Point(x, y-1), Point(x+1, y-1),
            Point(x-1, y),                  Point(x+1, y),
            Point(x-1, y+1), Point(x, y+1), Point(x+1, y+1),
        ]
        present_points = list(filter(lambda p: p in self.sparse_grid, points))
        return present_points

    def step_simulation(self) -> bool:
        # "First, the energy level of each octopus increases by 1."
        flash_pending_points = []
        for point, octopus in self.sparse_grid.items():
            octopus.add_energy()
            if octopus.should_flash():
                flash_pending_points.append(point)

        # "Then, any octopus with an energy level greater than 9 flashes."
        while len(flash_pending_points) > 0:
            point = flash_pending_points.pop()
            flashing_octopus = self.sparse_grid[point]
            if flashing_octopus.should_flash():
                flashing_octopus.flash()
                # "This increases the energy level of all adjacent octopuses by 1"
                for nb_point in self.nearby_points(point):
                    neighbor = self.sparse_grid[nb_point]
                    neighbor.add_energy()
                    # "If this causes an octopus to have an energy level greater than 9, it also flashes."
                    if neighbor.should_flash():
                        flash_pending_points.append(nb_point)

        # "Finally, any octopus that flashed during this step has its energy level set to 0, as it used all of its energy to flash."
        step_flashes = 0
        for o in self.sparse_grid.values():
            if o.reset_if_needed():
                step_flashes += 1
        if step_flashes == len(self.sparse_grid): # all octopi flashed
            return True
        return False

    def total_flashes(self) -> int:
        return sum([o.flashes for o in self.sparse_grid.values()])

    def __str__(self):
        out = ""
        for y in range(0, self.y_size):
            for x in range(0, self.x_size):
                energy = self.sparse_grid[Point(x, y)].energy
                out += str(energy) if energy < 9 else "."
            if y < self.y_size-1:
                out += "\n"
        return out


def part1(input_string, steps) -> int:
    octo_grid = OctopusGrid.from_input_string(input_string)
    print("initial state:")
    print(octo_grid)

    for i in range(1, steps+1):
        octo_grid.step_simulation()
        #print(f"after {i} steps")
        #print(octo_grid)

    return octo_grid.total_flashes()


def part2(input_string) -> int:
    max_steps = 1024
    octo_grid = OctopusGrid.from_input_string(input_string)

    for i in range(1, max_steps+1):
        print(f"simulating step {i}...")
        if octo_grid.step_simulation():
            return i

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

def assert_listgrid_matches_OctopusGrid(grid, og):
    for y, row in enumerate(grid):
        for x, cell in enumerate(row):
            assert cell == og.sparse_grid[Point(x, y)].energy


def test_grid_from_input():
    expected_energies = [
        [1,1,1,1,1],
        [1,9,9,9,1],
        [1,9,1,9,1],
        [1,9,9,9,1],
        [1,1,1,1,1],
    ]
    computed = OctopusGrid.from_input_string(SAMPLE_INPUT_SMALL)
    assert_listgrid_matches_OctopusGrid(expected_energies, computed)


def test_step_simulation_on_small_sample():
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
    octo_grid = OctopusGrid.from_input_string(SAMPLE_INPUT_SMALL)

    octo_grid.step_simulation()
    assert_listgrid_matches_OctopusGrid(expected_after_1, octo_grid)

    octo_grid.step_simulation()
    assert_listgrid_matches_OctopusGrid(expected_after_2, octo_grid)


def test_step_simulation_on_large_sample():
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
    expected_after_10 = [
        [0,4,8,1,1,1,2,9,7,6],
        [0,0,3,1,1,1,2,0,0,9],
        [0,0,4,1,1,1,2,5,0,4],
        [0,0,8,1,1,1,1,4,0,6],
        [0,0,9,9,1,1,1,3,0,6],
        [0,0,9,3,5,1,1,2,3,3],
        [0,4,4,2,3,6,1,1,3,0],
        [5,5,3,2,2,5,2,3,5,0],
        [0,5,3,2,2,5,0,6,0,0],
        [0,0,3,2,2,4,0,0,0,0],
    ]
    octo_grid = OctopusGrid.from_input_string(SAMPLE_INPUT_LARGE)

    octo_grid.step_simulation()
    assert_listgrid_matches_OctopusGrid(expected_after_1, octo_grid)

    octo_grid.step_simulation()
    assert_listgrid_matches_OctopusGrid(expected_after_2, octo_grid)

    for _ in range(8):
        octo_grid.step_simulation()
    assert_listgrid_matches_OctopusGrid(expected_after_10, octo_grid)


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
    octo_grid = OctopusGrid.from_input_string(SAMPLE_INPUT_LARGE)
    first_synchronized_flash = part2(SAMPLE_INPUT_LARGE)
    assert first_synchronized_flash == 195
