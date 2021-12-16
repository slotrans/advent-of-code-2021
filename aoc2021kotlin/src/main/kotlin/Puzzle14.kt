package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertArrayEquals
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

    fun part1alternate(template: String, translations: Map<String, Pair<String, String>>): Long {
        val countByCharCode = countByElement(template, translations, 10)
        val highestCount = countByCharCode.filter { it > 0 }.maxOf { it }
        val lowestCount = countByCharCode.filter { it > 0 }.minOf { it }
        val p1answer = highestCount - lowestCount
        return p1answer
    }

    /* haha maybe if you had a petabyte of RAM...
    fun part2(template: String, rules: List<PairInsertionRule>): Long {
        var mutatingTemplate = template
        for(step in 1..40) {
            mutatingTemplate = applyRules(mutatingTemplate, rules)
        }

        val countByChar = mutableMapOf<Char, Long>().withDefault { 0 }
        for(c in mutatingTemplate) {
            countByChar[c] = countByChar[c]!! + 1
        }
        val highestCount = countByChar.values.maxOf { it }
        val lowestCount = countByChar.values.minOf { it }
        val p1answer = highestCount - lowestCount
        return p1answer
    }
     */

    fun part2(template: String, translations: Map<String, Pair<String, String>>): Long {
        val countByCharCode = countByElement(template, translations, 40)
        val highestCount = countByCharCode.filter { it > 0 }.maxOf { it }
        val lowestCount = countByCharCode.filter { it > 0 }.minOf { it }
        val p1answer = highestCount - lowestCount
        return p1answer
    }

    fun buildTranslationMap(inputString: String): Map<String, Pair<String, String>> {
        val rulesPart = inputString.split("\n\n")[1]
        return rulesPart.split("\n").associate {
            val (left, right) = it.split(" -> ")
            // e.g. "CH -> B" becomes "CH" to Pair("CB", "BH")
            left to Pair("${left[0]}${right}", "${right}${left[1]}")
        }
    }

    fun countByElement(template: String, translation: Map<String, Pair<String, String>>, steps: Int): LongArray {
        val initialLetterPairs = (0 until template.length-1).map { i ->
            template.slice(i..i+1)
        }

        // fire off first layer of calls to expandAndCount
        val countsFromExpansion = initialLetterPairs.map {
            expandAndCount(it, translation, steps-1)
        }.fold(LongArray(26) { 0 }) {
                left, right -> combineCounts(left, right)
        } // this fold() isn't super necessary now that I've made combineCounts() varargs, but whatever

        // the expansion process tracks everything but the last letter of the template, so we add it directly
        val lastLetter = template.last().toString()
        val totalCounts = combineCounts(countsFromExpansion, toCountArray(lastLetter))
        return totalCounts
    }

    fun expandAndCount(letterPair: String, translation: Map<String, Pair<String, String>>, stepsRemaining: Int): LongArray {
        val expanded = translation[letterPair]!!
        if(stepsRemaining == 0) {
            // return count-by-char for the *LEFT HALF ONLY* of the above pair
            return toCountArray(expanded.first)
        }
        else
        {
            val firstCounts = expandAndCount(expanded.first, translation,stepsRemaining-1)
            val secondCounts = expandAndCount(expanded.second, translation,stepsRemaining-1)
            return combineCounts(firstCounts, secondCounts)
        }
    }

