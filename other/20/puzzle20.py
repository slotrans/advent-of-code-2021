from collections import namedtuple, Counter


Point = namedtuple("Point", ["x", "y"])


def sparse_grid_from_lines(input_lines) -> dict:
    grid = {}
    for y, line in enumerate(input_lines):
        for x, pixel in enumerate(line):
            grid[Point(x,y)] = pixel
    grid["unknown_pixel"] = "."
    return grid


def get_nine_pixel_number(grid, center_point) -> [Point]:
    x, y = center_point
    unk = grid["unknown_pixel"]
    nine_pixel_string = "".join([
        grid.get(Point(x-1, y-1), unk), grid.get(Point(x, y-1), unk), grid.get(Point(x+1, y-1), unk),
        grid.get(Point(x-1, y), unk),   grid.get(Point(x, y), unk),   grid.get(Point(x+1, y), unk),
        grid.get(Point(x-1, y+1), unk), grid.get(Point(x, y+1), unk), grid.get(Point(x+1, y+1), unk),
    ])
    binary_string = nine_pixel_string.replace(".", "0").replace("#", "1")
    return int(binary_string, 2)


def get_enhanced_pixel(grid, algorithm, point) -> str:
    algo_idx = get_nine_pixel_number(grid=grid, center_point=point)
    enhanced_pixel = algorithm[algo_idx]
    return enhanced_pixel


def enhance_image(in_grid: dict, algorithm: str) -> dict:
    x_min, x_max = None, None
    y_min, y_max = None, None
    for point in in_grid.keys():
        if type(point) != Point: # hacky exception for "unknown_pixel"
            continue 
        if x_min is None or point.x < x_min:
            x_min = point.x
        if y_min is None or point.y < y_min:
            y_min = point.y
        if x_max is None or point.x > x_max:
            x_max = point.x
        if y_max is None or point.y > y_max:
            y_max = point.y


    out_grid = {}

    # process an additional 2 pixels around the border of the image
    for x in range(x_min-2, x_max+2+1):
        for y in range(y_min-2, y_max+2+1):
            point = Point(x,y)
            out_grid[point] = get_enhanced_pixel(in_grid, algorithm, point)
            #print(f"@({point.x},{point.y}) {in_grid[point]} -> {out_grid[point]}")    

    # for the infinite expanse, if it's all "off" then a 9-cell grid will eval to 0,
    # if it's all "on" then it will eval to 2^9-1 (511) a.k.a. the last char in algorithm
    if in_grid["unknown_pixel"] == ".":
        out_grid["unknown_pixel"] = algorithm[0]
    else: # "#"
        out_grid["unknown_pixel"] = algorithm[-1]

    return out_grid


if __name__ == "__main__":
    input20 = open("input20", encoding="utf-8").read().strip()
    algorithm = input20.split("\n\n")[0]
    input_lines = input20.split("\n\n")[1].split("\n")

    print("Part 1")
    image = sparse_grid_from_lines(input_lines)
    enhanced1 = enhance_image(image, algorithm)
    enhanced2 = enhance_image(enhanced1, algorithm)
    lit_pixels1 = Counter(enhanced2.values())["#"]
    print(f"(p1 answer) lit pixels after 2 enhance passes = {lit_pixels1}") # 5354
    # 5029, wrong too low
    # 5969, wrong too high

    print("Part 2")
    enhanced_muchly = image
    for _ in range(50):
        enhanced_muchly = enhance_image(enhanced_muchly, algorithm)
    lit_pixels2 = Counter(enhanced_muchly.values())["#"]
    print(f"(p2 answer) lit pixels after 50 enhance passes = {lit_pixels2}") # 18269
    

###############################################################################

def test_enhance_one_pixel_of_small_sample():
    algorithm = "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#"
    grid = sparse_grid_from_lines([
        "#..#.",
        "#....",
        "##..#",
        "..#..",
        "..###",
    ])
    computed = get_enhanced_pixel(grid, algorithm, Point(2,2))
    assert "#" == computed


def test_enhance_large_sample_2x():
    algorithm = "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#"
    grid = sparse_grid_from_lines([
        "...............",
        "...............",
        "...............",
        "...............",
        "...............",
        ".....#..#......",
        ".....#.........",
        ".....##..#.....",
        ".......#.......",
        ".......###.....",
        "...............",
        "...............",
        "...............",
        "...............",
        "...............",
    ])
    expected1 = sparse_grid_from_lines([
        "...............",
        "...............",
        "...............",
        "...............",
        ".....##.##.....",
        "....#..#.#.....",
        "....##.#..#....",
        "....####..#....",
        ".....#..##.....",
        "......##..#....",
        ".......#.#.....",
        "...............",
        "...............",
        "...............",
        "...............",    
    ])
    expected2 = sparse_grid_from_lines([
        "...............",
        "...............",
        "...............",
        "..........#....",
        "....#..#.#.....",
        "...#.#...###...",
        "...#...##.#....",
        "...#.....#.#...",
        "....#.#####....",
        ".....#.#####...",
        "......##.##....",
        ".......###.....",
        "...............",
        "...............",
        "...............",
    ])
    computed1 = enhance_image(grid, algorithm)
    #assert expected1 == computed1
    for x in range(0,10):
        for y in range(0,10):
            assert expected1[Point(x,y)] == computed1[Point(x,y)]
    computed2 = enhance_image(computed1, algorithm)
    #assert expected2 == computed2
    for x in range(0,10):
        for y in range(0,10):
            assert expected2[Point(x,y)] == computed2[Point(x,y)]
    lit_pixel_count = Counter(computed2.values())["#"]
    assert 35 == lit_pixel_count


def test_enhance_large_sample_50x():
    algorithm = "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#"
    grid = sparse_grid_from_lines([
        "...............",
        "...............",
        "...............",
        "...............",
        "...............",
        ".....#..#......",
        ".....#.........",
        ".....##..#.....",
        ".......#.......",
        ".......###.....",
        "...............",
        "...............",
        "...............",
        "...............",
        "...............",
    ])
    computed = grid
    for _ in range(50):
        computed = enhance_image(computed, algorithm)
    lit_pixel_count = Counter(computed.values())["#"]
    assert 3351 == lit_pixel_count
