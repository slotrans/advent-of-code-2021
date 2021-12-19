package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File


object Puzzle18 {
    sealed class SFTreeNode(level: Int) {
        abstract fun deepCopy(levelAdjustment: Int = 0): SFTreeNode
        abstract fun magnitude(): Long
    }

    data class SFPairNode(var leftNode: SFTreeNode, var rightNode: SFTreeNode, val level: Int): SFTreeNode(level) {
        override
        fun deepCopy(levelAdjustment: Int): SFPairNode {
            return SFPairNode(
                leftNode = this.leftNode.deepCopy(levelAdjustment),
                rightNode = this.rightNode.deepCopy(levelAdjustment),
                level = this.level + levelAdjustment,
            )
        }

        override
        fun magnitude(): Long {
            return (3 * leftNode.magnitude()) + (2 * rightNode.magnitude())
        }
    }

    data class SFNumberNode(var numVal: Int, val level: Int): SFTreeNode(level) {
        override
        fun deepCopy(levelAdjustment: Int): SFNumberNode {
            return SFNumberNode(this.numVal, this.level + levelAdjustment)
        }

        override
        fun magnitude(): Long {
            return numVal.toLong()
        }
    }


    data class SnailfishNumber(val rootNode: SFPairNode) {
        // defines binary "+" operator
        operator fun plus(other: SnailfishNumber): SnailfishNumber {
            val left = this.rootNode.deepCopy(1)
            val right = other.rootNode.deepCopy(1)
            val newRoot = SFPairNode(left, right, 0)
            return SnailfishNumber(newRoot)
        }

        fun reduce(): SnailfishNumber {
            TODO()
        }

        fun magnitude(): Long {
            return rootNode.magnitude()
        }

        companion object {
            fun fromInputString(inputString: String): SnailfishNumber {
                val rootNode = parseTreeFromTokens(tokenizeInput(inputString))
                if(rootNode is SFPairNode) {
                    return SnailfishNumber(rootNode)
                } else {
                    throw Exception("input did not parse to a pair: $inputString")
                }
            }
        }
    }


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

    fun listViewFromTree(rootNode: SFTreeNode): List<SFTreeNode> {
        return when(rootNode) {
            is SFNumberNode -> listOf(rootNode)
            is SFPairNode -> listViewFromTree(rootNode.leftNode) + listViewFromTree(rootNode.rightNode)
        }
    }

    fun replaceInTree(rootNode: SFTreeNode, targetNode: SFTreeNode, newNode: SFTreeNode) {

    }

    fun run() {
        val input18 = File("${Main.aocRoot}/other/18/input18").readText().trim()

    }

    /******************************************************************************************************************/

    class Puzzle18Test {
        @Test
        fun `tokenizing example 1`() {
            val tokens = tokenizeInput("[1,2]")
            assertEquals(listOf("[", "1", ",", "2", "]"), tokens)
        }

        @Test
        fun `tokenizing example 2`() {
            val tokens = tokenizeInput("[[1,2],3]")
            assertEquals(listOf("[", "[", "1", ",", "2", "]", ",", "3", "]"), tokens)
        }

        @Test
        fun `tokenizing example 3`() {
            val tokens = tokenizeInput("[9,[8,7]]")
            assertEquals(listOf("[", "9", ",", "[", "8", ",", "7", "]", "]"), tokens)
        }

        @Test
        fun `tokenizing example 4`() {
            val tokens = tokenizeInput("[[1,9],[8,5]]")
            assertEquals(listOf("[", "[", "1", ",", "9", "]", ",", "[", "8", ",", "5", "]", "]"), tokens)
        }

        @Test
        fun `tokenizing example 5`() {
            val tokens = tokenizeInput("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]")
            assertEquals(listOf("[", "[", "[", "[", "1", ",", "2", "]", ",", "[", "3", ",", "4", "]", "]", ",", "[", "[", "5", ",", "6", "]", ",", "[", "7", ",", "8", "]", "]", "]", ",", "9", "]"), tokens)
        }

