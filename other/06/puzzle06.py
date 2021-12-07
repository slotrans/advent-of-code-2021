from collections import defaultdict


def step_simulation(fish_timers):
    new_fish = []
    for i in range(0, len(fish_timers)):
        ft = fish_timers[i]
        if ft == 0:
            new_fish.append(8) # spawn a fish
            fish_timers[i] = 6 # reset
        else:
            fish_timers[i] -= 1
    fish_timers += new_fish


def part1(fish_timers):
    print("Part 1")

    for day in range(1, 18+1):
        step_simulation(fish_timers)
        #print(f"{day}: {fish_timers}")
    print(f"fish after 18 days: {len(fish_timers)}") # 1638

    for day in range(19, 80+1):
        step_simulation(fish_timers)
        #print(f"{day}: {fish_timers}")
    print(f"(p1 answer) fish after 80 days: {len(fish_timers)}") # 362740


def step_simulation_optimized(timer_dict):
    new_timer_dict = defaultdict(lambda: 0)
    for timer_val in (8,7,6,5,4,3,2,1,0):
        fish_count = timer_dict[timer_val]
        if timer_val == 0:
            new_timer_dict[8] = fish_count # spawn
            new_timer_dict[6] += fish_count # reset
        else:
            new_timer_dict[timer_val-1] = fish_count # tick timers
    return new_timer_dict


def count_fish(timer_dict):
    return sum(timer_dict.values())


def part2(fish_timers):
    print("Part 2")

    timer_dict = defaultdict(lambda: 0)
    for ft in fish_timers:
        timer_dict[ft] += 1

    for day in range(1, 256+1):
        timer_dict = step_simulation_optimized(timer_dict)
    how_many = count_fish(timer_dict)
    print(f"(p2 answer) fish after 256 days: {how_many}") # 1644874076764


SAMPLE_FISH_TIMERS = [3,4,3,1,2]

def sample_part1(fish_timers):
    print("SAMPLE Part 1")

    for day in range(1, 18+1):
        step_simulation(fish_timers)
        print(f"{day}: {fish_timers}")
    print(f"fish after 18 days: {len(fish_timers)}")
    assert len(fish_timers) == 26

    for day in range(19, 80+1):
        step_simulation(fish_timers)
        #print(f"{day}: {fish_timers}")
    print(f"fish after 80 days: {len(fish_timers)}")
    assert len(fish_timers) == 5934


def sample_part2(fish_timers):
    print("SAMPLE Part 2")

    timer_dict = defaultdict(lambda: 0)
    for ft in fish_timers:
        timer_dict[ft] += 1

    for day in range(1, 18+1):
        timer_dict = step_simulation_optimized(timer_dict)
    how_many = count_fish(timer_dict)
    print(f"fish after 18 days: {how_many}")
    assert how_many == 26

    for day in range(19, 80+1):
        timer_dict = step_simulation_optimized(timer_dict)
    how_many = count_fish(timer_dict)
    print(f"fish after 80 days: {how_many}")
    assert how_many == 5934

    for day in range(81, 256+1):
        timer_dict = step_simulation_optimized(timer_dict)
    how_many = count_fish(timer_dict)
    print(f"fish after 256 days: {how_many}")
    assert how_many == 26984457539


if __name__ == "__main__":
    input06 = open("input06", encoding="utf-8").read().strip()
    fish_timers = [int(x) for x in input06.split(",")]

    #sample_part1(SAMPLE_FISH_TIMERS.copy())
    part1(fish_timers.copy())
    #sample_part2(SAMPLE_FISH_TIMERS.copy())
    part2(fish_timers.copy())
