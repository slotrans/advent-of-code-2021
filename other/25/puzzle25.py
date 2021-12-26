def grid_from_input(input_string: str) -> list[list[str]]:
    grid = []
    for line in input_string.split("\n"):
        grid.append(list(line))
    return grid


def empty_grid(x_size: int, y_size: int) -> list[list[str]]:
    grid = []
    for row in range(0, y_size):
        grid.append(list('.' * x_size))
    return grid


def copy_grid(in_grid: list[list[str]]) -> list[list[str]]:
    out_grid = []
    for row in in_grid:
        out_grid.append(row.copy())
    return out_grid


def string_from_grid(grid: list[list[str]]) -> str:
    return "\n".join(["".join(line) for line in grid])


def step(grid: str) -> str:
    y_size = len(grid)
    x_size = len(grid[0])

    east_grid = copy_grid(grid)

    # east-facing ">"
    for y, row in enumerate(grid):
        new_y = y
        for x, cell in enumerate(row):
            if cell == '>':
                if grid[y][(x+1) % x_size] == '.':
                    new_x = (x+1) % x_size
                    east_grid[y][x] = '.'
                else:
                    new_x = x
                east_grid[new_y][new_x] = cell

    south_grid = copy_grid(east_grid)

    # south-facing "v"
    for y, row in enumerate(east_grid):
        for x, cell in enumerate(row):
            new_x = x
            if cell == 'v':
                if east_grid[(y+1) % y_size][x] == '.':
                    new_y = (y+1) % y_size
                    south_grid[y][x] = '.'
                else:
                    new_y = y
                south_grid[new_y][new_x] = cell

    return south_grid


def step_until_stable(grid: list[list[str]]) -> tuple[int, list[list[str]]]: # (steps, final_grid)
    steps = 0
    old_grid = grid
    new_grid = None
    while(True):
        steps += 1
        new_grid = step(old_grid)
        if new_grid == old_grid:
            return (steps, new_grid)

        old_grid = new_grid


if __name__ == "__main__":
    input25 = open("input25", encoding="utf-8").read().strip()

    print("Part 1")
    initial_grid = grid_from_input(input25)
    steps, final_grid = step_until_stable(initial_grid)
    print(f"(p1 answer) first step no sea cucumbers move = {steps}") # 513


###############################################################################

SAMPLE_INPUT_SMALL = """
...>...
.......
......>
v.....>
......>
.......
..vvv..
""".strip()

SAMPLE_INPUT_LARGE = """
v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>
""".strip()


def test_grid_from_input():
    expected = [
        [".", ".", ".", ">", ".", ".", "."],
        [".", ".", ".", ".", ".", ".", "."],
        [".", ".", ".", ".", ".", ".", ">"],
        ["v", ".", ".", ".", ".", ".", ">"],
        [".", ".", ".", ".", ".", ".", ">"],
        [".", ".", ".", ".", ".", ".", "."],
        [".", ".", "v", "v", "v", ".", "."],
    ]
    computed = grid_from_input(SAMPLE_INPUT_SMALL)
    assert expected == computed


def test_copy_grid():
    expected = grid_from_input(SAMPLE_INPUT_SMALL)
    computed = copy_grid(expected)
    assert expected == computed


def test_string_from_grid():
    assert SAMPLE_INPUT_SMALL == string_from_grid(grid_from_input(SAMPLE_INPUT_SMALL))
    assert SAMPLE_INPUT_LARGE == string_from_grid(grid_from_input(SAMPLE_INPUT_LARGE))


def test_step_small_sample():
    initial = grid_from_input(SAMPLE_INPUT_SMALL)

    expected1 = grid_from_input("""
..vv>..
.......
>......
v.....>
>......
.......
....v..""".strip())
    computed1 = step(initial)
    assert string_from_grid(expected1) == string_from_grid(computed1)

    expected2 = grid_from_input("""
....v>.
..vv...
.>.....
......>
v>.....
.......
.......
""".strip())
    computed2 = step(computed1)
    assert string_from_grid(expected2) == string_from_grid(computed2)

    expected3 = grid_from_input("""
......>
..v.v..
..>v...
>......
..>....
v......
.......""".strip())
    computed3 = step(computed2)
    assert string_from_grid(expected3) == string_from_grid(computed3)

    expected4 = grid_from_input("""
>......
..v....
..>.v..
.>.v...
...>...
.......
v......""".strip())
    computed4 = step(computed3)
    assert string_from_grid(expected4) == string_from_grid(computed4)


def test_step_large_sample():
    initial = grid_from_input(SAMPLE_INPUT_LARGE)

    expected1 = grid_from_input("""
....>.>v.>
v.v>.>v.v.
>v>>..>v..
>>v>v>.>.v
.>v.v...v.
v>>.>vvv..
..v...>>..
vv...>>vv.
>.v.v..v.v""".strip())
    computed1 = step(initial)
    assert string_from_grid(computed1) == string_from_grid(expected1)

    expected2 = grid_from_input("""
>.v.v>>..v
v.v.>>vv..
>v>.>.>.v.
>>v>v.>v>.
.>..v....v
.>v>>.v.v.
v....v>v>.
.vv..>>v..
v>.....vv.""".strip())
    computed2 = step(computed1)
    assert string_from_grid(computed2) == string_from_grid(expected2)

    expected3 = grid_from_input("""
v>v.v>.>v.
v...>>.v.v
>vv>.>v>..
>>v>v.>.v>
..>....v..
.>.>v>v..v
..v..v>vv>
v.v..>>v..
.v>....v..""".strip())
    computed3 = step(computed2)
    assert string_from_grid(computed3) == string_from_grid(expected3)

    expected10 = grid_from_input("""
..>..>>vv.
v.....>>.v
..v.v>>>v>
v>.>v.>>>.
..v>v.vv.v
.v.>>>.v..
v.v..>v>..
..v...>v.>
.vv..v>vv.""".strip())
    computedN = computed3
    for _ in range(7):
        computedN = step(computedN)
    assert string_from_grid(expected10) == string_from_grid(computedN)


def test_large_sample_stopping_point():
    initial = grid_from_input(SAMPLE_INPUT_LARGE)
    expected57 = grid_from_input("""
..>>v>vv..
..v.>>vv..
..>>v>>vv.
..>>>>>vv.
v......>vv
v>v....>>v
vvv.....>>
>vv......>
.>v.vv.v..""".strip())
    computed = initial
    for i in range(57):
        computed = step(computed)
        print(f"after {i}")
        print(string_from_grid(computed))
    assert string_from_grid(expected57) == string_from_grid(computed)

    # no movement from 57 -> 58
    expected58 = expected57
    computed = step(computed)
    assert string_from_grid(expected58) == string_from_grid(computed)


def test_step_until_stable():
    expected_final_grid = grid_from_input("""
..>>v>vv..
..v.>>vv..
..>>v>>vv.
..>>>>>vv.
v......>vv
v>v....>>v
vvv.....>>
>vv......>
.>v.vv.v..""".strip())
    initial = grid_from_input(SAMPLE_INPUT_LARGE)
    steps, final_grid = step_until_stable(initial)
    assert 58 == steps
    assert expected_final_grid == final_grid
