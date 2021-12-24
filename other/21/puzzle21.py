from functools import cache
from collections import Counter


class DeterministicD100:
    def __init__(self):
        self.next_value = 1
        self.roll_count = 0

    def roll(self):
        ret = self.next_value
        
        self.next_value += 1
        if self.next_value > 100:
            self.next_value = 1
        
        self.roll_count += 1
        
        return ret


@cache
def move(current_space: int, roll: int) -> int: # new space
    return (current_space + roll - 1) % 10 + 1


def play_practice_game(p1_start: int, p2_start: int) -> dict:
    die = DeterministicD100()
    p1_pos, p2_pos = p1_start, p2_start
    p1_score, p2_score = 0, 0
    while(True):
        p1_roll = sum([die.roll(), die.roll(), die.roll()])
        p1_pos = move(p1_pos, p1_roll)
        p1_score += p1_pos
        if p1_score >= 1000:
            break

        p2_roll = sum([die.roll(), die.roll(), die.roll()])
        p2_pos = move(p2_pos, p2_roll)
        p2_score += p2_pos
        if p2_score >= 1000:
            break

    return {
        "p1_score": p1_score,
        "p2_score": p2_score,
        "winner": "p1" if p1_score >= 1000 else "p2",
        "roll_count": die.roll_count,
    }


def multiply_counter(c: Counter, x: int) -> Counter:
    return Counter({k: v*x for k,v in c.items()})    


@cache
def play_dirac_game_p1(p1_pos: int, p2_pos: int, p1_score: int, p2_score: int) -> Counter:
    if p2_score >= 21:
        return Counter([2])

    return (
        multiply_counter(play_dirac_game_p2(move(p1_pos, 3), p2_pos, p1_score+move(p1_pos, 3), p2_score), 1) +
        multiply_counter(play_dirac_game_p2(move(p1_pos, 4), p2_pos, p1_score+move(p1_pos, 4), p2_score), 3) +
        multiply_counter(play_dirac_game_p2(move(p1_pos, 5), p2_pos, p1_score+move(p1_pos, 5), p2_score), 6) +
        multiply_counter(play_dirac_game_p2(move(p1_pos, 6), p2_pos, p1_score+move(p1_pos, 6), p2_score), 7) +
        multiply_counter(play_dirac_game_p2(move(p1_pos, 7), p2_pos, p1_score+move(p1_pos, 7), p2_score), 6) +
        multiply_counter(play_dirac_game_p2(move(p1_pos, 8), p2_pos, p1_score+move(p1_pos, 8), p2_score), 3) +
        multiply_counter(play_dirac_game_p2(move(p1_pos, 9), p2_pos, p1_score+move(p1_pos, 9), p2_score), 1)
    )


@cache
def play_dirac_game_p2(p1_pos: int, p2_pos: int, p1_score: int, p2_score: int) -> Counter:
    if p1_score >= 21:
        return Counter([1])

    return (
        multiply_counter(play_dirac_game_p1(p1_pos, move(p2_pos, 3), p1_score, p2_score+move(p2_pos, 3)), 1) +
        multiply_counter(play_dirac_game_p1(p1_pos, move(p2_pos, 4), p1_score, p2_score+move(p2_pos, 4)), 3) +
        multiply_counter(play_dirac_game_p1(p1_pos, move(p2_pos, 5), p1_score, p2_score+move(p2_pos, 5)), 6) +
        multiply_counter(play_dirac_game_p1(p1_pos, move(p2_pos, 6), p1_score, p2_score+move(p2_pos, 6)), 7) +
        multiply_counter(play_dirac_game_p1(p1_pos, move(p2_pos, 7), p1_score, p2_score+move(p2_pos, 7)), 6) +
        multiply_counter(play_dirac_game_p1(p1_pos, move(p2_pos, 8), p1_score, p2_score+move(p2_pos, 8)), 3) +
        multiply_counter(play_dirac_game_p1(p1_pos, move(p2_pos, 9), p1_score, p2_score+move(p2_pos, 9)), 1)
    )


if __name__ == "__main__":
    input21 = open("input21", encoding="utf-8").read().strip()
    input_lines = input21.split("\n")
    p1_start = int(input_lines[0].split(": ")[1])
    p2_start = int(input_lines[1].split(": ")[1])

    print("Part 1")
    game_result = play_practice_game(p1_start, p2_start)
    print(f"result of practice game: {game_result}")
    losing_score = game_result["p2_score"] if game_result["winner"] == "p1" else game_result["p1_score"]
    part1_answer = losing_score * game_result["roll_count"]
    print(f"(p1 answer) losing score * roll count = {part1_answer}") # 518418


    print("Part 2")
    universe_counts = play_dirac_game_p1(p1_start, p2_start, 0, 0)
    part2_answer = max(universe_counts.values())
    print(f"(p2 answer) most winning universes = {part2_answer}") # 116741133558209


###############################################################################

def test_practice_game():
    expected_game_result = {
        "p1_score": 1000,
        "p2_score": 745,
        "winner": "p1",
        "roll_count": 993,
    }
    computed = play_practice_game(p1_start=4, p2_start=8)
    assert expected_game_result == computed


def test_dirac_game():
    expected = Counter({1: 444356092776315, 2: 341960390180808})
    computed = play_dirac_game_p1(p1_pos=4, p2_pos=8, p1_score=0, p2_score=0)
    assert expected == computed
