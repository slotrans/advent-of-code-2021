input01 = open('input01', encoding='utf-8').read().strip()

measurements = [int(line) for line in input01.split("\n")]

increases = 0
last_depth = measurements[0]
for depth in measurements[1:]:
    if depth > last_depth:
        increases += 1
    last_depth = depth

print(f"(p1 answer) increases = {increases}") #answer: 1548


increases = 0
last_window = measurements[0:3]
for i in range(1, len(measurements)-2):
    window = measurements[i:i+3]
    if sum(window) > sum(last_window):
        increases += 1
    last_window = window

print(f"(p2 answer) increases = {increases}") #answer: 1589
