package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


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

        override
        fun toString(): String {
            return "[$leftNode,$rightNode]"
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

        override
        fun toString(): String {
            return numVal.toString()
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

        fun nodesInOrder(rootNode: SFTreeNode = this.rootNode): List<SFTreeNode> {
            return when(rootNode) {
                is SFNumberNode -> listOf(rootNode)
                is SFPairNode -> nodesInOrder(rootNode.leftNode) + listOf(rootNode) + nodesInOrder(rootNode.rightNode)
            }
        }

        // mutates!
        fun explode(targetNode: SFPairNode) {
            var targetFound = false
            var numberToLeft: SFNumberNode? = null
            var numberToRight: SFNumberNode? = null
            var parent: SFPairNode? = null

            for(node in nodesInOrder(this.rootNode)) {
                if(node === targetNode) { // identity!
                    targetFound = true
                } else {
                    if(node is SFNumberNode && node !== targetNode.leftNode && node !== targetNode.rightNode) {
                        if(!targetFound) {
                            numberToLeft = node
                        }
                        if(targetFound && numberToRight == null) {
                            numberToRight = node
                        }
                    }

                    if(node is SFPairNode) {
                        if(node.leftNode === targetNode || node.rightNode === targetNode) { // identity!
                            parent = node
                        }
                    }
                }

                // lol early exit for "efficiency"
                if(targetFound && numberToLeft != null && numberToRight != null && parent != null) break
            }

            val explodingLeft = targetNode.leftNode
            val explodingRight = targetNode.rightNode
            if(explodingLeft is SFNumberNode && explodingRight is SFNumberNode) {
                if(numberToLeft != null) {
                    numberToLeft.numVal += explodingLeft.numVal
                }
                if(numberToRight != null) {
                    numberToRight.numVal += explodingRight.numVal
                }
            } else {
                throw Exception("invariant violated: 'Exploding pairs will always consist of two regular numbers.'")
            }

            //replace target with 0
            if(parent != null && parent.leftNode === targetNode) {
                parent.leftNode = SFNumberNode(0, parent.level+1)
            } else if(parent != null && parent.rightNode === targetNode) {
                parent.rightNode = SFNumberNode(0, parent.level+1)
            } else {
                throw Exception("parent not found while exploding")
            }
        }

        // obviously this wouldn't be necessary if nodes knew their parents...
        fun findParent(targetNode: SFTreeNode, searchNode: SFPairNode = this.rootNode): SFPairNode? {
            val searchLeft = searchNode.leftNode
            val searchRight = searchNode.rightNode

            if(searchLeft === targetNode || searchRight === targetNode) { // identity!
                return searchNode
            }
            else {
                if(searchLeft is SFPairNode) {
                    val leftRecursiveResult = findParent(targetNode, searchLeft)
                    if(leftRecursiveResult != null) return leftRecursiveResult
                }

                if(searchRight is SFPairNode) {
                    val rightRecursiveResult = findParent(targetNode, searchRight)
                    if(rightRecursiveResult != null) return rightRecursiveResult
                }
            }

            // should never happen unless the caller passes bogus input
            return null
        }

        // mutates!
        private fun explodeAlternate(targetNode: SFPairNode) {
            val explodingLeft = targetNode.leftNode
            val explodingRight = targetNode.rightNode
            if (explodingLeft is SFNumberNode && explodingRight is SFNumberNode) {
                val listView = listViewFromTree(this.rootNode)
                for(i in listView.indices) {
                    if(listView[i] === targetNode) { // identity equality!!!
                        var numberToLeft: SFNumberNode? = null
                        for(lookLeft in i-1 downTo 0) {
                            val temp = listView[lookLeft]
                            if(temp is SFNumberNode) {
                                numberToLeft = temp
                                break
                            }
                        }

                        var numberToRight: SFNumberNode? = null
                        for(lookRight in i+1 until listView.size) {
                            val temp = listView[lookRight]
                            if(temp is SFNumberNode) {
                                numberToRight = temp
                                break
                            }
                        }

                        if(numberToLeft != null) {
                            numberToLeft.numVal += explodingLeft.numVal
                        }

                        if(numberToRight != null) {
                            numberToRight.numVal += explodingRight.numVal
                        }

                        //replace target with 0
                        val parent = findParent(targetNode)
                        if(parent != null && parent.leftNode === targetNode) {
                            parent.leftNode = SFNumberNode(0, parent.level+1)
                        } else if(parent != null && parent.rightNode === targetNode) {
                            parent.rightNode = SFNumberNode(0, parent.level+1)
                        } else {
                            throw Exception("parent not found while exploding")
                        }

                        return
                    }
                }
            } else {
                throw Exception("invariant violated: 'Exploding pairs will always consist of two regular numbers.'")
            }
        }

        // mutates!
        fun split(targetNode: SFNumberNode) {
            val newLeft = floor(targetNode.numVal.toDouble() / 2).toInt()
            val newRight = ceil(targetNode.numVal.toDouble() / 2).toInt()
            assert(newLeft + newRight == targetNode.numVal)

            val parent = findParent(targetNode)

            if(parent != null) {
                val newNode = SFPairNode(
                    SFNumberNode(newLeft, parent.level + 2),
                    SFNumberNode(newRight, parent.level + 2),
                    parent.level + 1,
                )

                if(parent.leftNode === targetNode) {
                    parent.leftNode = newNode
                } else {
                    parent.rightNode = newNode
                }
            } else {
                throw Exception("parent not found while splitting")
            }
        }

        // mutates!
        fun reduce(): Boolean {
            for(node in nodesInOrder()) {
                if(node is SFPairNode && node.level >= 4) {
                    println("exploding $node")
                    explode(node)
                    return true
                }
            }

            for(node in nodesInOrder()) {
                if(node is SFNumberNode && node.numVal >= 10) {
                    println("splitting $node")
                    split(node)
                    return true
                }
            }

            return false
        }

        // mutates!
        fun reduceFully() {
            while(reduce()) {
                println("continuing reduction...")
            }
            println("reduction complete")
        }

        fun magnitude(): Long {
            return rootNode.magnitude()
        }

        override
        fun toString(): String {
            return rootNode.toString()
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

    fun part2(inputSnailfishNumbers: List<SnailfishNumber>): Long {
        /*
        What is the largest magnitude you can get from adding only two of the snailfish numbers?
        Note that snailfish addition is not commutative - that is, x + y and y + x can produce different results.
         */
        var maxMagnitude: Long = 0
        for(i in inputSnailfishNumbers.indices) {
            for(j in inputSnailfishNumbers.indices) {
                if(i == j) continue

                val temp1 = inputSnailfishNumbers[i] + inputSnailfishNumbers[j]
                temp1.reduceFully()
                val mag1 = temp1.magnitude()

                val temp2 = inputSnailfishNumbers[j] + inputSnailfishNumbers[i]
                temp2.reduceFully()
                val mag2 = temp2.magnitude()

                maxMagnitude = maxOf(maxMagnitude, mag1, mag2)
            }
        }
        return maxMagnitude
    }

    fun run() {
        val input18 = File("${Main.aocRoot}/other/18/input18").readText().trim()

        println("Part 1")
        val inputSnailfishNumbers = input18.split("\n").map {
            SnailfishNumber.fromInputString(it)
        }

        var allAddedUp = inputSnailfishNumbers[0]
        for(next in inputSnailfishNumbers.drop(1)) {
            allAddedUp += next
            allAddedUp.reduceFully()
        }

        val magnitude = allAddedUp.magnitude()
        println("(p1 answer) magnitude of sum = $magnitude") // 3734


        println("Part 2")
        val maxMagnitude = part2(inputSnailfishNumbers)
        println("(p2 answer) max magnitude from adding 2 input numbers = $maxMagnitude") // 4837
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
        fun `addition example 1`() {
            val a = SnailfishNumber.fromInputString("[1,1]")
            val b = SnailfishNumber.fromInputString("[2,2]")
            val c = SnailfishNumber.fromInputString("[3,3]")
            val d = SnailfishNumber.fromInputString("[4,4]")
            val expected = "[[[[1,1],[2,2]],[3,3]],[4,4]]"
            val computed = a + b + c + d
            assertEquals(expected, computed.toString())
        }

        @Test
        fun `addition example 2`() {
            val a = SnailfishNumber.fromInputString("[1,1]")
            val b = SnailfishNumber.fromInputString("[2,2]")
            val c = SnailfishNumber.fromInputString("[3,3]")
            val d = SnailfishNumber.fromInputString("[4,4]")
            val e = SnailfishNumber.fromInputString("[5,5]")
            val expected = "[[[[3,0],[5,3]],[4,4]],[5,5]]"
            var computed = a + b + c + d
            computed.reduceFully()
            computed += e
            computed.reduceFully()
            assertEquals(expected, computed.toString())
        }

        @Test
        fun `addition example 3`() {
            val a = SnailfishNumber.fromInputString("[1,1]")
            val b = SnailfishNumber.fromInputString("[2,2]")
            val c = SnailfishNumber.fromInputString("[3,3]")
            val d = SnailfishNumber.fromInputString("[4,4]")
            val e = SnailfishNumber.fromInputString("[5,5]")
            val f = SnailfishNumber.fromInputString("[6,6]")
            val expected = "[[[[5,0],[7,4]],[5,5]],[6,6]]"
            var computed = a + b + c + d
            computed.reduceFully()
            computed += e
            computed.reduceFully()
            computed += f
            computed.reduceFully()
            assertEquals(expected, computed.toString())
        }

        @Test
        fun `addition example 4`() {
            val a = SnailfishNumber.fromInputString("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]")
            val b = SnailfishNumber.fromInputString("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]")
            val c = SnailfishNumber.fromInputString("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]")
            val d = SnailfishNumber.fromInputString("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]")
            val e = SnailfishNumber.fromInputString("[7,[5,[[3,8],[1,4]]]]")
            val f = SnailfishNumber.fromInputString("[[2,[2,2]],[8,[8,1]]]")
            val g = SnailfishNumber.fromInputString("[2,9]")
            val h = SnailfishNumber.fromInputString("[1,[[[9,3],9],[[9,0],[0,7]]]]")
            val i = SnailfishNumber.fromInputString("[[[5,[7,4]],7],1]")
            val j = SnailfishNumber.fromInputString("[[[[4,2],2],6],[8,7]]")
            var computed = a + b
            computed.reduceFully()
            for(next in listOf(c, d, e, f, g, h, i, j)) {
                computed += next
                computed.reduceFully()
            }
            val expected = "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
            assertEquals(expected, computed.toString())
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

        @Test
        fun `find parent 1`() {
            val sf = SnailfishNumber.fromInputString("[1,2]")
            val target = sf.rootNode.leftNode // 1
            val expected = sf.rootNode
            val computed = sf.findParent(target)
            assertEquals(expected, computed)
        }

        @Test
        fun `find parent 2`() {
            val sf = SnailfishNumber.fromInputString("[[1,2],3]")
            val target = (sf.rootNode.leftNode as SFPairNode).rightNode // 2
            val expected = sf.rootNode.leftNode
            val computed = sf.findParent(target)
            assertEquals(expected, computed)
        }

        @Test
        fun `explode example 1`() {
            val sf = SnailfishNumber.fromInputString("[[[[[9,8],1],2],3],4]")
            val expected = "[[[[0,9],2],3],4]"
            val target = sf.nodesInOrder().firstOrNull { it.toString() == "[9,8]" } as SFPairNode
            sf.explode(target)
            val computed = sf.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `explode example 2`() {
            val sf = SnailfishNumber.fromInputString("[7,[6,[5,[4,[3,2]]]]]")
            val expected = "[7,[6,[5,[7,0]]]]"
            val target = sf.nodesInOrder().firstOrNull { it.toString() == "[3,2]" } as SFPairNode
            sf.explode(target)
            val computed = sf.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `explode example 3`() {
            val sf = SnailfishNumber.fromInputString("[[6,[5,[4,[3,2]]]],1]")
            val expected = "[[6,[5,[7,0]]],3]"
            val target = sf.nodesInOrder().firstOrNull { it.toString() == "[3,2]" } as SFPairNode
            sf.explode(target)
            val computed = sf.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `explode example 4`() {
            val sf = SnailfishNumber.fromInputString("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")
            val expected = "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"
            val target = sf.nodesInOrder().firstOrNull { it.toString() == "[7,3]" } as SFPairNode
            sf.explode(target)
            val computed = sf.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `explode example 5`() {
            val sf = SnailfishNumber.fromInputString("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
            val expected = "[[3,[2,[8,0]]],[9,[5,[7,0]]]]"
            val target = sf.nodesInOrder().firstOrNull { it.toString() == "[3,2]" } as SFPairNode
            sf.explode(target)
            val computed = sf.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `split example 1`() {
            val sf = SnailfishNumber.fromInputString("[[[[0,7],4],[15,[0,13]]],[1,1]]")
            val expected = "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"
            val target = sf.nodesInOrder().firstOrNull { it.toString() == "15" } as SFNumberNode
            sf.split(target)
            val computed = sf.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `split example 2`() {
            val sf = SnailfishNumber.fromInputString("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")
            val expected = "[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"
            val target = sf.nodesInOrder().firstOrNull { it.toString() == "13" } as SFNumberNode
            sf.split(target)
            val computed = sf.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `reduce example one step at a time`() {
            val sf = SnailfishNumber.fromInputString("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]")

            val after1 = "[[[[0,7],4],[7,[[8,4],9]]],[1,1]]"
            sf.reduce()
            assertEquals(after1, sf.toString())

            val after2 = "[[[[0,7],4],[15,[0,13]]],[1,1]]"
            sf.reduce()
            assertEquals(after2, sf.toString())

            val after3 = "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"
            sf.reduce()
            assertEquals(after3, sf.toString())

            val after4 = "[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"
            sf.reduce()
            assertEquals(after4, sf.toString())

            val after5 = "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"
            sf.reduce()
            assertEquals(after5, sf.toString())
        }

        @Test
        fun `reduce example all at once`() {
            val sf = SnailfishNumber.fromInputString("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]")
            val expected = "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"
            sf.reduceFully()
            assertEquals(expected, sf.toString())
        }

        @Test
        fun `part 1 sample`() {
            val a = SnailfishNumber.fromInputString("[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]")
            val b = SnailfishNumber.fromInputString("[[[5,[2,8]],4],[5,[[9,9],0]]]")
            val c = SnailfishNumber.fromInputString("[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]")
            val d = SnailfishNumber.fromInputString("[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]")
            val e = SnailfishNumber.fromInputString("[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]")
            val f = SnailfishNumber.fromInputString("[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]")
            val g = SnailfishNumber.fromInputString("[[[[5,4],[7,7]],8],[[8,3],8]]")
            val h = SnailfishNumber.fromInputString("[[9,3],[[9,9],[6,[4,9]]]]")
            val i = SnailfishNumber.fromInputString("[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]")
            val j = SnailfishNumber.fromInputString("[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]")
            var computedSum = a + b
            computedSum.reduceFully()
            for(next in listOf(c, d, e, f, g, h, i, j)) {
                computedSum += next
                computedSum.reduceFully()
            }
            val expectedSum = "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]"
            assertEquals(expectedSum, computedSum.toString())

            assertEquals(4140, computedSum.magnitude())
        }

        @Test
        fun `part 2 sample`() {
            val inputString = """
                [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
                [[[5,[2,8]],4],[5,[[9,9],0]]]
                [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
                [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
                [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
                [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
                [[[[5,4],[7,7]],8],[[8,3],8]]
                [[9,3],[[9,9],[6,[4,9]]]]
                [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
                [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
            """.trimIndent().trim()
            val inputSnailfishNumbers = inputString.split("\n").map {
                SnailfishNumber.fromInputString(it)
            }
            val computed = part2(inputSnailfishNumbers)
            val expected: Long = 3993
            assertEquals(expected, computed)
        }
    }
}