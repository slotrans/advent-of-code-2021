import sys
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


def compute_grid(line_data):
    x_max, y_max = 0, 0
    for l in line_data:
        x_max = max(x_max, l.x1, l.x2)
        y_max = max(y_max, l.y1, l.y2)

    #yxgrid = [[0] * (x_max+1)] * (y_max+1)
    yxgrid = []
    for y in range(0, y_max+1):
        yxgrid.append([0] * (x_max+1))

    for line in line_data:
        print(line, file=sys.stderr)
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
            print(f"neither H nor V: {line}", file=sys.stderr)
            continue
        print(get_printable_grid(yxgrid), file=sys.stderr)

    return yxgrid


def get_printable_grid(grid):
    out_lines = []
    for row in grid:
        out_lines.append("".join(["." if cell == 0 else str(cell) for cell in row]))
    return "\n".join(out_lines)


def get_p1_answer(grid):
    twos = 0
    for row in grid:
        for cell in row:
            if cell >= 2:
                twos += 1
    return twos


def run_sample():
    sample_input = """
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

    sample_output = """
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

    parsed_sample_input = parse_input(sample_input)
    grid = compute_grid(parsed_sample_input)
    pgrid = get_printable_grid(grid)
    print(pgrid)

    assert(pgrid == sample_output)

    sample_answer = get_p1_answer(grid)
    print(f"sample p1 answer: {sample_answer}")
    assert(sample_answer == 5)


def main():
    input05 = open("input05", encoding="utf-8").read().strip()
    parsed_input = parse_input(input05)
    grid = compute_grid(parsed_input)
    p1_answer = get_p1_answer(grid)
    print(f"p1 answer = {p1_answer}")


if __name__ == "__main__":
    #run_sample()
    main()
