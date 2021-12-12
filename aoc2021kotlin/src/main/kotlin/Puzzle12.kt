package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File
import java.lang.Exception


object Puzzle12 {
    enum class CaveSize { SMALL, BIG }

    data class Node(val name: String) {
        val size = if(name == name.uppercase()) CaveSize.BIG else CaveSize.SMALL

        companion object {
            fun pairFromInputLine(inputLine: String): Pair<Node, Node> {
                val (firstNodeName, secondNodeName) = inputLine.split("-")
                val nodeA = Node(firstNodeName)
                val nodeB = Node(secondNodeName)
                return Pair(nodeA, nodeB)
            }
        }
    }

    class Path {
        val nodes = mutableListOf<Node>()

        constructor(firstNode: Node) {
            nodes.add(firstNode)
        }

        constructor(otherPath: Path) {
            nodes.addAll(otherPath.nodes)
        }

        // to ease testing
        constructor(pathString: String) {
            for(nodeName in pathString.split(",")) {
                nodes.add(Node(nodeName))
            }
        }

        fun isValidNextNode(n: Node): Boolean {
            return !(n.size == CaveSize.SMALL && nodes.contains(n))
        }

        fun append(n: Node) {
            if(!isValidNextNode(n)) {
                throw Exception("size=SMALL node $n is already on this path!")
            }
            nodes.add(n)
        }

        override
        fun toString(): String {
            return nodes.map { it.name }.joinToString(separator = ",")
        }

        // comparing sets-of-Path for equality does not work without these
        // since the string representation is high-fidelity, I'm taking shortcuts here
        override
        fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            return this.toString() == other.toString()
        }

