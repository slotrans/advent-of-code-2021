import re
from math import floor, ceil


def find_exploding_pair(sf) -> (str, int): # (match, pos)
    depth = 0
    for i in range(len(sf)):
        if sf[i] == "[":
            if depth < 4:
                depth += 1
            else:
                return re.search(r"\[[0-9]+,[0-9]+\]", sf[i:])[0], i

        elif sf[i] == "]":
            depth -= 1

    return None, None


def sf_explode(sf) -> (str, bool): # (new string, changed flag)
    target, pos = find_exploding_pair(sf)
    if target is None:
        return sf, False

    print(f"exploding {target}... {sf}")

    left_remaining = sf[0:pos]
    right_remaining = sf[pos+len(target):]
    #print(f"L/R remaining: {left_remaining} | {right_remaining}")

    exp_left, exp_right = map(int, target.strip("[]").split(","))
    recip_left = list(re.finditer(r"[0-9]+", left_remaining))
    recip_right = list(re.finditer(r"[0-9]+", right_remaining))

    if recip_left:
        left_match = recip_left[-1]
        #print(f"left_match={left_match}")
        recip_left_start = left_match.start(0)
        recip_left_end = left_match.end(0)
        recip_left_num = int(left_match[0])
        new_left_num = recip_left_num + exp_left
        left_remaining = left_remaining[0:recip_left_start] + str(new_left_num) + left_remaining[recip_left_end:]

    if recip_right:
        right_match = recip_right[0]
        #print(f"right_match={right_match}")
        recip_right_start = right_match.start(0)
        recip_right_end = right_match.end(0)
        recip_right_num = int(right_match[0])
        new_right_num = recip_right_num + exp_right
        right_remaining = right_remaining[0:recip_right_start] + str(new_right_num) + right_remaining[recip_right_end:]

    new_sf = f"{left_remaining}0{right_remaining}"
    #print(f"new_sf={new_sf}")

    return new_sf, True


def find_splitting_number(sf) -> (str, int): # (match, pos)
    match = re.search(r"[0-9]{2,}", sf) # equivalent to looking for numbers > 9
    if match:
        return match[0], match.start(0)
    else:
        return None, None


def sf_split(sf) -> (str, bool): # (new string, changed flag)
    target, pos = find_splitting_number(sf)
    if target is None:
        return sf, False

    print(f"splitting {target}... {sf}")

    left_remaining = sf[0:pos]
    right_remaining = sf[pos+len(target):]

    new_left = floor(int(target) / 2)
    new_right = ceil(int(target) / 2)

    new_sf = f"{left_remaining}[{new_left},{new_right}]{right_remaining}"

    return new_sf, True


def sf_reduce(sf) -> (str, bool): # (new string, changed flag)
    new_sf, changed = sf_explode(sf)
    if changed:
        return new_sf, changed

    new_sf, changed = sf_split(sf)
    if changed:
        return new_sf, changed

    return sf, False


def sf_reduce_fully(sf) -> str: # new string
    new_sf, changed = sf_reduce(sf)
    while changed:
        print(f"continuing reduction...")
        new_sf, changed = sf_reduce(new_sf)
    print("reduction complete")
    return new_sf


def sf_add(a, b) -> str: # new string
    return f"[{a},{b}]"


def sf_add_with_reduction(a, b) -> str: # new string
    new_sf = sf_add(a, b)
    return sf_reduce_fully(new_sf)


def sf_magnitude(sf) -> int:
    if sf[0] == "[":
        comma_idx = None
        close_bracket_idx = None
        depth = 0

        for i in range(1,len(sf)):
            if sf[i] == "]" and depth == 0:
                close_bracket_idx = i
                break

            if sf[i] == "," and depth == 0:
                comma_idx = i

            if sf[i] == "[":
                depth += 1
            elif sf[i] == "]":
                depth -= 1

        return (3 * sf_magnitude(sf[1:comma_idx])) + (2 * sf_magnitude(sf[comma_idx+1:close_bracket_idx]))

    elif sf[0] == "]":
        raise Exception("unexpected ]")
    elif sf[0] == ",":
        raise Exception("unexpected ,")
    else:
        num_string = sf[0]
        for i in range(1,len(sf)):
            if sf[i].isdigit():
                num_string += sf[i]
            else:
                break

        return int(num_string)


def sf_list_magnitude(sf_list):
    acc = sf_list[0]
    for sf in sf_list[1:]:
        acc = sf_add_with_reduction(acc, sf)
    return sf_magnitude(acc)    


def sf_max_pair_magnitude(sf_list):
    max_magnitude = 0
    for i in range(len(sf_list)):
        for j in range(len(sf_list)):
            if i == j:
                continue
            magnitude = sf_magnitude(sf_add_with_reduction(sf_list[i], sf_list[j]))
            max_magnitude = max(max_magnitude, magnitude)
    return max_magnitude


if __name__ == "__main__":
    input18 = open("input18", encoding="utf-8").read().strip()

    print("Part 1")
    input_lines = input18.split("\n")
    magnitude_of_input = sf_list_magnitude(input_lines)
    print(f"(p1 answer) magnitude of all input added = {magnitude_of_input}") # 3734

    print("Part 2")
    max_pair_magnitude = sf_max_pair_magnitude(input_lines)
    print(f"(p2 answer) max magnitude from adding two numbers = {max_pair_magnitude}") # 4837


###############################################################################

def test_explode_example_1():
    computed = sf_explode("[[[[[9,8],1],2],3],4]")
    expected = ("[[[[0,9],2],3],4]", True)
    assert expected == computed

