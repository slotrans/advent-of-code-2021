package net.blergh.advent2021

import java.io.File

object Puzzle08 {
    fun run() {
        val input08 = File("${Main.aocRoot}/other/08/input08").readText().trim()

        //samplePart1()
        //part1(input08)
        samplePart2()
    }

    private fun samplePart2() {
        val smallSampleInput = "acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf"
        val signalPatterns = smallSampleInput.split(" | ")[0].split(" ").toSet()
        val displaySegments = smallSampleInput.split(" | ")[1].split(" ")

        val foo = inferSegmentWiring(signalPatterns, displaySegments)
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

    private fun inferSegmentWiring(signalPatterns: Set<String>, displaySegments: List<String>): Map<String, Int> {
        val segmentWiring = mutableMapOf<String, Int>()

        val possibleDigitWirings = mutableMapOf<Int, Set<String>>().withDefault { mutableSetOf() }
        correctDigitWirings.forEachIndexed { digit, correctPattern ->
            possibleDigitWirings[digit] = signalPatterns.filter { it.length == correctPattern.length }.toMutableSet()
        }
        // 1,4,7,8 will be known at this point
        println("possible digit wirings: $possibleDigitWirings")



        return segmentWiring
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
        SAMPLE_INPUT.split("\n").forEach { inputLine ->
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

    private val SAMPLE_INPUT = """
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