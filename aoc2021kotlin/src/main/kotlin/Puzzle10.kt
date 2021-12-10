package net.blergh.advent2021

import java.io.File
import java.lang.RuntimeException

object Puzzle10 {
    fun run() {
        val input10 = File("${Main.aocRoot}/other/10/input10").readText().trim()
        val inputLines = input10.split("\n")

        samplePart1()
        part1(inputLines)
    }

    private fun part1(inputLines: List<String>) {
        println("Part 1)")

        var score = 0
        for(line in inputLines) {
            println("checking: $line")
            val illegal = firstIllegalChar(line)
            if(illegal != null) {
                score += SCORE_TABLE[illegal]!!
            }
        }
        println("(p1 answer) syntax error score: $score") // 394647
    }

    private fun samplePart1() {
        println("P1 SAMPLE")

        var score = 0
        for(line in SAMPLE_INPUT.split("\n")) {
            println("checking: $line")
            val illegal = firstIllegalChar(line)
            if(illegal != null) {
                score += SCORE_TABLE[illegal]!!
            }
        }
        println("syntax error score: $score")
        assert(score == 26397)
    }

    private fun firstIllegalChar(navString: String): Char? {
        val delimiterStack = mutableListOf<Char>()

        for(c in navString.toCharArray()) {
            if(c in OPENERS) {
                delimiterStack.add(c) // push
            } else if(c in CLOSERS) {
                val opener = delimiterStack.removeLast() // pop
                val expectedCloser = MATCHING_DELIMITERS[opener]

                if(c != MATCHING_DELIMITERS[opener]) {
                    println("expected $expectedCloser, found $c")
                    return c
                }
            } else {
                throw RuntimeException("unexpected character $c")
            }
        }

        return null
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

    private val SCORE_TABLE = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
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