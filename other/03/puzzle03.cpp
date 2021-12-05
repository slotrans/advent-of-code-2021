#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <stdexcept>
#include <array>

#define BIT_STRING_LENGTH 12


typedef std::vector<std::string> BitStringList_t;
typedef std::array<int32_t, BIT_STRING_LENGTH> Bitcount_t;


BitStringList_t readInput(std::string filename)
{
    std::vector<std::string> output;
    std::string line;

    std::ifstream myfile(filename);
    if(myfile.is_open())
    {
        while(std::getline(myfile, line))
        {
            //std::string tempLine = line;
            output.push_back(line);
        }
    }
    else
    {
        throw std::runtime_error("couldn't open file");
    }

    return output;
}


std::string getMostCommonBits(BitStringList_t& bitStrings)
{
    Bitcount_t frequencies = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for(auto bs : bitStrings)
    {
        for(size_t i = 0; i < BIT_STRING_LENGTH; ++i)
        {
            frequencies[i] += (bs[i] - '0'); //ascii tricks
        }
    }

    std::string mostCommonBits = "";
    for(size_t i = 0; i < frequencies.size(); ++i)
    {
        if((float)frequencies[i] / bitStrings.size() >= 0.5)
        {
            mostCommonBits += "1";
        }
        else
        {
            mostCommonBits += "0";
        }
    }

    return mostCommonBits;
}


std::string invertBitString(std::string bitString)
{
    std::string inverted = "";
    for(size_t i = 0; i < bitString.size(); ++i)
    {
        inverted += ('1' == bitString[i] ? '0' : '1');
    }
    return inverted;
}


BitStringList_t filterByBitRule(BitStringList_t inBitStrings, std::string bitRule, size_t bitPosition)
{
    BitStringList_t outBitStrings;
    for(size_t i = 0; i < inBitStrings.size(); ++i)
    {
        if(inBitStrings[i][bitPosition] == bitRule[bitPosition])
        {
            outBitStrings.push_back(inBitStrings[i]);
        }
    }
    return outBitStrings;
}


int main()
{
    BitStringList_t bitStrings = readInput("input03");

    std::string mostCommonBits = getMostCommonBits(bitStrings);
    std::cout << mostCommonBits << std::endl;

    std::string leastCommonBits = invertBitString(mostCommonBits);
    std::cout << leastCommonBits << std::endl;

    int32_t gammaRate = std::stoi(mostCommonBits, nullptr, 2);
    int32_t epsilonRate = std::stoi(leastCommonBits, nullptr, 2);

    std::cout << "gamma rate = " << gammaRate << std::endl; // 199
    std::cout << "epsilon rate = " << epsilonRate << std::endl; // 3896

    int32_t powerConsumption = gammaRate * epsilonRate;
    std::cout << "(p1 answer) power consumption = " << powerConsumption << std::endl; // 775304

    /* part 2 */

    BitStringList_t tempBitStrings = bitStrings;
    for(size_t i = 0; i < BIT_STRING_LENGTH; ++i)
    {
        std::string mostCommonBits = getMostCommonBits(tempBitStrings);
        tempBitStrings = filterByBitRule(tempBitStrings, mostCommonBits, i);
        if(tempBitStrings.size() == 1)
        {
            break;
        }
    }
    std::cout << tempBitStrings[0] << std::endl;
    int32_t oxygenGeneratorRating = std::stoi(tempBitStrings[0], nullptr, 2);
    std::cout << "oxygen generator rating = " << oxygenGeneratorRating << std::endl; // 509

    tempBitStrings = bitStrings; //I hope this a copy!
    for(size_t i = 0; i < BIT_STRING_LENGTH; ++i)
    {
        std::string leastCommonBits = invertBitString(getMostCommonBits(tempBitStrings));
        tempBitStrings = filterByBitRule(tempBitStrings, leastCommonBits, i);
        if(tempBitStrings.size() == 1)
        {
            break;
        }
    }
    std::cout << tempBitStrings[0] << std::endl;
    int32_t co2ScrubberRating = std::stoi(tempBitStrings[0], nullptr, 2);
    std::cout << "co2 scrubber rating = " << co2ScrubberRating << std::endl; // 2693

    int32_t lifeSupportRating = oxygenGeneratorRating * co2ScrubberRating;
    std::cout << "(p2 answer) life support rating = " << lifeSupportRating << std::endl; // 1370737
}
