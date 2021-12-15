package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

data class PairInsertionRule(val pair: String, val insertion: String)

object Puzzle14 {
    fun parsePolymerTemplate(inputString: String): String {
        return inputString.split("\n\n")[0]
    }

    fun parsePairInsertionRules(inputString: String): List<PairInsertionRule> {
        val rulesPart = inputString.split("\n\n")[1]
        return rulesPart.split("\n").map {
            val (first, second) = it.split(" -> ")
            PairInsertionRule(first, second)
        }
    }

    fun applyRules(template: String, rules: List<PairInsertionRule>): String {
        val insertions = mutableMapOf<IntRange, String>()
        for(i in 0 until template.length-1) {
            for(r in rules) {
                val range = (i..i+1)
                if(template.slice(range) == r.pair) {
                    insertions[range] = r.insertion
                }

            }
        }

        var scratch = template
        var offset = 0 // each substitution we perform requires bumping over all subsequent ranges
        for((range, insertion) in insertions) {
            val updatedRange = (range.first+offset .. range.last+offset)

            val stringToInsert = "${scratch[updatedRange.first]}$insertion${scratch[updatedRange.last]}"

            scratch = scratch.replaceRange(updatedRange, stringToInsert)

            offset++
        }

        return scratch
    }

    fun part1(template: String, rules: List<PairInsertionRule>): Int {
        var mutatingTemplate = template
        for(step in 1..10) {
            mutatingTemplate = applyRules(mutatingTemplate, rules)
        }

        val countByChar = mutatingTemplate.groupingBy { it }.eachCount()
        val highestCount = countByChar.values.maxOf { it }
        val lowestCount = countByChar.values.minOf { it }
        val p1answer = highestCount - lowestCount
        return p1answer
    }

    fun run() {
        val input14 = File("${Main.aocRoot}/other/14/input14").readText().trim()
        val template = parsePolymerTemplate(input14)
        val rules = parsePairInsertionRules(input14)

        println("Part 1")
        val p1 = part1(template, rules)
        println("most common minus least common = $p1")
    }

    class Puzzle14Test {
        val SAMPLE_INPUT = """
            NNCB

            CH -> B
            HH -> N
            CB -> H
            NH -> C
            HB -> C
            HC -> B
            HN -> C
            NN -> C
            BH -> H
            NC -> B
            NB -> B
            BN -> B
            BB -> N
            BC -> B
            CC -> N
            CN -> C
        """.trimIndent().trim()

        @Test
        fun `parse polymer template`() {
            val expected = "NNCB"
            val computed = parsePolymerTemplate(SAMPLE_INPUT)
            assertEquals(expected, computed)
        }

        @Test
        fun `parse pair insertion rules`() {
            val expected = listOf(
                PairInsertionRule("CH", "B"),
                PairInsertionRule("HH", "N"),
                PairInsertionRule("CB", "H"),
                PairInsertionRule("NH", "C"),
                PairInsertionRule("HB", "C"),
                PairInsertionRule("HC", "B"),
                PairInsertionRule("HN", "C"),
                PairInsertionRule("NN", "C"),
                PairInsertionRule("BH", "H"),
                PairInsertionRule("NC", "B"),
                PairInsertionRule("NB", "B"),
                PairInsertionRule("BN", "B"),
                PairInsertionRule("BB", "N"),
                PairInsertionRule("BC", "B"),
                PairInsertionRule("CC", "N"),
                PairInsertionRule("CN", "C"),
            )
            val computed = parsePairInsertionRules(SAMPLE_INPUT)
            assertEquals(expected, computed)
        }

        @Test
        fun `apply insertion rules to sample`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT)
            val rules = parsePairInsertionRules(SAMPLE_INPUT)

            val expected1 = "NCNBCHB"
            val computed1 = applyRules(template, rules)
            assertEquals(expected1, computed1)

            val expected2 = "NBCCNBBBCBHCB"
            val computed2 = applyRules(computed1, rules)
            assertEquals(expected2, computed2)

            val expected3 = "NBBBCNCCNBBNBNBBCHBHHBCHB"
            val computed3 = applyRules(computed2, rules)
            assertEquals(expected3, computed3)

            val expected4 = "NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB"
            val computed4 = applyRules(computed3, rules)
            assertEquals(expected4, computed4)
        }
    }
}