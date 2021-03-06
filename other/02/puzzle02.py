input02 = open("input02", encoding="utf-8").read().strip()

instructions = input02.split("\n")

position = 0
depth = 0
for inst in instructions:
    direction, value_str = inst.split(" ")
    value = int(value_str)

    if direction == "forward":
        position += value
    elif direction == "up":
        depth -= value
    elif direction == "down":
        depth += value
    else:
        raise ValueError(f"unexpected direction: {direction}")

print(f"position = {position}, depth = {depth}") # 2003, 980
p1_answer = position * depth
print(f"(p1 answer) position * depth = {p1_answer}") # 1962940


position = 0
depth = 0
aim = 0
for inst in instructions:
    direction, value_str = inst.split(" ")
    value = int(value_str)

    if direction == "down":
        aim += value
    elif direction == "up":
        aim -= value
    elif direction == "forward":
        position += value
        depth += value * aim
    else:
        raise ValueError(f"unexpected direction: {direction}")

print(f"position = {position}, depth = {depth}") # 2003, 905474
p2_answer = position * depth
print(f"(p2 answer) position * depth = {p2_answer}") # 1813664422
