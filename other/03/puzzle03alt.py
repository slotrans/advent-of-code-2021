import pandas as pd


def get_most_common_bits(df):
    return [(1 if df[i].mean() >= 0.5 else 0) for i in range(0,12)]


def invert_bit_list(bit_list):
    return [(1 if bit == 0 else 0) for i, bit in enumerate(bit_list)]


if __name__ == "__main__":
    input03 = open("input03", encoding="utf-8").read().strip()
    input03lines = input03.split("\n")
    bit_string_df = pd.DataFrame([[int(c) for c in x] for x in input03lines])

    most_common_bits = get_most_common_bits(bit_string_df)
    least_common_bits = invert_bit_list(most_common_bits)

    gamma_rate = int("".join([str(i) for i in most_common_bits]), 2)
    epsilon_rate = int("".join([str(i) for i in least_common_bits]), 2)

    print(f"gamma rate = {gamma_rate}") # 199
    print(f"epsilon rate = {epsilon_rate}") # 3896
    power_consumption = gamma_rate * epsilon_rate
    print(f"(p1 answer) power consumption = {power_consumption}") # 775304

    ### part 2 ###

    bit_string_length =  len(bit_string_df.loc[0, :])

    temp_bit_string_df = bit_string_df.copy()
    for i in range(0, bit_string_length):
        most_common_bits = get_most_common_bits(temp_bit_string_df)
        temp_bit_string_df = temp_bit_string_df[temp_bit_string_df[i] == most_common_bits[i]]
        if len(temp_bit_string_df) == 1:
            break
    oxygen_generator_rating = int("".join([str(i) for i in temp_bit_string_df.iloc[0]]), 2)

    temp_bit_string_df = bit_string_df.copy()
    for i in range(0, bit_string_length):
        least_common_bits = invert_bit_list(get_most_common_bits(temp_bit_string_df))
        temp_bit_string_df = temp_bit_string_df[temp_bit_string_df[i] == most_common_bits[i]]
        if len(temp_bit_string_df) == 1:
            break
    co2_scrubber_rating = int("".join([str(i) for i in temp_bit_string_df.iloc[0]]), 2)

    print(f"oxygen generator rating = {oxygen_generator_rating}") # 509
    print(f"co2 scrubber rating = {co2_scrubber_rating}") # 2693
    life_support_rating = oxygen_generator_rating * co2_scrubber_rating
    print(f"(p2 answer) life support rating = {life_support_rating}") # 1370737