        override
        fun hashCode(): Int {
            return this.toString().hashCode()
        }
    }

    class Graph(private val adjacency: Map<Node, Set<Node>>) {
        val paths = mutableSetOf<Path>()

        fun getAdjacentNodes(refNode: Node): Set<Node> {
            return adjacency.getOrDefault(refNode, setOf())
        }

        fun startToEndPaths(): Set<Path> {
            // assumes every graph has at least one path from "start" to "end"
            if(paths.isEmpty()) {
                val startNode = Node("start")
                for(n in getAdjacentNodes(startNode)) {
                    tracePath(Path(startNode), n)
                }
            }

            return paths
        }

        private fun tracePath(currentPath: Path, nextNode: Node) {
            val endNode = Node("end")
            if(nextNode == endNode) {
                currentPath.append(endNode)
                paths.add(currentPath)
            } else if(currentPath.isValidNextNode(nextNode)) {
                currentPath.append(nextNode)
                for(n in getAdjacentNodes(nextNode)) {
                    tracePath(Path(currentPath), n) // tracePath mutates the path so pass a copy
                }
            }
            // else this path can't go through the requested node, so drop it on the floor
        }

        companion object {
            fun fromInputString(inputString: String): Graph {
                val adjacency = mutableMapOf<Node, Set<Node>>().withDefault { mutableSetOf() }
                inputString.split("\n").map { line ->
                    val (nodeA, nodeB) = Node.pairFromInputLine(line)
                    adjacency[nodeA] = adjacency.getValue(nodeA).plus(nodeB)
                    adjacency[nodeB] = adjacency.getValue(nodeB).plus(nodeA)
                }
                return Graph(adjacency)
            }
        }
    }

    fun run() {
        val input12 = File("${Main.aocRoot}/other/12/input12").readText().trim()

        println("Part 1")
        val graph = Graph.fromInputString(input12)
        val paths = graph.startToEndPaths()
        println("(p1 answer) count of start->end paths: ${paths.size}")
    }

    /******************************************************************************************************************/

    class Puzzle12Test {
        val SAMPLE_INPUT_1 = """
            start-A
            start-b
            A-c
            A-b
            b-d
            A-end
            b-end
        """.trimIndent().trim()

        val SAMPLE_INPUT_2 = """
            dc-end
            HN-start
            start-kj
            dc-start
            dc-HN
            LN-dc
            HN-end
            kj-sa
            kj-HN
            kj-dc
        """.trimIndent().trim()

        val SAMPLE_INPUT_3 = """
            fs-end
            he-DX
            fs-he
            start-DX
            pj-DX
            end-zg
            zg-sl
            zg-pj
            pj-he
            RW-he
            fs-DX
            pj-RW
            zg-RW
            start-pj
            he-WI
            zg-he
            pj-fs
            start-RW
        """.trimIndent().trim()

        @Test
        fun `nodes from line`() {
            val inputLine = "A-b"
            val (nodeA, nodeB) = Node.pairFromInputLine(inputLine)
            assertEquals(nodeA, Node("A"))
            assertEquals(nodeA.size, CaveSize.BIG)
            assertEquals(nodeB, Node("b"))
            assertEquals(nodeB.size, CaveSize.SMALL)
        }

        @Test
        fun `construct graph from sample input `() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_1)
            assertTrue(graph.getAdjacentNodes(Node("start")) == setOf(
                Node("A"),
                Node("b"),
            ))
            assertTrue(graph.getAdjacentNodes(Node("A")) == setOf(
                Node("start"),
                Node("b"),
                Node("c"),
                Node("end"),
            ))
            assertTrue(graph.getAdjacentNodes(Node("b")) == setOf(
                Node("start"),
                Node("A"),
                Node("d"),
                Node("end"),
            ))
            assertTrue(graph.getAdjacentNodes(Node("c")) == setOf(
                Node("A"),
            ))
            assertTrue(graph.getAdjacentNodes(Node("d")) == setOf(
                Node("b"),
            ))
            assertTrue(graph.getAdjacentNodes(Node("end")) == setOf(
                Node("A"),
                Node("b"),
            ))
        }

        @Test
        fun `paths in sample 1`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_1)
            val paths = graph.startToEndPaths()
            val expectedPaths = setOf(
                Path("start,A,b,A,c,A,end"),
                Path("start,A,b,A,end"),
                Path("start,A,b,end"),
                Path("start,A,c,A,b,A,end"),
                Path("start,A,c,A,b,end"),
                Path("start,A,c,A,end"),
                Path("start,A,end"),
                Path("start,b,A,c,A,end"),
                Path("start,b,A,end"),
                Path("start,b,end"),
            )
            assertEquals(paths.size, expectedPaths.size) // 10
            assertEquals(paths, expectedPaths)
        }

        @Test
        fun `paths in sample 2`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_2)
            val paths = graph.startToEndPaths()
            val expectedPaths = setOf(
                Path("start,HN,dc,HN,end"),
                Path("start,HN,dc,HN,kj,HN,end"),
                Path("start,HN,dc,end"),
                Path("start,HN,dc,kj,HN,end"),
                Path("start,HN,end"),
                Path("start,HN,kj,HN,dc,HN,end"),
                Path("start,HN,kj,HN,dc,end"),
                Path("start,HN,kj,HN,end"),
                Path("start,HN,kj,dc,HN,end"),
                Path("start,HN,kj,dc,end"),
                Path("start,dc,HN,end"),
                Path("start,dc,HN,kj,HN,end"),
                Path("start,dc,end"),
                Path("start,dc,kj,HN,end"),
                Path("start,kj,HN,dc,HN,end"),
                Path("start,kj,HN,dc,end"),
                Path("start,kj,HN,end"),
                Path("start,kj,dc,HN,end"),
                Path("start,kj,dc,end"),
            )
            assertEquals(paths.size, expectedPaths.size) // 19
            assertEquals(paths, expectedPaths)
        }

        @Test
        fun `path count in sample 3`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_3)
            val paths = graph.startToEndPaths()
            assertEquals(paths.size, 226)
        }
    }
}