package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
        val maxSmallVisits: Int

        constructor(firstNode: Node, _maxSmallVisits: Int) {
            nodes.add(firstNode)
            maxSmallVisits = _maxSmallVisits
        }

        constructor(otherPath: Path) {
            nodes.addAll(otherPath.nodes)
            maxSmallVisits = otherPath.maxSmallVisits
        }

        // to ease testing
        constructor(pathString: String, _maxSmallVisits: Int) {
            for(nodeName in pathString.split(",")) {
                nodes.add(Node(nodeName))
            }
            maxSmallVisits = _maxSmallVisits
        }

        fun isValidNextNode(n: Node): Boolean {
            //return !(n.size == CaveSize.SMALL && nodes.contains(n))
            if(n.size != CaveSize.SMALL) return true

            if(nodes.contains(n) && (n == Node("start") || n == Node("end"))) return false

            if(!nodes.contains(n)) return true

            val mostVisitsToAnySmallCave = nodes.filter { it.size == CaveSize.SMALL }
                .groupingBy { it }
                .eachCount()
                .values
                .maxOf { it }
            if(mostVisitsToAnySmallCave < maxSmallVisits) return true

            return false
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

        fun startToEndPaths(maxSmallVisits: Int): Set<Path> {
            // assumes every graph has at least one path from "start" to "end"
            if(paths.isEmpty()) {
                val startNode = Node("start")
                for(n in getAdjacentNodes(startNode)) {
                    tracePath(Path(startNode, maxSmallVisits), n)
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
        val graph1 = Graph.fromInputString(input12)
        val paths1 = graph1.startToEndPaths(1)
        println("(p1 answer) count of start->end paths: ${paths1.size}") // 5178

        println("Part 2")
        val graph2 = Graph.fromInputString(input12)
        val paths2 = graph2.startToEndPaths(2)
        println("(p2 answer) count of start->end paths: ${paths2.size}") // 130094
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
            assertEquals(Node("A"), nodeA)
            assertEquals(CaveSize.BIG, nodeA.size)
            assertEquals(Node("b"), nodeB)
            assertEquals(CaveSize.SMALL, nodeB.size)
        }

        @Test
        fun `construct graph from sample input `() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_1)
            assertEquals(setOf(
                Node("A"),
                Node("b"),
            ), graph.getAdjacentNodes(Node("start")))
            assertEquals(setOf(
                Node("start"),
                Node("b"),
                Node("c"),
                Node("end"),
            ), graph.getAdjacentNodes(Node("A")))
            assertEquals(setOf(
                Node("start"),
                Node("A"),
                Node("d"),
                Node("end"),
            ), graph.getAdjacentNodes(Node("b")))
            assertEquals(setOf(
                Node("A"),
            ), graph.getAdjacentNodes(Node("c")))
            assertEquals(setOf(
                Node("b"),
            ), graph.getAdjacentNodes(Node("d")))
            assertEquals(setOf(
                Node("A"),
                Node("b"),
            ), graph.getAdjacentNodes(Node("end")))
        }

        @Test
        fun `paths in sample 1 with part1 rules`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_1)
            val paths = graph.startToEndPaths(1)
            val expectedPaths = setOf(
                Path("start,A,b,A,c,A,end", 1),
                Path("start,A,b,A,end", 1),
                Path("start,A,b,end", 1),
                Path("start,A,c,A,b,A,end", 1),
                Path("start,A,c,A,b,end", 1),
                Path("start,A,c,A,end", 1),
                Path("start,A,end", 1),
                Path("start,b,A,c,A,end", 1),
                Path("start,b,A,end", 1),
                Path("start,b,end", 1),
            )
            assertEquals(expectedPaths.size, paths.size) // 10
            assertEquals(expectedPaths, paths)
        }

        @Test
        fun `paths in sample 2 with part1 rules`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_2)
            val paths = graph.startToEndPaths(1)
            val expectedPaths = setOf(
                Path("start,HN,dc,HN,end", 1),
                Path("start,HN,dc,HN,kj,HN,end", 1),
                Path("start,HN,dc,end", 1),
                Path("start,HN,dc,kj,HN,end", 1),
                Path("start,HN,end", 1),
                Path("start,HN,kj,HN,dc,HN,end", 1),
                Path("start,HN,kj,HN,dc,end", 1),
                Path("start,HN,kj,HN,end", 1),
                Path("start,HN,kj,dc,HN,end", 1),
                Path("start,HN,kj,dc,end", 1),
                Path("start,dc,HN,end", 1),
                Path("start,dc,HN,kj,HN,end", 1),
                Path("start,dc,end", 1),
                Path("start,dc,kj,HN,end", 1),
                Path("start,kj,HN,dc,HN,end", 1),
                Path("start,kj,HN,dc,end", 1),
                Path("start,kj,HN,end", 1),
                Path("start,kj,dc,HN,end", 1),
                Path("start,kj,dc,end", 1),
            )
            assertEquals(expectedPaths.size, paths.size) // 19
            assertEquals(expectedPaths, paths)
        }

        @Test
        fun `path count in sample 3 with part1 rules`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_3)
            val paths = graph.startToEndPaths(1)
            assertEquals(226, paths.size)
        }

        @Test
        fun `paths in sample 1 with part2 rules`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_1)
            val paths = graph.startToEndPaths(2)
            val expectedPaths = setOf(
                Path("start,A,b,A,b,A,c,A,end", 2),
                Path("start,A,b,A,b,A,end", 2),
                Path("start,A,b,A,b,end", 2),
                Path("start,A,b,A,c,A,b,A,end", 2),
                Path("start,A,b,A,c,A,b,end", 2),
                Path("start,A,b,A,c,A,c,A,end", 2),
                Path("start,A,b,A,c,A,end", 2),
                Path("start,A,b,A,end", 2),
                Path("start,A,b,d,b,A,c,A,end", 2),
                Path("start,A,b,d,b,A,end", 2),
                Path("start,A,b,d,b,end", 2),
                Path("start,A,b,end", 2),
                Path("start,A,c,A,b,A,b,A,end", 2),
                Path("start,A,c,A,b,A,b,end", 2),
                Path("start,A,c,A,b,A,c,A,end", 2),
                Path("start,A,c,A,b,A,end", 2),
                Path("start,A,c,A,b,d,b,A,end", 2),
                Path("start,A,c,A,b,d,b,end", 2),
                Path("start,A,c,A,b,end", 2),
                Path("start,A,c,A,c,A,b,A,end", 2),
                Path("start,A,c,A,c,A,b,end", 2),
                Path("start,A,c,A,c,A,end", 2),
                Path("start,A,c,A,end", 2),
                Path("start,A,end", 2),
                Path("start,b,A,b,A,c,A,end", 2),
                Path("start,b,A,b,A,end", 2),
                Path("start,b,A,b,end", 2),
                Path("start,b,A,c,A,b,A,end", 2),
                Path("start,b,A,c,A,b,end", 2),
                Path("start,b,A,c,A,c,A,end", 2),
                Path("start,b,A,c,A,end", 2),
                Path("start,b,A,end", 2),
                Path("start,b,d,b,A,c,A,end", 2),
                Path("start,b,d,b,A,end", 2),
                Path("start,b,d,b,end", 2),
                Path("start,b,end", 2),
            )
            assertEquals(expectedPaths.size, paths.size) // 36
            assertEquals(expectedPaths, paths)
        }

        @Test
        fun `path count in sample 2 with part2 rules`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_2)
            val paths = graph.startToEndPaths(2)
            assertEquals(103, paths.size)
        }

        @Test
        fun `path count in sample 3 with part2 rules`() {
            val graph = Graph.fromInputString(SAMPLE_INPUT_3)
            val paths = graph.startToEndPaths(2)
            assertEquals(3509, paths.size)
        }
    }
}