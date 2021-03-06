import networkx as nx


def grid_from_input_p1(input_string):
    grid = []
    for line in input_string.split("\n"):
        grid.append([int(c) for c in line])
    return grid


def wrap_1_to_9(n):
    return (n-1) % 9 + 1


def increase_risk_level(risk_list):
    return [wrap_1_to_9(x+1) for x in risk_list]


def grid_from_input_p2(input_string):
    initial_grid = grid_from_input_p1(input_string)
    y_size = len(initial_grid)
    x_size = len(initial_grid[0])

    # stretch each existing row to the right
    new_grid = []
    for row in initial_grid:
        new_row = row.copy()
        temp_row = row.copy()
        for _ in range(4):
            temp_row = increase_risk_level(temp_row)
            new_row += temp_row
        new_grid.append(new_row)

    # stretch those widened rows down
    offset = 0
    for _ in range(4):
        for i in range(offset+0, offset+y_size):
            new_row = increase_risk_level(new_grid[i])
            new_grid.append(new_row)
        offset += y_size

    return new_grid


def node_name(x, y):
    return f"({x},{y})"


def graph_from_grid(grid):
    DG = nx.DiGraph()

    # this isn't strictly necessary because nodes are added implicitly when edges are added, 
    # but it costs little and lets us keep the risk values associated with the node not just the edges
    for y, row in enumerate(grid):
        for x, cell in enumerate(row):
            DG.add_node(f"({x},{y})", node_risk=cell)

    x_size = len(grid[0])
    y_size = len(grid)
    for y, row in enumerate(grid):
        for x, cell in enumerate(row):
            # for each possible neighboring cell, if it exists, add an edge weighted by the risk of *that* cell
            # also add a reverse edge weighted by the risk of *this* cell
            for other_x, other_y in [
                (x-1, y), # left
                (x, y-1), # up
                (x+1, y), # right
                (x, y+1), # down
            ]:
                if 0 <= other_x < x_size and 0 <= other_y < y_size: # bounds check
                    this_node = node_name(x, y)
                    that_node = node_name(other_x, other_y)
                    out_edge_weight = grid[other_y][other_x]
                    in_edge_weight = grid[y][x]
                    DG.add_weighted_edges_from([(this_node, that_node, out_edge_weight)], weight="edge_risk")
                    DG.add_weighted_edges_from([(that_node, this_node, in_edge_weight)], weight="edge_risk")

    return DG


if __name__ == "__main__":
    input15 = open("input15", encoding="utf-8").read().strip()

    print("Part 1")
    grid_p1 = grid_from_input_p1(input15)
    graph_p1 = graph_from_grid(grid_p1)
    start_node_p1 = node_name(0, 0)
    finish_node_p1 = node_name(len(grid_p1)-1, len(grid_p1[0])-1)
    total_risk_p1 = nx.shortest_path_length(graph_p1, start_node_p1, finish_node_p1, "edge_risk")
    print(f"least total risk = {total_risk_p1}") # 415

    print("Part 2")
    grid_p2 = grid_from_input_p2(input15)
    graph_p2 = graph_from_grid(grid_p2)
    start_node_p2 = node_name(0, 0)
    finish_node_p2 = node_name(len(grid_p2)-1, len(grid_p2[0])-1)
    total_risk_p2 = nx.shortest_path_length(graph_p2, start_node_p2, finish_node_p2, "edge_risk")
    print(f"least total risk = {total_risk_p2}") # 


###############################################################################
##tests

SAMPLE_INPUT = """
1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581
""".strip()

def test_node_name():
    assert node_name(0, 0) == "(0,0)"
    assert node_name(0, 1) == "(0,1)"
    assert node_name(1, 0) == "(1,0)"
    assert node_name(1, 1) == "(1,1)"

