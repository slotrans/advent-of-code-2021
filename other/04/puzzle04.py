import re
import sys


def mark(board, number) -> None:
    for x in range(0, 5):
        for y in range(0, 5):
            if board[(x,y)][0] == number:
                board[(x,y)] = (number, True)


def is_winner(board) -> bool:
    # test rows
    for y in range(0, 5):
        row_marks = [board[(x,y)][1] for x in range(0, 5)]
        if row_marks == [True]*5:
            return True

    # test columns
    for x in range(0, 5):
        col_marks = [board[(x,y)][1] for y in range(0,5)]
        if col_marks == [True]*5:
            return True

    return False


def get_score(board, winning_number) -> int:
    total = sum([x[0] for x in board.values() if not x[1]])
    return total * winning_number


if __name__ == "__main__":
    input04 = open("input04", encoding="utf-8").read().strip()
    input04_chunks = input04.split("\n\n")

    bingo_sequence = [int(x) for x in input04_chunks[0].split(",")]

    board_list = []
    for chunk in input04_chunks[1:]:
        numbers = [int(x) for x in re.split("\\s+", chunk.strip())]
        board = {}
        for i in range(0,25):
            y = i // 5
            x = i % 5
            board[(x,y)] = (numbers[i], False) # tuple is (number, marked)
        board_list.append(board)


    print("Part 1:")

    for bingo_num in bingo_sequence:
        print(f"marking {bingo_num}...")

        for board in board_list:
            mark(board, bingo_num)
            if is_winner(board):
                score = get_score(board=board, winning_number=bingo_num)
                print("FIRST winning board found!")
                print(board)
                print(f"score = {score}") # 23177
                sys.exit(0)