def test_explode_example_2():
    computed = sf_explode("[7,[6,[5,[4,[3,2]]]]]")
    expected = ("[7,[6,[5,[7,0]]]]", True)
    assert expected == computed

def test_explode_example_3():
    computed = sf_explode("[[6,[5,[4,[3,2]]]],1]")
    expected = ("[[6,[5,[7,0]]],3]", True)
    assert expected == computed

def test_explode_example_4():
    computed = sf_explode("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")
    expected = ("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", True)
    assert expected == computed

def test_explode_example_5():
    computed = sf_explode("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
    expected = ("[[3,[2,[8,0]]],[9,[5,[7,0]]]]", True)
    assert expected == computed

def test_explode_nothing_to_do():
    computed = sf_explode("[[[[0,9],2],3],4]")
    expected = ("[[[[0,9],2],3],4]", False)
    assert expected == computed


def test_split_example_1():
    computed = sf_split("[[[[0,7],4],[15,[0,13]]],[1,1]]")
    expected = ("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]", True)
    assert expected == computed

def test_split_example_2():
    computed = sf_split("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")
    expected = ("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]", True)
    assert expected == computed


def test_reduce_stepped():
    sf = "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]"

    after1 = ("[[[[0,7],4],[7,[[8,4],9]]],[1,1]]", True)
    sf, changed = sf_reduce(sf)
    assert after1 == (sf, changed)

    after2 = ("[[[[0,7],4],[15,[0,13]]],[1,1]]", True)
    sf, changed = sf_reduce(sf)
    assert after2 == (sf, changed)

    after3 = ("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]", True)
    sf, changed = sf_reduce(sf)
    assert after3 == (sf, changed)

    after4 = ("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]", True)
    sf, changed = sf_reduce(sf)
    assert after4 == (sf, changed)

    after5 = ("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", True)
    sf, changed = sf_reduce(sf)
    assert after5 == (sf, changed)

def test_reduce_fully():
    sf = "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]"
    expected = "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"
    computed = sf_reduce_fully(sf)
    assert expected == computed


def test_adding_example_1():
    acc = "[1,1]"
    for x in [
        "[2,2]",
        "[3,3]",
        "[4,4]",
    ]:
        acc = sf_add_with_reduction(acc, x)
    assert acc == "[[[[1,1],[2,2]],[3,3]],[4,4]]"

def test_adding_example_2():
    acc = "[1,1]"
    for x in [
        "[2,2]",
        "[3,3]",
        "[4,4]",
        "[5,5]",
    ]:
        acc = sf_add_with_reduction(acc, x)
    assert acc == "[[[[3,0],[5,3]],[4,4]],[5,5]]"

def test_adding_example_3():
    acc = "[1,1]"
    for x in [
        "[2,2]",
        "[3,3]",
        "[4,4]",
        "[5,5]",
        "[6,6]",
    ]:
        acc = sf_add_with_reduction(acc, x)
    assert acc == "[[[[5,0],[7,4]],[5,5]],[6,6]]"

def test_adding_example_4():
    acc = "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"
    for x in [
        "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
        "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
        "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
        "[7,[5,[[3,8],[1,4]]]]",
        "[[2,[2,2]],[8,[8,1]]]",
        "[2,9]",
        "[1,[[[9,3],9],[[9,0],[0,7]]]]",
        "[[[5,[7,4]],7],1]",
        "[[[[4,2],2],6],[8,7]]",
    ]:
        acc = sf_add_with_reduction(acc, x)
    assert acc == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"


def test_magnitude_1():
    expected = 129
    computed = sf_magnitude("[[9,1],[1,9]]")
    assert computed == expected

def test_magnitude_2():
    expected = 143
    computed = sf_magnitude("[[1,2],[[3,4],5]]")
    assert expected == computed

def test_magnitude_3():    
    expected = 1384
    computed = sf_magnitude("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
    assert expected == computed

def test_magnitude_4():    
    expected = 445
    computed = sf_magnitude("[[[[1,1],[2,2]],[3,3]],[4,4]]")
    assert expected == computed

def test_magnitude_5():    
    expected = 791
    computed = sf_magnitude("[[[[3,0],[5,3]],[4,4]],[5,5]]")
    assert expected == computed

def test_magnitude_6():    
    expected = 1137
    computed = sf_magnitude("[[[[5,0],[7,4]],[5,5]],[6,6]]")
    assert expected == computed

def test_magnitude_7():    
    expected = 3488
    computed = sf_magnitude("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
    assert expected == computed


def test_magnitude_of_list(): # part 1
    input_list = [
        "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
        "[[[5,[2,8]],4],[5,[[9,9],0]]]",
        "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
        "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
        "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
        "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
        "[[[[5,4],[7,7]],8],[[8,3],8]]",
        "[[9,3],[[9,9],[6,[4,9]]]]",
        "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
        "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]",
    ]
    expected = 4140
    computed = sf_list_magnitude(input_list)
    assert expected == computed


def test_max_magnitude_of_pair(): # part 2
    input_list = [
        "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
        "[[[5,[2,8]],4],[5,[[9,9],0]]]",
        "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
        "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
        "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
        "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
        "[[[[5,4],[7,7]],8],[[8,3],8]]",
        "[[9,3],[[9,9],[6,[4,9]]]]",
        "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
        "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]",
    ]
    expected = 3993
    computed = sf_max_pair_magnitude(input_list)
    assert expected == computed