def test_grid_from_input_p1_sample():
    expected = [
        [1,1,6,3,7,5,1,7,4,2],
        [1,3,8,1,3,7,3,6,7,2],
        [2,1,3,6,5,1,1,3,2,8],
        [3,6,9,4,9,3,1,5,6,9],
        [7,4,6,3,4,1,7,1,1,1],
        [1,3,1,9,1,2,8,1,3,7],
        [1,3,5,9,9,1,2,4,2,1],
        [3,1,2,5,4,2,1,6,3,9],
        [1,2,9,3,1,3,8,5,2,1],
        [2,3,1,1,9,4,4,5,8,1],
    ]
    computed = grid_from_input_p1(SAMPLE_INPUT)
    assert expected == computed

def test_graph_from_grid_sample():
    grid = grid_from_input_p1(SAMPLE_INPUT)
    graph = graph_from_grid(grid)

    # check size
    assert len(graph.nodes()) == 100

    # check node list
    for y, row in enumerate(grid):
        for x, cell in enumerate(row):
            assert node_name(x, y) in graph.nodes()

    # check edge weights
    assert graph.edges[node_name(2,0), node_name(2,1)] == {"edge_risk": 8}
    assert graph.edges[node_name(2,1), node_name(2,0)] == {"edge_risk": 6}

def test_shortest_path_sample():
    grid = grid_from_input_p1(SAMPLE_INPUT)
    graph = graph_from_grid(grid)

    total_risk = nx.shortest_path_length(graph, node_name(0, 0), node_name(9, 9), "edge_risk")
    assert total_risk == 40

def test_wrap_1_to_9():
    assert wrap_1_to_9(1) == 1
    assert wrap_1_to_9(2) == 2
    assert wrap_1_to_9(3) == 3
    assert wrap_1_to_9(4) == 4
    assert wrap_1_to_9(5) == 5
    assert wrap_1_to_9(6) == 6
    assert wrap_1_to_9(7) == 7
    assert wrap_1_to_9(8) == 8
    assert wrap_1_to_9(9) == 9
    assert wrap_1_to_9(10) == 1

def test_increase_risk_level():
    assert increase_risk_level([1,2,3,4,5]) == [2,3,4,5,6]
    assert increase_risk_level([8,9,1,2,3]) == [9,1,2,3,4]

def test_grid_from_input_p2_tiny():
    expected = [
        [8,9,1,2,3],
        [9,1,2,3,4],
        [1,2,3,4,5],
        [2,3,4,5,6],
        [3,4,5,6,7],
    ]
    computed = grid_from_input_p2("8")
    assert expected == computed

