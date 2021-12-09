package net.blergh.advent2021

import java.io.File

class Heightmap(private val yxGrid: List<List<Int>>) {
    fun isLowPoint(x: Int, y:Int): Boolean {
        val adjacentHeights = listOfNotNull(
            yxGrid.getOrNull(y)?.getOrNull(x-1),
            yxGrid.getOrNull(y)?.getOrNull(x+1),
            yxGrid.getOrNull(y-1)?.getOrNull(x),
            yxGrid.getOrNull(y+1)?.getOrNull(x),
        )
        return adjacentHeights.all { yxGrid[y][x] < it }
    }

    fun getRiskLevel(x: Int, y: Int): Int {
        return yxGrid[y][x] + 1
    }

    fun getTotalRiskLevel(): Int {
        return yxGrid.flatMapIndexed() { y, row ->
            row.mapIndexed { x, cell ->
                if(isLowPoint(x, y)) getRiskLevel(x, y) else 0
            }
        }.sum()
    }

    companion object {
        fun fromStringInput(stringInput: String): Heightmap {
            return Heightmap(stringInput.split("\n").map { line ->
                line.map { heightValue ->
                    heightValue.toString().toInt()
                }
            })
        }
    }
}

object Puzzle09 {
    fun run() {
        val input09 = File("${Main.aocRoot}/other/09/input09").readText().trim()

        samplePart1()
        part1(input09)
    }

    fun part1(stringInput: String) {
        println("Part 1")

        val heightmap = Heightmap.fromStringInput(stringInput)
        val totalRiskLevel = heightmap.getTotalRiskLevel()
        println("(p1 answer) total risk level: $totalRiskLevel")
    }

    fun samplePart1() {
        println("SAMPLE P1")

        val heightmap = Heightmap.fromStringInput(SAMPLE_INPUT)
        val totalRiskLevel = heightmap.getTotalRiskLevel()
        println("total risk level: $totalRiskLevel")
        assert(totalRiskLevel == 15)
    }

    private val SAMPLE_INPUT = """
        2199943210
        3987894921
        9856789892
        8767896789
        9899965678
    """.trimIndent().trim()
}