package net.blergh.advent2021

import java.io.File

object Puzzle07 {
    fun run() {
        val input07 = File("${Main.aocRoot}/other/07/input07").readText().trim()
        val initialPositions = input07.split(",").map { it.toInt() }

        samplePart1()
        part1(initialPositions)
        samplePart2()
        part2(initialPositions)
    }

    private fun minimizeFuelPart1(positions: List<Int>): Pair<Int, Int> {
        val low = positions.minOf { it }
        val high = positions.maxOf { it }
        var bestFuel = Int.MAX_VALUE
        var bestPosition = -1
        for(p in low..high) {
            val totalFuel = positions.map { kotlin.math.abs(it - p) }.sum()
            if(totalFuel < bestFuel) {
                bestPosition = p
                bestFuel = totalFuel
            }
        }

        return Pair(bestPosition, bestFuel)
    }

    private fun minimizeFuelPart2(positions: List<Int>): Pair<Int, Int> {
        val low = positions.minOf { it }
        val high = positions.maxOf { it }
        var bestFuel = Int.MAX_VALUE
        var bestPosition = -1
        for(p in low..high) {
            val totalFuel = positions.map {
                val distance = kotlin.math.abs(it - p)
                (distance * (distance + 1)) / 2
            }.sum()
            if(totalFuel < bestFuel) {
                bestPosition = p
                bestFuel = totalFuel
            }
        }

        return Pair(bestPosition, bestFuel)
    }

    private fun part1(positions: List<Int>) {
        println("Part 1")

        val (bestPosition, totalFuel) = minimizeFuelPart1(positions)
        println("least fuel used at position $bestPosition: $totalFuel") // p=339, fuel=343468
    }

    private fun part2(positions: List<Int>) {
        println("Part 2")

        val (bestPosition, totalFuel) = minimizeFuelPart2(positions)
        println("least fuel used at position $bestPosition: $totalFuel") // p=478, fuel=96086265
    }

    private fun samplePart1() {
        println("SAMPLE P1")

        val positions = SAMPLE_INPUT.split(",").map { it.toInt() }
        val (bestPosition, totalFuel) = minimizeFuelPart1(positions)

        println("least fuel used at position $bestPosition: $totalFuel")
        assert(bestPosition == 2)
        assert(totalFuel == 37)
    }

    private fun samplePart2() {
        println("SAMPLE P2")

        val positions = SAMPLE_INPUT.split(",").map { it.toInt() }
        val (bestPosition, totalFuel) = minimizeFuelPart2(positions)

        println("least fuel used at position $bestPosition: $totalFuel")
        assert(bestPosition == 5)
        assert(totalFuel == 168)
    }


    private const val SAMPLE_INPUT = "16,1,2,0,4,2,7,1,2,14"
}