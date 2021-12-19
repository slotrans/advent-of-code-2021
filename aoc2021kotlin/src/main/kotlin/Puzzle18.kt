package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

sealed class SFTreeNode(level: Int)

data class SFPairNode(var leftNode: SFTreeNode, var rightNode: SFTreeNode, val level: Int): SFTreeNode(level)

data class SFNumberNode(var numVal: Int, val level: Int): SFTreeNode(level)


object Puzzle18 {
    fun tokenizeInput(inputString: String): List<String> {
        val out = mutableListOf<String>()

        var i = 0
        while(i < inputString.length) {
            when(inputString[i]) {
                '[' -> {
                    out.add("[")
                    i++
                }
                ']' -> {
                    out.add("]")
                    i++
                }
                ',' -> {
                    out.add(",")
                    i++
                }
                else -> {
                    val digits = inputString.drop(i).takeWhile { it.isDigit() }
                    out.add(digits)
                    i += digits.length
                }
            }
        }

        return out
    }

    fun parseTreeFromTokens(tokens: List<String>, level: Int = 0): SFTreeNode {
        return when(tokens[0]) {
            "[" -> {
                var commaIndex = -1
                var closeBracketIndex = -1
                var depth = 0
                for(i in 1 until tokens.size) {
                    if(tokens[i] == "]" && depth == 0) {
                        closeBracketIndex = i
                        break
                    }

                    if(tokens[i] == "," && depth == 0) commaIndex = i

                    if(tokens[i] == "[") depth++
                    if(tokens[i] == "]") depth--
                }
                SFPairNode(
                    parseTreeFromTokens(tokens.slice(1 until commaIndex), level+1),
                    parseTreeFromTokens(tokens.slice(commaIndex+1 until closeBracketIndex), level+1),
                    level
                )
            }
            "]" -> throw Exception("parseTreeFromTokens encountered ]")
            else -> {
                SFNumberNode(tokens[0].toInt(), level)
            }
        }
    }

    fun run() {
        val input18 = File("${Main.aocRoot}/other/18/input18").readText().trim()

    }

    /******************************************************************************************************************/

    class Puzzle18Test {
        @Test
        fun `test tokenizing example 1`() {
            val tokens = tokenizeInput("[1,2]")
            assertEquals(listOf("[", "1", ",", "2", "]"), tokens)
        }

        @Test
        fun `test tokenizing example 2`() {
            val tokens = tokenizeInput("[[1,2],3]")
            assertEquals(listOf("[", "[", "1", ",", "2", "]", ",", "3", "]"), tokens)
        }

        @Test
        fun `test tokenizing example 3`() {
            val tokens = tokenizeInput("[9,[8,7]]")
            assertEquals(listOf("[", "9", ",", "[", "8", ",", "7", "]", "]"), tokens)
        }

        @Test
        fun `test tokenizing example 4`() {
            val tokens = tokenizeInput("[[1,9],[8,5]]")
            assertEquals(listOf("[", "[", "1", ",", "9", "]", ",", "[", "8", ",", "5", "]", "]"), tokens)
        }

        @Test
        fun `test tokenizing example 5`() {
            val tokens = tokenizeInput("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]")
            assertEquals(listOf("[", "[", "[", "[", "1", ",", "2", "]", ",", "[", "3", ",", "4", "]", "]", ",", "[", "[", "5", ",", "6", "]", ",", "[", "7", ",", "8", "]", "]", "]", ",", "9", "]"), tokens)
        }

        @Test
        fun `test parsing example 1`() {
            val tokens = tokenizeInput("[1,2]")
            val expected = SFPairNode(
                leftNode =  SFNumberNode(1, 1),
                rightNode =  SFNumberNode(2, 1),
                level = 0,
            )
            val computed = parseTreeFromTokens(tokens)
            assertEquals(expected, computed)
        }

        @Test
        fun `test parsing example 2`() {
            val tokens = tokenizeInput("[[1,2],3]")
            val expected = SFPairNode(
                SFPairNode(
                    leftNode = SFNumberNode(1, 2),
                    rightNode = SFNumberNode(2, 2),
                    level = 1,
                ),
                SFNumberNode(3, 1),
                level = 0,
            )
            val computed = parseTreeFromTokens(tokens)
            assertEquals(expected, computed)
        }

        @Test
        fun `test parsing example 3`() {
            val tokens = tokenizeInput("[9,[8,7]]")
            val expected = SFPairNode(
                SFNumberNode(9, 1),
                SFPairNode(
                    leftNode = SFNumberNode(8, 2),
                    rightNode = SFNumberNode(7, 2),
                    level = 1,
                ),
                level = 0,
            )
            val computed = parseTreeFromTokens(tokens)
            assertEquals(expected, computed)
        }

        @Test
        fun `test parsing example 4`() {
            val tokens = tokenizeInput("[[1,9],[8,5]]")
            val expected = SFPairNode(
                SFPairNode(
                    leftNode = SFNumberNode(1, 2),
                    rightNode = SFNumberNode(9, 2),
                    level = 1,
                ),
                SFPairNode(
                    leftNode = SFNumberNode(8, 2),
                    rightNode = SFNumberNode(5, 2),
                    level = 1,
                ),
                level = 0,
            )
            val computed = parseTreeFromTokens(tokens)
            assertEquals(expected, computed)
        }

        @Test
        fun `test parsing example 5`() {
            val tokens = tokenizeInput("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]")
            val expected = SFPairNode(
                SFPairNode(
                    leftNode = SFPairNode(
                        leftNode = SFPairNode(
                            leftNode = SFNumberNode(1, 4),
                            rightNode = SFNumberNode(2, 4),
                            level = 3
                        ),
                        rightNode = SFPairNode(
                            leftNode = SFNumberNode(3, 4),
                            rightNode = SFNumberNode(4, 4),
                            level = 3
                        ),
                        level = 2,
                    ),
                    rightNode = SFPairNode(
                        leftNode = SFPairNode(
                            leftNode = SFNumberNode(5, 4),
                            rightNode = SFNumberNode(6, 4),
                            level = 3
                        ),
                        rightNode = SFPairNode(
                            leftNode = SFNumberNode(7, 4),
                            rightNode = SFNumberNode(8, 4),
                            level = 3
                        ),
                        level = 2,
                    ),
                    level = 1,
                ),
                SFNumberNode(9, 1),
                level = 0,
            )
            val computed = parseTreeFromTokens(tokens)
            assertEquals(expected, computed)
        }
    }
}