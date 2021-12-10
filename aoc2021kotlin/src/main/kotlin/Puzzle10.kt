package net.blergh.advent2021

import java.io.File
import java.lang.RuntimeException

class CorruptedLineException(message: String, val illegalChar: Char): Exception(message)

object Puzzle10 {
    fun run() {
        val input10 = File("${Main.aocRoot}/other/10/input10").readText().trim()
        val inputLines = input10.split("\n")

        samplePart1()
        part1(inputLines)
        samplePart2()
        part2(inputLines)
    }

    private fun part1(inputLines: List<String>) {
        println("Part 1)")

        var score = 0
        for(line in inputLines) {
            println("checking: $line")
            try {
                val delimiters = findMissingDelimiters(line)
            } catch(e: CorruptedLineException) {
                val illegal = e.illegalChar
                score += P1_SCORE_TABLE[illegal]!!
            }
        }
        println("(p1 answer) syntax error score: $score") // 394647
    }

    private fun part2(inputLines: List<String>) {
        println("Part 2")

        val lineScores = mutableListOf<Long>()
        for(line in inputLines) {
            println("checking: $line")
            try {
                val delimiters = findMissingDelimiters(line)
                lineScores.add(computePart2LineScore(delimiters))
            } catch(e: CorruptedLineException) {
                println("(discarding corrupted line)")
            }
        }
        val overallScore = computePart2OverallScore(lineScores)
        println("(p2 answer) auto-complete score: $overallScore") // 2380061249
    }

    private fun samplePart1() {
        println("P1 SAMPLE")

        var score = 0
        for(line in SAMPLE_INPUT.split("\n")) {
            println("checking: $line")
            try {
                val delimiters = findMissingDelimiters(line)
            } catch(e: CorruptedLineException) {
                val illegal = e.illegalChar
                score += P1_SCORE_TABLE[illegal]!!
            }
        }
        println("syntax error score: $score")
        assert(score == 26397)
    }

    private fun samplePart2() {
        println("P2 SAMPLE")

        val lineScores = mutableListOf<Long>()
        for(line in SAMPLE_INPUT.split("\n")) {
            println("checking: $line")
            try {
                val delimiters = findMissingDelimiters(line)
                lineScores.add(computePart2LineScore(delimiters))
            } catch(e: CorruptedLineException) {
                println("(discarding corrupted line)")
            }
        }
        val overallScore = computePart2OverallScore(lineScores)
        println("auto-complete score: $overallScore")
        assert(overallScore == 288957L)
    }

    private fun findMissingDelimiters(navString: String): String {
        val delimiterStack = mutableListOf<Char>()

        for(c in navString.toCharArray()) {
            if(c in OPENERS) {
                delimiterStack.add(c) // push
            } else if(c in CLOSERS) {
                val opener = delimiterStack.removeLast() // pop
                val expectedCloser = MATCHING_DELIMITERS[opener]

                if(c != MATCHING_DELIMITERS[opener]) {
                    val msg = "expected $expectedCloser, found $c"
                    println(msg)
                    throw CorruptedLineException(msg, c)
                }
            } else {
                throw RuntimeException("unexpected character $c")
            }
        }

        val missingDelimiters = delimiterStack.reversed().map {
            MATCHING_DELIMITERS[it]
        }.joinToString(separator = "")
        return missingDelimiters
    }

    private fun computePart2LineScore(delimiters: String): Long {
        var score = 0L
        for(d in delimiters.toCharArray()) {
            val incrementalScore = P2_SCORE_TABLE[d]!!
            score = (score * 5) + incrementalScore
        }
        return score
    }

    private fun computePart2OverallScore(scores: List<Long>): Long {
        assert(scores.size.mod(2) == 1) // instructions say the length will always be odd

        val index = (scores.size - 1) / 2
        return scores.sorted()[index]
    }

    private val OPENERS = setOf('(', '[', '{', '<')

    private val CLOSERS = setOf(')', ']', '}', '>')

    private val MATCHING_DELIMITERS = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>',
        ')' to '(',
        ']' to '[',
        '}' to '{',
        '>' to '<',
    )

    private val P1_SCORE_TABLE = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    )

    private val P2_SCORE_TABLE = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4,
    )

    private val SAMPLE_INPUT = """
        [({(<(())[]>[[{[]{<()<>>
        [(()[<>])]({[<{<<[]>>(
        {([(<{}[<>[]}>{[]{[(<()>
        (((({<>}<{<{<>}{[]{[]{}
        [[<[([]))<([[{}[[()]]]
        [{[{({}]{}}([{[{{{}}([]
        {<[[]]>}<{[{[{[]{()[[[]
        [<(<(<(<{}))><([]([]()
        <{([([[(<>()){}]>(<<{{
        <{([{{}}[<[[[<>{}]]]>[]]
    """.trimIndent().trim()
}