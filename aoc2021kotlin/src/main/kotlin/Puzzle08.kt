package net.blergh.advent2021

import java.io.File

object Puzzle08 {
    fun run() {
        val input08 = File("${Main.aocRoot}/other/08/input08").readText().trim()

        samplePart1()
        part1(input08)
        samplePart2()
        part2(input08)
    }

    private fun samplePart2() {
        println("P2 SAMPLE")
        val signalPatterns = SAMPLE_INPUT_SMALL.split(" | ")[0].split(" ").toSet()
        val displaySegmentStrings = SAMPLE_INPUT_SMALL.split(" | ")[1].split(" ")

        val segmentWiring = inferSegmentWiring(signalPatterns, displaySegmentStrings)
        val displayOutput = getDisplayOutput(segmentWiring, displaySegmentStrings)
        println("(small sample) display: $displayOutput")
        assert(displayOutput == "5353")

        println("large sample:")
        var total = 0
        for(line in SAMPLE_INPUT_LARGE.split("\n")) {
            val signalPatterns = line.split(" | ")[0].split(" ").toSet()
            val displaySegmentStrings = line.split(" | ")[1].split(" ")

            val segmentWiring = inferSegmentWiring(signalPatterns, displaySegmentStrings)
            val displayOutput = getDisplayOutput(segmentWiring, displaySegmentStrings)
            println("display: $displayOutput")
            total += displayOutput.toInt()
        }
        println("sum of large sample: $total")
        assert(total == 61229)
    }

    private fun part2(inputAsString: String) {
        println("Part 2")

        var total = 0
        for(line in inputAsString.split("\n")) {
            val signalPatterns = line.split(" | ")[0].split(" ").toSet()
            val displaySegmentStrings = line.split(" | ")[1].split(" ")

            val segmentWiring = inferSegmentWiring(signalPatterns, displaySegmentStrings)
            val displayOutput = getDisplayOutput(segmentWiring, displaySegmentStrings)
            println("display: $displayOutput")
            total += displayOutput.toInt()
        }
        println("(p2 answer) sum of displays: $total") // 1040429
    }

    private val correctDigitWirings = arrayOf (
        "abcefg",  //0
        "cf",      //1
        "acdeg",   //2
        "acdfg",   //3
        "bcdf",    //4
        "abdfg",   //5
        "abdefg",  //6
        "acf",     //7
        "abcdefg", //8
        "abcdfg",  //9
    )

    // for clarity
    private fun charsOf(s: String): Set<Char> {
        return s.toSet()
    }

    // for clarity
    private fun sortString(s: String): String {
        return s.toList().sorted().joinToString(separator = "")
    }

    private fun inferSegmentWiring(signalPatterns: Set<String>, displaySegments: List<String>): Map<String, Int> {
        // "pdw" = possible digit wirings, need the name to be short
        val pdw = mutableMapOf<Int, Set<String>>()
        correctDigitWirings.forEachIndexed { digit, correctPattern ->
            pdw[digit] = signalPatterns.filter { it.length == correctPattern.length }.toMutableSet()
        }

        // 1,4,7,8 are known at this point
        //println("possible digit wirings: $pdw")
        //3 includes all elements of 7 -> solves 3
        pdw[3] = pdw[3]!!.filter {
            val seven = charsOf(pdw[7]!!.single())
            charsOf(it).containsAll(seven)
        }.toSet()
        //println("possible digit wirings: $pdw")
        //eliminate 3 from 2 and 5
        pdw[2] = pdw[2]!!.minus(pdw[3]!!)
        pdw[5] = pdw[5]!!.minus(pdw[3]!!)
        //println("possible digit wirings: $pdw")
        //9 includes all of 4 and 7 -> solves 9
        pdw[9] = pdw[9]!!.filter {
            val fourAndSeven = charsOf(pdw[4]!!.single()).union(charsOf(pdw[7]!!.single()))
            charsOf(it).containsAll(fourAndSeven)
        }.toSet()
        //println("possible digit wirings: $pdw")
        //eliminate 9 from 0 and 6
        pdw[0] = pdw[0]!!.minus(pdw[9]!!)
        pdw[6] = pdw[6]!!.minus(pdw[9]!!)
        //println("possible digit wirings: $pdw")
        //0 includes all of 7 -> solves 0
        pdw[0] = pdw[0]!!.filter {
            val seven = charsOf(pdw[7]!!.single())
            charsOf(it).containsAll(seven)
        }.toSet()
        //println("possible digit wirings: $pdw")
        //eliminate 0 from 6 -> solves 6
        pdw[6] = pdw[6]!!.minus(pdw[0]!!)
        //println("possible digit wirings: $pdw")
        //5 is a subset of 6 -> solves 5
        pdw[5] = pdw[5]!!.filter {
            val six = charsOf(pdw[6]!!.single())
            six.containsAll(charsOf(it))
        }.toSet()
        //println("possible digit wirings: $pdw")
        //eliminate 5 from 2 -> solves 2
        pdw[2] = pdw[2]!!.minus(pdw[5]!!)
        //println("possible digit wirings: $pdw")

        //invert the map
        //sort the segment strings to ease comparisons later
        val segmentWiring = pdw.entries.associate {
            val segmentCharsSorted = sortString(it.value.single())
            val digit = it.key
            segmentCharsSorted to digit
        }
        //println("segment wiring: $segmentWiring")
        return segmentWiring
    }

    private fun getDisplayOutput(segmentWiring: Map<String, Int>, displaySegmentStrings: List<String>): String {
        return displaySegmentStrings.map {
            segmentWiring[sortString(it)]
        }.joinToString(separator = "")
    }

    private fun part1(inputAsString: String) {
        println("Part 1")

        val segmentCountTotals = Array(8) { 0 } // 0 index will be unused
        inputAsString.split("\n").forEach { inputLine ->
            val (digitWirings, displaySegments) = inputLine.split(" | ").map { it.split(" ")}
            for(x in displaySegments) {
                segmentCountTotals[x.length] += 1
            }
        }

        val p1answer = segmentCountTotals[2] + // 2 segments lit = 1
                segmentCountTotals[4] +        // 4 segments lit = 4
                segmentCountTotals[3] +        // 3 segments lit = 7
                segmentCountTotals[7]          // 7 segments lit = 8
        println("(p1 answer) count of displayed 1,4,7,8: $p1answer") // 488
    }

    private fun samplePart1() {
        println("P1 SAMPLE")

        val segmentCountTotals = Array(8) { 0 } // 0 index will be unused
        SAMPLE_INPUT_LARGE.split("\n").forEach { inputLine ->
            val (digitWirings, displaySegments) = inputLine.split(" | ").map { it.split(" ")}
            for(x in displaySegments) {
                segmentCountTotals[x.length] += 1
            }
        }

        val p1answer = segmentCountTotals[2] + // 2 segments lit = 1
                segmentCountTotals[4] +        // 4 segments lit = 4
                segmentCountTotals[3] +        // 3 segments lit = 7
                segmentCountTotals[7]          // 7 segments lit = 8
        println("count of displayed 1,4,7,8: $p1answer")
        assert(p1answer == 26)
    }

    private val SAMPLE_INPUT_SMALL = "acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf"

    private val SAMPLE_INPUT_LARGE = """
        be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
        edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
        fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
        fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
        aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
        fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
        dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
        bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
        egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
        gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
    """.trimIndent()
}