//    fun combineCounts(left: LongArray, right: LongArray): LongArray {
//        val combined = LongArray(26) { 0 }
//        for(i in 0 until 26) {
//            combined[i] = left[i] + right[i]
//        }
//        return combined
//    }

    fun combineCounts(vararg inArrays: LongArray): LongArray {
        val combined = LongArray(26) { 0 }
        for(i in 0 until 26) {
            combined[i] = inArrays.sumOf { it[i] }
        }
        return combined
    }

    private val TO_COUNT_ARRAY_CACHE = mutableMapOf<String, LongArray>()

    fun toCountArray(elementString: String): LongArray {
        if(TO_COUNT_ARRAY_CACHE.contains(elementString)) {
            return TO_COUNT_ARRAY_CACHE[elementString]!!
        }

        val out = LongArray(26) { 0 }

        for(c in elementString.toCharArray()) {
            val idx = c.code - 65 // ascii tricks, 'A' is 65
            out[idx] = out[idx] + 1
        }

        TO_COUNT_ARRAY_CACHE[elementString] = out
        return out
    }

    fun run() {
        val input14 = File("${Main.aocRoot}/other/14/input14").readText().trim()
        val template = parsePolymerTemplate(input14)
        val rules = parsePairInsertionRules(input14)
        val translations = buildTranslationMap(input14)

        println("Part 1")
        val p1 = part1(template, rules)
        println("most common minus least common = $p1") // 2915

        println("Part 1 (alternate method)")
        val p1alt = part1alternate(template, translations)
        println("most common minus least common = $p1alt") // 2915

        println("Part 2")
        val p2 = part2(template, translations)
        println("most common minus least common = $p2") //
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

        @Test
        fun `test part1 on sample`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT)
            val rules = parsePairInsertionRules(SAMPLE_INPUT)

            val expected = 1588
            val computed = part1(template, rules)
            assertEquals(expected, computed)
        }

        @Test
        fun `test part1alternate on sample`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT)
            val translations = buildTranslationMap(SAMPLE_INPUT)

            val expected: Long = 1588
            val computed = part1alternate(template, translations)
            assertEquals(expected, computed)
        }

        /*
        @Test
        fun `test part2 on sample`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT)
            val rules = parsePairInsertionRules(SAMPLE_INPUT)

            val expected = 2188189693529
            val computed = part2(template, rules)
            assertEquals(expected, computed)
        }
         */

        @Test
        fun `build translation map from sample input`() {
            val expected = mapOf(
                "CH" to Pair("CB", "BH"), // B
                "HH" to Pair("HN", "NH"), // N
                "CB" to Pair("CH", "HB"), // H
                "NH" to Pair("NC", "CH"), // C
                "HB" to Pair("HC", "CB"), // C
                "HC" to Pair("HB", "BC"), // B
                "HN" to Pair("HC", "CN"), // C
                "NN" to Pair("NC", "CN"), // C
                "BH" to Pair("BH", "HH"), // H
                "NC" to Pair("NB", "BC"), // B
                "NB" to Pair("NB", "BB"), // B
                "BN" to Pair("BB", "BN"), // B
                "BB" to Pair("BN", "NB"), // N
                "BC" to Pair("BB", "BC"), // B
                "CC" to Pair("CN", "NC"), // N
                "CN" to Pair("CC", "CN"), // C
            )
            val computed = buildTranslationMap(SAMPLE_INPUT)
            assertEquals(expected, computed)
        }

        @Test
        fun `toCountArray realistic calls`() {
            var expected = LongArray(26) { 0 }
            expected[1] = 1
            expected[13] = 1
            var computed = toCountArray("NB")
            assertArrayEquals(expected, computed)

            expected = LongArray(26) { 0 }
            expected[2] = 2
            computed = toCountArray("CC")
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `toCountArray full alphabet`() {
            val expected = LongArray(26) { 1 }
            val computed = toCountArray("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `combineCounts 2 args`() {
            val expected = LongArray(26) { 0 }
            expected[0] = 1
            expected[1] = 1

            val leftInput = LongArray(26) { 0 }
            leftInput[0] = 1

            val rightInput = LongArray(26) { 0 }
            rightInput[1] = 1

            val computed = combineCounts(leftInput, rightInput)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `combineCounts 3 args`() {
            val expected = LongArray(26) { 0 }
            expected[0] = 1
            expected[1] = 1
            expected[2] = 1

            val inputZero = LongArray(26) { 0 }
            inputZero[0] = 1

            val inputOne = LongArray(26) { 0 }
            inputOne[1] = 1

            val inputTwo = LongArray(26) { 0 }
            inputTwo[2] = 1

            val computed = combineCounts(inputZero, inputOne, inputTwo)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `expandAndCount stepsRemaining=0 case`() {
            val translations = buildTranslationMap(SAMPLE_INPUT)
            val expected = toCountArray("NB")
            val computed = expandAndCount("NB", translations, 0)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `expandAndCount stepsRemaining=1 case`() {
            val translations = buildTranslationMap(SAMPLE_INPUT)
            val expected = combineCounts(
                toCountArray("NB"),
                toCountArray("BN"),
            )
            val computed = expandAndCount("NB", translations, 1)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `expandAndCount stepsRemaining=2 case`() {
            val translations = buildTranslationMap(SAMPLE_INPUT)
            val expected = combineCounts(
                toCountArray("NB"),
                toCountArray("BN"),
                toCountArray("BB"),
                toCountArray("NB"),
            )
            val computed = expandAndCount("NB", translations, 2)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `countByElement on sample input, steps=1`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT) // "NNCB"
            val translations = buildTranslationMap(SAMPLE_INPUT)
            val expected = toCountArray("NCNBCHB")
            val computed = countByElement(template, translations, 1)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `countByElement on sample input, steps=2`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT) // "NNCB"
            val translations = buildTranslationMap(SAMPLE_INPUT)
            val expected = toCountArray("NBCCNBBBCBHCB")
            val computed = countByElement(template, translations, 2)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `countByElement on sample input, steps=3`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT) // "NNCB"
            val translations = buildTranslationMap(SAMPLE_INPUT)
            val expected = toCountArray("NBBBCNCCNBBNBNBBCHBHHBCHB")
            val computed = countByElement(template, translations, 3)
            assertArrayEquals(expected, computed)
        }

        @Test
        fun `countByElement on sample input, steps=4`() {
            val template = parsePolymerTemplate(SAMPLE_INPUT) // "NNCB"
            val translations = buildTranslationMap(SAMPLE_INPUT)
            val expected = toCountArray("NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB")
            val computed = countByElement(template, translations, 4)
            assertArrayEquals(expected, computed)
        }
    }
}