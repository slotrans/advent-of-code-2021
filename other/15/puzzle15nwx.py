import networkx as nx


def grid_from_input(input_string):
    grid = []
    for line in input_string.split("\n"):
        grid.append([int(c) for c in line])
    return grid


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
    grid = grid_from_input(input15)
    graph = graph_from_grid(grid)
    start_node = node_name(0, 0)
    finish_node = node_name(len(grid)-1, len(grid[0])-1)
    total_risk = nx.shortest_path_length(graph, start_node, finish_node, "edge_risk")
    print(f"least total risk = {total_risk}") # 415


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

def test_grid_from_input_sample():
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
    computed = grid_from_input(SAMPLE_INPUT)
    assert expected == computed

def test_graph_from_grid_sample():
    grid = grid_from_input(SAMPLE_INPUT)
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
    grid = grid_from_input(SAMPLE_INPUT)
    graph = graph_from_grid(grid)

    total_risk = nx.shortest_path_length(graph, node_name(0, 0), node_name(9, 9), "edge_risk")
    assert total_risk == 40

