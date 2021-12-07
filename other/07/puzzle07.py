
def minimize_fuel(positions):
    min_position = min(positions)
    max_position = max(positions)
    #print(f"searching from {min_position} to {max_position}")
    best_total_fuel = 2**62
    best_position = None
    for possible_position in range(min_position, max_position+1):
        total_fuel = sum([abs(x-possible_position) for x in positions])
        #print(f"total fuel to move to position {possible_position}: {total_fuel}")
        if total_fuel < best_total_fuel:
            best_total_fuel = total_fuel
            best_position = possible_position

    return best_position, best_total_fuel


SAMPLE_INPUT = "16,1,2,0,4,2,7,1,2,14"

def sample_part1():
    print("SAMPLE Part 1")
    positions = [int(x) for x in SAMPLE_INPUT.split(",")]
    best_position, total_fuel = minimize_fuel(positions)
    print(f"best position = {best_position}, total_fuel = {total_fuel}")
    assert best_position == 2
    assert total_fuel == 37


def part1(positions):
    print("Part 1")
    best_position, total_fuel = minimize_fuel(positions)
    print(f"best position = {best_position}, (p1 answer) total_fuel = {total_fuel}") # 


if __name__ == "__main__":
    input07 = open("input07", encoding="utf-8").read().strip()
    initial_positions = [int(x) for x in input07.split(",")]

    sample_part1()
    part1(initial_positions)
