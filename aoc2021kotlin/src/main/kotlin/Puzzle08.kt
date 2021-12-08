package net.blergh.advent2021

import java.io.File

object Puzzle08 {
    fun run() {
        val input08 = File("${Main.aocRoot}/other/08/input08").readText().trim()

        samplePart1()
        part1(input08)
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