def test_grid_from_input_p2_sample():
    expected = [
        [1,1,6,3,7,5,1,7,4,2,2,2,7,4,8,6,2,8,5,3,3,3,8,5,9,7,3,9,6,4,4,4,9,6,1,8,4,1,7,5,5,5,1,7,2,9,5,2,8,6],
        [1,3,8,1,3,7,3,6,7,2,2,4,9,2,4,8,4,7,8,3,3,5,1,3,5,9,5,8,9,4,4,6,2,4,6,1,6,9,1,5,5,7,3,5,7,2,7,1,2,6],
        [2,1,3,6,5,1,1,3,2,8,3,2,4,7,6,2,2,4,3,9,4,3,5,8,7,3,3,5,4,1,5,4,6,9,8,4,4,6,5,2,6,5,7,1,9,5,5,7,6,3],
        [3,6,9,4,9,3,1,5,6,9,4,7,1,5,1,4,2,6,7,1,5,8,2,6,2,5,3,7,8,2,6,9,3,7,3,6,4,8,9,3,7,1,4,8,4,7,5,9,1,4],
        [7,4,6,3,4,1,7,1,1,1,8,5,7,4,5,2,8,2,2,2,9,6,8,5,6,3,9,3,3,3,1,7,9,6,7,4,1,4,4,4,2,8,1,7,8,5,2,5,5,5],
        [1,3,1,9,1,2,8,1,3,7,2,4,2,1,2,3,9,2,4,8,3,5,3,2,3,4,1,3,5,9,4,6,4,3,4,5,2,4,6,1,5,7,5,4,5,6,3,5,7,2],
        [1,3,5,9,9,1,2,4,2,1,2,4,6,1,1,2,3,5,3,2,3,5,7,2,2,3,4,6,4,3,4,6,8,3,3,4,5,7,5,4,5,7,9,4,4,5,6,8,6,5],
        [3,1,2,5,4,2,1,6,3,9,4,2,3,6,5,3,2,7,4,1,5,3,4,7,6,4,3,8,5,2,6,4,5,8,7,5,4,9,6,3,7,5,6,9,8,6,5,1,7,4],
        [1,2,9,3,1,3,8,5,2,1,2,3,1,4,2,4,9,6,3,2,3,4,2,5,3,5,1,7,4,3,4,5,3,6,4,6,2,8,5,4,5,6,4,7,5,7,3,9,6,5],
        [2,3,1,1,9,4,4,5,8,1,3,4,2,2,1,5,5,6,9,2,4,5,3,3,2,6,6,7,1,3,5,6,4,4,3,7,7,8,2,4,6,7,5,5,4,8,8,9,3,5],
        [2,2,7,4,8,6,2,8,5,3,3,3,8,5,9,7,3,9,6,4,4,4,9,6,1,8,4,1,7,5,5,5,1,7,2,9,5,2,8,6,6,6,2,8,3,1,6,3,9,7],
        [2,4,9,2,4,8,4,7,8,3,3,5,1,3,5,9,5,8,9,4,4,6,2,4,6,1,6,9,1,5,5,7,3,5,7,2,7,1,2,6,6,8,4,6,8,3,8,2,3,7],
        [3,2,4,7,6,2,2,4,3,9,4,3,5,8,7,3,3,5,4,1,5,4,6,9,8,4,4,6,5,2,6,5,7,1,9,5,5,7,6,3,7,6,8,2,1,6,6,8,7,4],
        [4,7,1,5,1,4,2,6,7,1,5,8,2,6,2,5,3,7,8,2,6,9,3,7,3,6,4,8,9,3,7,1,4,8,4,7,5,9,1,4,8,2,5,9,5,8,6,1,2,5],
        [8,5,7,4,5,2,8,2,2,2,9,6,8,5,6,3,9,3,3,3,1,7,9,6,7,4,1,4,4,4,2,8,1,7,8,5,2,5,5,5,3,9,2,8,9,6,3,6,6,6],
        [2,4,2,1,2,3,9,2,4,8,3,5,3,2,3,4,1,3,5,9,4,6,4,3,4,5,2,4,6,1,5,7,5,4,5,6,3,5,7,2,6,8,6,5,6,7,4,6,8,3],
        [2,4,6,1,1,2,3,5,3,2,3,5,7,2,2,3,4,6,4,3,4,6,8,3,3,4,5,7,5,4,5,7,9,4,4,5,6,8,6,5,6,8,1,5,5,6,7,9,7,6],
        [4,2,3,6,5,3,2,7,4,1,5,3,4,7,6,4,3,8,5,2,6,4,5,8,7,5,4,9,6,3,7,5,6,9,8,6,5,1,7,4,8,6,7,1,9,7,6,2,8,5],
        [2,3,1,4,2,4,9,6,3,2,3,4,2,5,3,5,1,7,4,3,4,5,3,6,4,6,2,8,5,4,5,6,4,7,5,7,3,9,6,5,6,7,5,8,6,8,4,1,7,6],
        [3,4,2,2,1,5,5,6,9,2,4,5,3,3,2,6,6,7,1,3,5,6,4,4,3,7,7,8,2,4,6,7,5,5,4,8,8,9,3,5,7,8,6,6,5,9,9,1,4,6],
        [3,3,8,5,9,7,3,9,6,4,4,4,9,6,1,8,4,1,7,5,5,5,1,7,2,9,5,2,8,6,6,6,2,8,3,1,6,3,9,7,7,7,3,9,4,2,7,4,1,8],
        [3,5,1,3,5,9,5,8,9,4,4,6,2,4,6,1,6,9,1,5,5,7,3,5,7,2,7,1,2,6,6,8,4,6,8,3,8,2,3,7,7,9,5,7,9,4,9,3,4,8],
        [4,3,5,8,7,3,3,5,4,1,5,4,6,9,8,4,4,6,5,2,6,5,7,1,9,5,5,7,6,3,7,6,8,2,1,6,6,8,7,4,8,7,9,3,2,7,7,9,8,5],
        [5,8,2,6,2,5,3,7,8,2,6,9,3,7,3,6,4,8,9,3,7,1,4,8,4,7,5,9,1,4,8,2,5,9,5,8,6,1,2,5,9,3,6,1,6,9,7,2,3,6],
        [9,6,8,5,6,3,9,3,3,3,1,7,9,6,7,4,1,4,4,4,2,8,1,7,8,5,2,5,5,5,3,9,2,8,9,6,3,6,6,6,4,1,3,9,1,7,4,7,7,7],
        [3,5,3,2,3,4,1,3,5,9,4,6,4,3,4,5,2,4,6,1,5,7,5,4,5,6,3,5,7,2,6,8,6,5,6,7,4,6,8,3,7,9,7,6,7,8,5,7,9,4],
        [3,5,7,2,2,3,4,6,4,3,4,6,8,3,3,4,5,7,5,4,5,7,9,4,4,5,6,8,6,5,6,8,1,5,5,6,7,9,7,6,7,9,2,6,6,7,8,1,8,7],
        [5,3,4,7,6,4,3,8,5,2,6,4,5,8,7,5,4,9,6,3,7,5,6,9,8,6,5,1,7,4,8,6,7,1,9,7,6,2,8,5,9,7,8,2,1,8,7,3,9,6],
        [3,4,2,5,3,5,1,7,4,3,4,5,3,6,4,6,2,8,5,4,5,6,4,7,5,7,3,9,6,5,6,7,5,8,6,8,4,1,7,6,7,8,6,9,7,9,5,2,8,7],
        [4,5,3,3,2,6,6,7,1,3,5,6,4,4,3,7,7,8,2,4,6,7,5,5,4,8,8,9,3,5,7,8,6,6,5,9,9,1,4,6,8,9,7,7,6,1,1,2,5,7],
        [4,4,9,6,1,8,4,1,7,5,5,5,1,7,2,9,5,2,8,6,6,6,2,8,3,1,6,3,9,7,7,7,3,9,4,2,7,4,1,8,8,8,4,1,5,3,8,5,2,9],
        [4,6,2,4,6,1,6,9,1,5,5,7,3,5,7,2,7,1,2,6,6,8,4,6,8,3,8,2,3,7,7,9,5,7,9,4,9,3,4,8,8,1,6,8,1,5,1,4,5,9],
        [5,4,6,9,8,4,4,6,5,2,6,5,7,1,9,5,5,7,6,3,7,6,8,2,1,6,6,8,7,4,8,7,9,3,2,7,7,9,8,5,9,8,1,4,3,8,8,1,9,6],
        [6,9,3,7,3,6,4,8,9,3,7,1,4,8,4,7,5,9,1,4,8,2,5,9,5,8,6,1,2,5,9,3,6,1,6,9,7,2,3,6,1,4,7,2,7,1,8,3,4,7],
        [1,7,9,6,7,4,1,4,4,4,2,8,1,7,8,5,2,5,5,5,3,9,2,8,9,6,3,6,6,6,4,1,3,9,1,7,4,7,7,7,5,2,4,1,2,8,5,8,8,8],
        [4,6,4,3,4,5,2,4,6,1,5,7,5,4,5,6,3,5,7,2,6,8,6,5,6,7,4,6,8,3,7,9,7,6,7,8,5,7,9,4,8,1,8,7,8,9,6,8,1,5],
        [4,6,8,3,3,4,5,7,5,4,5,7,9,4,4,5,6,8,6,5,6,8,1,5,5,6,7,9,7,6,7,9,2,6,6,7,8,1,8,7,8,1,3,7,7,8,9,2,9,8],
        [6,4,5,8,7,5,4,9,6,3,7,5,6,9,8,6,5,1,7,4,8,6,7,1,9,7,6,2,8,5,9,7,8,2,1,8,7,3,9,6,1,8,9,3,2,9,8,4,1,7],
        [4,5,3,6,4,6,2,8,5,4,5,6,4,7,5,7,3,9,6,5,6,7,5,8,6,8,4,1,7,6,7,8,6,9,7,9,5,2,8,7,8,9,7,1,8,1,6,3,9,8],
        [5,6,4,4,3,7,7,8,2,4,6,7,5,5,4,8,8,9,3,5,7,8,6,6,5,9,9,1,4,6,8,9,7,7,6,1,1,2,5,7,9,1,8,8,7,2,2,3,6,8],
        [5,5,1,7,2,9,5,2,8,6,6,6,2,8,3,1,6,3,9,7,7,7,3,9,4,2,7,4,1,8,8,8,4,1,5,3,8,5,2,9,9,9,5,2,6,4,9,6,3,1],
        [5,7,3,5,7,2,7,1,2,6,6,8,4,6,8,3,8,2,3,7,7,9,5,7,9,4,9,3,4,8,8,1,6,8,1,5,1,4,5,9,9,2,7,9,2,6,2,5,6,1],
        [6,5,7,1,9,5,5,7,6,3,7,6,8,2,1,6,6,8,7,4,8,7,9,3,2,7,7,9,8,5,9,8,1,4,3,8,8,1,9,6,1,9,2,5,4,9,9,2,1,7],
        [7,1,4,8,4,7,5,9,1,4,8,2,5,9,5,8,6,1,2,5,9,3,6,1,6,9,7,2,3,6,1,4,7,2,7,1,8,3,4,7,2,5,8,3,8,2,9,4,5,8],
        [2,8,1,7,8,5,2,5,5,5,3,9,2,8,9,6,3,6,6,6,4,1,3,9,1,7,4,7,7,7,5,2,4,1,2,8,5,8,8,8,6,3,5,2,3,9,6,9,9,9],
        [5,7,5,4,5,6,3,5,7,2,6,8,6,5,6,7,4,6,8,3,7,9,7,6,7,8,5,7,9,4,8,1,8,7,8,9,6,8,1,5,9,2,9,8,9,1,7,9,2,6],
        [5,7,9,4,4,5,6,8,6,5,6,8,1,5,5,6,7,9,7,6,7,9,2,6,6,7,8,1,8,7,8,1,3,7,7,8,9,2,9,8,9,2,4,8,8,9,1,3,1,9],
        [7,5,6,9,8,6,5,1,7,4,8,6,7,1,9,7,6,2,8,5,9,7,8,2,1,8,7,3,9,6,1,8,9,3,2,9,8,4,1,7,2,9,1,4,3,1,9,5,2,8],
        [5,6,4,7,5,7,3,9,6,5,6,7,5,8,6,8,4,1,7,6,7,8,6,9,7,9,5,2,8,7,8,9,7,1,8,1,6,3,9,8,9,1,8,2,9,2,7,4,1,9],
        [6,7,5,5,4,8,8,9,3,5,7,8,6,6,5,9,9,1,4,6,8,9,7,7,6,1,1,2,5,7,9,1,8,8,7,2,2,3,6,8,1,2,9,9,8,3,3,4,7,9],
    ]
    computed = grid_from_input_p2(SAMPLE_INPUT)
    assert expected == computed
