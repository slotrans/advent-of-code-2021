#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>


struct Instruction
{
    int32_t x_min;
    int32_t x_max;
    int32_t y_min;
    int32_t y_max;
    int32_t z_min;
    int32_t z_max;
    int32_t on_or_off;
};

// shamelessly pasted from https://stackoverflow.com/questions/116038/how-do-i-read-an-entire-file-into-a-stdstring-in-c
std::string slurp(std::string& filename)
{
    std::ifstream infile(filename);
    std::ostringstream sstr;
    sstr << infile.rdbuf();
    return sstr.str();
}

// doesn't handle all corner cases but good enough for what we need here
std::vector<std::string> split(std::string str, std::string delim)
{
    std::vector<std::string> out;
    size_t start = 0;
    size_t i = 0;
    while(i < str.size())
    {
        if(str.substr(i, delim.size()) == delim)
        {
            std::string temp = str.substr(start, i-start);
            out.push_back(temp);
            i += delim.size();
            start = i;
        }
        else
        {
            i += 1;
        }
    }

    if(start < str.size())
    {
        out.push_back(str.substr(start, str.size()-start));
    }

    return out;
}

std::vector<Instruction> instructions_from_input(std::string& input_string)
{
    std::vector<Instruction> out;

    std::vector<std::string> input_lines = split(input_string, "\n");
    for(const std::string& line : input_lines)
    {
        Instruction inst;

        size_t ranges_start;
        if(line.substr(0, 2) == "on")
        {
            inst.on_or_off = 1;
            ranges_start = 3;
        }
        else // "off"
        {
            inst.on_or_off = 0;
            ranges_start = 4;
        }

        std::string ranges_part = line.substr(ranges_start, line.size()-ranges_start);
        std::vector<std::string> ranges = split(ranges_part, ",");
        std::string x_range = ranges[0].substr(2, ranges[0].size()-2);
        std::string y_range = ranges[1].substr(2, ranges[1].size()-2);
        std::string z_range = ranges[2].substr(2, ranges[2].size()-2);

        auto x_parts = split(x_range, "..");
        inst.x_min = std::stoi(x_parts[0]);
        inst.x_max = std::stoi(x_parts[1]);
        auto y_parts = split(y_range, "..");
        inst.y_min = std::stoi(y_parts[0]);
        inst.y_max = std::stoi(y_parts[1]);
        auto z_parts = split(z_range, "..");
        inst.z_min = std::stoi(z_parts[0]);
        inst.z_max = std::stoi(z_parts[1]);

        out.push_back(inst);
    }

    return out;
}

void test_split()
{
    std::string foo = "foo,bar,baz,z";
    auto parts = split(foo, ",");
    for(size_t i = 0; i < parts.size(); ++i)
    {
        std::cout << "part " << i << ": [" << parts[i] << "]" << std::endl;
    }
}

int main()
{
    std::string input22 = slurp((std::string &) "input22");
    std::vector<Instruction> instructions = instructions_from_input(input22);



    return 0;
}
