from collections import namedtuple

Line = namedtuple("Line", ["x1", "y1", "x2", "y2"])

def parse_input(blob):
    data = []
    for line in blob.split("\n"):
        first, second = line.split(" -> ")
        x1, y1 = first.split(",")
        x2, y2 = second.split(",")
        data.append(Line(int(x1), int(y1), int(x2), int(y2)))

    return data


def is_horizontal(line):
    return line.y1 == line.y2


def is_vertical(line):
    return line.x1 == line.x2


def compute_grid_p1(line_data):
    x_max, y_max = 0, 0
    for l in line_data:
        x_max = max(x_max, l.x1, l.x2)
        y_max = max(y_max, l.y1, l.y2)

    #yxgrid = [[0] * (x_max+1)] * (y_max+1)  this doesn't work because each row ends up with a reference to the *same* list
    yxgrid = [[0] * (x_max+1) for _ in range(0, y_max+1)]

    for line in line_data:
        #print(line)
        if is_horizontal(line):
            y = line.y1
            start, stop = min(line.x1, line.x2), max(line.x1, line.x2) # arguments to range() must be in ascending order
            for x in range(start, stop+1):
                yxgrid[y][x] += 1
        elif is_vertical(line):
            x = line.x1
            start, stop = min(line.y1, line.y2), max(line.y1, line.y2)
            for y in range(start, stop+1):
                yxgrid[y][x] += 1
        else:
            #print(f"neither H nor V: {line}")
            continue
        #print(get_printable_grid(yxgrid))

    return yxgrid


def compute_grid_p2(line_data):
    x_max, y_max = 0, 0
    for l in line_data:
        x_max = max(x_max, l.x1, l.x2)
        y_max = max(y_max, l.y1, l.y2)

    yxgrid = [[0] * (x_max+1) for _ in range(0, y_max+1)]

    for line in line_data:
        #print(line)
        if is_horizontal(line):
            y = line.y1
            start, stop = min(line.x1, line.x2), max(line.x1, line.x2) # arguments to range() must be in ascending order
            for x in range(start, stop+1):
                yxgrid[y][x] += 1
        elif is_vertical(line):
            x = line.x1
            start, stop = min(line.y1, line.y2), max(line.y1, line.y2)
            for y in range(start, stop+1):
                yxgrid[y][x] += 1
        else: # 45-degree diagonal
            x_step = 1 if line.x2 >= line.x1 else -1
            y_step = 1 if line.y2 >= line.y1 else -1
            x, y = line.x1, line.y1
            should_stop = False
            while not should_stop:
                if x == line.x2 and y == line.y2:
                    should_stop = True
                yxgrid[y][x] += 1
                x += x_step
                y += y_step

        #print(get_printable_grid(yxgrid))

    return yxgrid


def get_printable_grid(grid):
    out_lines = []
    for row in grid:
        out_lines.append("".join(["." if cell == 0 else str(cell) for cell in row]))
    return "\n".join(out_lines)


def get_answer(grid):
    twos = 0
    for row in grid:
        for cell in row:
            if cell >= 2:
                twos += 1
    return twos


SAMPLE_INPUT = """
0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2""".strip()

SAMPLE_OUTPUT_P1 = """
.......1..
..1....1..
..1....1..
.......1..
.112111211
..........
..........
..........
..........
222111....
""".strip()

SAMPLE_OUTPUT_P2 = """
1.1....11.
.111...2..
..2.1.111.
...1.2.2..
.112313211
...1.2....
..1...1...
.1.....1..
1.......1.
222111....
""".strip()

def run_sample_p1():
    print("P1 SAMPLE")
    parsed_sample_input = parse_input(SAMPLE_INPUT)
    grid = compute_grid_p1(parsed_sample_input)
    pgrid = get_printable_grid(grid)
    print(pgrid)

    assert pgrid == SAMPLE_OUTPUT_P1

    sample_answer = get_answer(grid)
    print(f"sample p1 answer: {sample_answer}")
    assert sample_answer == 5


def run_sample_p2():
    print("P2 SAMPLE")
    parsed_sample_input = parse_input(SAMPLE_INPUT)
    grid = compute_grid_p2(parsed_sample_input)
    pgrid = get_printable_grid(grid)
    print(pgrid)

    assert pgrid == SAMPLE_OUTPUT_P2

    sample_answer = get_answer(grid)
    print(f"sample p2 answer: {sample_answer}")
    assert sample_answer == 12


def main():
    input05 = open("input05", encoding="utf-8").read().strip()
    parsed_input = parse_input(input05)

    print("Part 1:")
    grid = compute_grid_p1(parsed_input)
    p1_answer = get_answer(grid)
    print(f"(p1 answer) points with 2+ overlaps = {p1_answer}") # 5442

    print("Part 2:")
    grid = compute_grid_p2(parsed_input)
    p2_answer = get_answer(grid)
    print(f"(p2 answer) points with 2+ overlaps = {p2_answer}") # 19571


if __name__ == "__main__":
    #run_sample_p1()
    #run_sample_p2()
    main()
