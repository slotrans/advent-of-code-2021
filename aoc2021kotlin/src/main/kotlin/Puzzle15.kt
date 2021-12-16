package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class RiskGrid {
    private val yxgrid: Array<Array<Int>>
    private val xMax: Int
    private val yMax: Int

    constructor(inputString: String) {
        val inputLines = inputString.split("\n")
        yxgrid = Array(inputLines.size) {
            Array(inputLines[0].length) {
                0
            }
        }
        inputLines.mapIndexed { y, line ->
            line.mapIndexed { x, cell ->
                yxgrid[y][x] = cell.digitToInt()
            }
        }
        yMax = yxgrid.size - 1
        xMax = yxgrid[0].size - 1
    }

    fun findLeastCost(): Int {
        //return findLeastCost(xMax, yMax, setOf())
        return findLeastCost(xMax, yMax)
    }

    private val COST_CACHE = mutableMapOf<Point2, Int>()

    private fun findLeastCost(x: Int, y: Int): Int {
        val point = Point2(x, y)
        if(point in COST_CACHE) {
            return COST_CACHE[point]!!
        }

        var out: Int
        if(x < 0 || y < 0 || x > xMax || y > yMax) {
            out = Int.MAX_VALUE/2
        } else if(x == 0 && y == 0) {
            out = 0 // cost of (0,0) doesn't count, per instructions
        } else {
            out = yxgrid[y][x] + minOf(
                findLeastCost(x-1, y), // left
                findLeastCost(x, y-1), // up
            )
        }

        COST_CACHE[point] = out
        return out
    }

    private fun findLeastCost(x: Int, y: Int, pointsSoFar: Set<Point2>): Int {
        //print("costing point ($x, $y)...")
        if(x < 0 || y < 0 || x > xMax || y > yMax) {
            //println("out of bounds")
            return Int.MAX_VALUE/2
        }

        if(Point2(x, y) in pointsSoFar) {
            //println("already visited")
            return Int.MAX_VALUE/2
        }

        // recursive terminal case
        if(x == 0 && y == 0) {
            //println("done!")
            return 0 // cost of (0,0) doesn't count, per instructions
        }

        //println("continuing search")
        val newPointSet = pointsSoFar.plus(Point2(x, y))
        return yxgrid[y][x] + minOf(
            findLeastCost(x-1, y, newPointSet), // left
            findLeastCost(x, y-1, newPointSet), // up
            //findLeastCost(x+1, y, newPointSet), // right
            //findLeastCost(x, y+1, newPointSet), // down
        )
    }

    override
    fun toString(): String {
        return yxgrid.joinToString(separator = "\n") { row ->
            row.joinToString(separator = "") { cell ->
                cell.toString()
            }
        }
    }
}

object Puzzle15 {
    fun run() {
        val input15 = File("${Main.aocRoot}/other/15/input15").readText().trim()
        val grid = RiskGrid(input15)

        println("Part 1")
        val p1answer = grid.findLeastCost()
        println("(p1 answer) least cost = $p1answer") // 421 = wrong, too high
    }

    class Puzzle15Test {
        val TINY_INPUT = """
            123
            482
            153
        """.trimIndent()

        val SAMPLE_INPUT = """
            1163751742
            1381373672
            2136511328
            3694931569
            7463417111
            1319128137
            1359912421
            3125421639
            1293138521
            2311944581
        """.trimIndent().trim()

        @Test
        fun `grid construction`() {
            val grid = RiskGrid(SAMPLE_INPUT)
            assertEquals(SAMPLE_INPUT, grid.toString())
        }

        @Test
        fun `least cost for tiny input`() {
            val grid = RiskGrid(TINY_INPUT)
            val expected = 10
            val computed = grid.findLeastCost()
            assertEquals(expected, computed)
        }

        @Test
        fun `least cost for sample`() {
            val grid = RiskGrid(SAMPLE_INPUT)
            val expected = 40
            val computed = grid.findLeastCost()
            assertEquals(expected, computed)
        }
    }
}