from collections import defaultdict


def get_bit_frequency(bit_strings):
    freq = [0] * len(bit_strings[0]) # start with a list of zeros the same length as an input bit string
    for bs in bit_strings:
        for i, bit in enumerate(bs):
            freq[i] += int(bit)
    
    return freq


def get_most_common_bits(bit_strings):
    bit_frequencies = get_bit_frequency(bit_strings)
    most_common_bits = ['_'] * len(bit_frequencies)
    for i, freq in enumerate(bit_frequencies):
        if freq / len(bit_strings) >= 0.5:
            most_common_bits[i] = '1'
        else:
            most_common_bits[i] = '0'

    return most_common_bits


def invert_bit_list(bit_list):
    return [('1' if bit == '0' else '0') for i, bit in enumerate(bit_list)]


if __name__ == "__main__":
    input03 = open("input03", encoding="utf-8").read().strip()
    bit_strings = input03.split("\n")

    most_common_bits = get_most_common_bits(bit_strings)
    least_common_bits = invert_bit_list(most_common_bits)

    gamma_rate = int("".join(most_common_bits), 2)
    epsilon_rate = int("".join(least_common_bits), 2)

    print(f"gamma rate = {gamma_rate}") # 199
    print(f"epsilon rate = {epsilon_rate}") # 3896
    power_consumption = gamma_rate * epsilon_rate
    print(f"(p1 answer) power consumption = {power_consumption}") # 775304