        @Test
        fun `parsing example 1`() {
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
        fun `parsing example 2`() {
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
        fun `parsing example 3`() {
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
        fun `parsing example 4`() {
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
        fun `parsing example 5`() {
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

        @Test
        fun `list view example 1`() {
            val tokens = tokenizeInput("[1,2]")
            val rootNode = parseTreeFromTokens(tokens)
            val expected = listOf(
                SFNumberNode(1, 1),
                SFNumberNode(2, 1),
            )
            val computed = listViewFromTree(rootNode)
            assertEquals(expected, computed)
        }

        @Test
        fun `list view example 2`() {
            val tokens = tokenizeInput("[[1,2],3]")
            val rootNode = parseTreeFromTokens(tokens)
            val expected = listOf(
                SFNumberNode(1, 2),
                SFNumberNode(2, 2),
                SFNumberNode(3, 1),
            )
            val computed = listViewFromTree(rootNode)
            assertEquals(expected, computed)
        }

        @Test
        fun `list view example 3`() {
            val tokens = tokenizeInput("[9,[8,7]]")
            val rootNode = parseTreeFromTokens(tokens)
            val expected = listOf(
                SFNumberNode(9, 1),
                SFNumberNode(8, 2),
                SFNumberNode(7, 2),
            )
            val computed = listViewFromTree(rootNode)
            assertEquals(expected, computed)
        }

        @Test
        fun `list view example 4`() {
            val tokens = tokenizeInput("[[1,9],[8,5]]")
            val rootNode = parseTreeFromTokens(tokens)
            val expected = listOf(
                SFNumberNode(1, 2),
                SFNumberNode(9, 2),
                SFNumberNode(8, 2),
                SFNumberNode(5, 2),
            )
            val computed = listViewFromTree(rootNode)
            assertEquals(expected, computed)
        }

        @Test
        fun `list view example 5`() {
            val tokens = tokenizeInput("[[[[1,2],[3,4]],[[5,6],[7,8]]],9]")
            val rootNode = parseTreeFromTokens(tokens)
            val expected = listOf(
                SFNumberNode(1, 4),
                SFNumberNode(2, 4),
                SFNumberNode(3, 4),
                SFNumberNode(4, 4),
                SFNumberNode(5, 4),
                SFNumberNode(6, 4),
                SFNumberNode(7, 4),
                SFNumberNode(8, 4),
                SFNumberNode(9, 1),
            )
            val computed = listViewFromTree(rootNode)
            assertEquals(expected, computed)
        }

        @Test
        fun `deep copy levelAdjustment 0`() {
            val original = parseTreeFromTokens(tokenizeInput("[[1,2],[3,4]]"))
            val computed = original.deepCopy()
            assertTrue(original == computed) // value
            assertTrue(original !== computed) // identity

            assertTrue(original is SFPairNode)
            assertTrue(computed is SFPairNode)

            if(original is SFPairNode && computed is SFPairNode) {
                val originalLeft = original.leftNode
                val computedLeft = computed.leftNode
                assertTrue(originalLeft == computedLeft)
                assertTrue(originalLeft !== computedLeft)
                assertEquals(0, computed.level)
                assertTrue(originalLeft is SFPairNode)
                assertTrue(computedLeft is SFPairNode)
                if(originalLeft is SFPairNode && computedLeft is SFPairNode) {
                    assertTrue(originalLeft.leftNode == computedLeft.leftNode)
                    assertTrue(originalLeft.leftNode !== computedLeft.leftNode)
                    assertEquals(1, computedLeft.level)
                    val computedOne = computedLeft.leftNode
                    assertTrue(computedOne is SFNumberNode)
                    if(computedOne is SFNumberNode) {
                        assertEquals(1, computedOne.numVal)
                        assertEquals(2, computedOne.level)
                    }

                    assertTrue(originalLeft.rightNode == computedLeft.rightNode)
                    assertTrue(originalLeft.rightNode !== computedLeft.rightNode)
                    val computedTwo = computedLeft.rightNode
                    assertTrue(computedTwo is SFNumberNode)
                    if(computedTwo is SFNumberNode) {
                        assertEquals(2, computedTwo.numVal)
                        assertEquals(2, computedTwo.level)
                    }
                }

                val originalRight = original.rightNode
                val computedRight = computed.rightNode
                assertTrue(originalRight == computedRight)
                assertTrue(originalRight !== computedRight)
                assertTrue(originalRight is SFPairNode)
                assertTrue(computedRight is SFPairNode)
                if(originalRight is SFPairNode && computedRight is SFPairNode) {
                    assertTrue(originalRight.leftNode == computedRight.leftNode)
                    assertTrue(originalRight.leftNode !== computedRight.leftNode)
                    assertEquals(1, computedRight.level)
                    val computedThree = computedRight.leftNode
                    assertTrue(computedThree is SFNumberNode)
                    if(computedThree is SFNumberNode) {
                        assertEquals(3, computedThree.numVal)
                        assertEquals(2, computedThree.level)
                    }

                    assertTrue(originalRight.rightNode == computedRight.rightNode)
                    assertTrue(originalRight.rightNode !== computedRight.rightNode)
                    val computedFour = computedRight.rightNode
                    assertTrue(computedFour is SFNumberNode)
                    if(computedFour is SFNumberNode) {
                        assertEquals(4, computedFour.numVal)
                        assertEquals(2, computedFour.level)
                    }
                }
            }
        }

        @Test
        fun `deep copy levelAdjustment 1`() {
            val original = parseTreeFromTokens(tokenizeInput("[[1,2],[3,4]]"))
            val computed = original.deepCopy(1)
            assertTrue(original != computed) // value
            assertTrue(original !== computed) // identity

            assertTrue(original is SFPairNode)
            assertTrue(computed is SFPairNode)

            if(original is SFPairNode && computed is SFPairNode) {
                val originalLeft = original.leftNode
                val computedLeft = computed.leftNode
                assertTrue(originalLeft != computedLeft)
                assertTrue(originalLeft !== computedLeft)
                assertEquals(0, original.level)
                assertEquals(1, computed.level)
                assertTrue(originalLeft is SFPairNode)
                assertTrue(computedLeft is SFPairNode)
                if(originalLeft is SFPairNode && computedLeft is SFPairNode) {
                    assertTrue(originalLeft.leftNode != computedLeft.leftNode)
                    assertTrue(originalLeft.leftNode !== computedLeft.leftNode)
                    assertEquals(1, originalLeft.level)
                    assertEquals(2, computedLeft.level)
                    val computedOne = computedLeft.leftNode
                    assertTrue(computedOne is SFNumberNode)
                    if(computedOne is SFNumberNode) {
                        assertEquals(1, computedOne.numVal)
                        assertEquals(3, computedOne.level)
                    }

                    assertTrue(originalLeft.rightNode != computedLeft.rightNode)
                    assertTrue(originalLeft.rightNode !== computedLeft.rightNode)
                    val computedTwo = computedLeft.rightNode
                    assertTrue(computedTwo is SFNumberNode)
                    if(computedTwo is SFNumberNode) {
                        assertEquals(2, computedTwo.numVal)
                        assertEquals(3, computedTwo.level)
                    }
                }

                val originalRight = original.rightNode
                val computedRight = computed.rightNode
                assertTrue(originalRight != computedRight)
                assertTrue(originalRight !== computedRight)
                assertTrue(originalRight is SFPairNode)
                assertTrue(computedRight is SFPairNode)
                if(originalRight is SFPairNode && computedRight is SFPairNode) {
                    assertTrue(originalRight.leftNode != computedRight.leftNode)
                    assertTrue(originalRight.leftNode !== computedRight.leftNode)
                    assertEquals(1, originalRight.level)
                    assertEquals(2, computedRight.level)
                    val computedThree = computedRight.leftNode
                    assertTrue(computedThree is SFNumberNode)
                    if(computedThree is SFNumberNode) {
                        assertEquals(3, computedThree.numVal)
                        assertEquals(3, computedThree.level)
                    }

                    assertTrue(originalRight.rightNode != computedRight.rightNode)
                    assertTrue(originalRight.rightNode !== computedRight.rightNode)
                    val computedFour = computedRight.rightNode
                    assertTrue(computedFour is SFNumberNode)
                    if(computedFour is SFNumberNode) {
                        assertEquals(4, computedFour.numVal)
                        assertEquals(3, computedFour.level)
                    }
                }
            }
        }

        @Test
        fun `snailfish add`() {
            val rootA = parseTreeFromTokens(tokenizeInput("[1,2]"))
            val rootB = parseTreeFromTokens(tokenizeInput("[3,4]"))
            val rootC = parseTreeFromTokens(tokenizeInput("[[1,2],[3,4]]"))
            if(rootA is SFPairNode && rootB is SFPairNode && rootC is SFPairNode) {
                val a = SnailfishNumber(rootA)
                val b = SnailfishNumber(rootB)
                val c = SnailfishNumber(rootC)
                val d = a + b
                assertEquals(c, d)
            }
            else {
                assertTrue(false)
            }
        }

        @Test
        fun `snailfish add alternate`() {
            val a = SnailfishNumber.fromInputString("[1,2]")
            val b = SnailfishNumber.fromInputString("[3,4]")
            val c = SnailfishNumber.fromInputString("[[1,2],[3,4]]")
            val d = a + b
            assertEquals(c, d)
        }

        @Test
        fun `magnitudes`() {
            assertEquals(129, SnailfishNumber.fromInputString("[[9,1],[1,9]]").magnitude())
            assertEquals(143, SnailfishNumber.fromInputString("[[1,2],[[3,4],5]]").magnitude())
            assertEquals(1384, SnailfishNumber.fromInputString("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").magnitude())
            assertEquals(445, SnailfishNumber.fromInputString("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude())
            assertEquals(791, SnailfishNumber.fromInputString("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude())
            assertEquals(1137, SnailfishNumber.fromInputString("[[[[5,0],[7,4]],[5,5]],[6,6]]").magnitude())
            assertEquals(3488, SnailfishNumber.fromInputString("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude())
        }
    }
}