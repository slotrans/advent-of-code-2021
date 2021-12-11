package net.blergh.advent2021

import java.io.File

data class Point2(val x: Int, val y: Int)

class Octopus(var energy: Int) {
    var flashes = 0
    var hasFlashed = false

    fun addEnergy() {
        energy++
    }

    fun flash() {
        hasFlashed = true
    }

    fun shouldFlash(): Boolean {
        return energy > 9 && !hasFlashed
    }

    fun resetIfNeeded() {
        if(hasFlashed) {
            flashes++
            energy = 0
            hasFlashed = false
        }
    }
}

class OctopusGrid(val xSize: Int, val ySize: Int) {
    val sparseGrid = mutableMapOf<Point2, Octopus>()

    companion object {
        fun fromInputString(inputString: String): OctopusGrid {
            val inputLines = inputString.split("\n")
            val xSize = inputLines[0].length
            val ySize = inputLines.size
            val octoGrid = OctopusGrid(xSize, ySize)

            inputLines.mapIndexed { y, line ->
                line.mapIndexed { x, cell ->
                    octoGrid.sparseGrid[Point2(x, y)] = Octopus(cell.digitToInt())
                }
            }

            return octoGrid
        }
    }

    fun nearbyPoints(referencePoint: Point2): List<Point2> {
        val x = referencePoint.x
        val y = referencePoint.y
        return listOf(
            Point2(x-1, y-1), Point2(x, y-1), Point2(x+1, y-1),
            Point2(x-1, y),  /* reference point */ Point2(x+1, y),
            Point2(x-1, y+1), Point2(x, y+1), Point2(x+1, y+1),
        ).filter {
            it in sparseGrid
        }
    }

    fun stepSimulation() {
        // "First, the energy level of each octopus increases by 1."
        val flashPendingPoints = mutableListOf<Point2>()
        for((point, octopus) in sparseGrid) {
            octopus.addEnergy()
            if(octopus.shouldFlash()) {
                flashPendingPoints.add(point)
            }
        }

        // "Then, any octopus with an energy level greater than 9 flashes."
        while(flashPendingPoints.isNotEmpty()) {
            val point = flashPendingPoints.removeLast()
            val flashingOctopus = sparseGrid[point]!!
            if(flashingOctopus.shouldFlash()) {
                flashingOctopus.flash()
                // "This increases the energy level of all adjacent octopuses by 1"
                for(nbPoint in nearbyPoints(point)) {
                    val neighbor = sparseGrid[nbPoint]!!
                    neighbor.addEnergy()
                    // "If this causes an octopus to have an energy level greater than 9, it also flashes."
                    if(neighbor.shouldFlash()) {
                        flashPendingPoints.add(nbPoint)
                    }
                }
            }
        }

        // "Finally, any octopus that flashed during this step has its energy level set to 0, as it used all of its energy to flash."
        for(octopus in sparseGrid.values) {
            octopus.resetIfNeeded()
        }
    }

    fun totalFlashes(): Int {
        return sparseGrid.values.sumOf { it.flashes }
    }

    override
    fun toString(): String {
        val sb = StringBuilder()
        for(y in 0 until ySize) {
            for(x in 0 until xSize) {
                val octopus = sparseGrid[Point2(x, y)]
                sb.append(octopus?.energy?.toString() ?: ".")
            }
            if(y < ySize-1) {
                sb.append("\n")
            }
        }
        return sb.toString()
    }
}

object Puzzle11 {
    fun run() {
        val input11 = File("${Main.aocRoot}/other/11/input11").readText().trim()
        val smallSampleInput11 = """
            11111
            19991
            19191
            19991
            11111
        """.trimIndent().trim()
        val largeSampleInput11 = """
            5483143223
            2745854711
            5264556173
            6141336146
            6357385478
            4167524645
            2176841721
            6882881134
            4846848554
            5283751526
        """.trimIndent().trim()

        println("P1 SMALL SAMPLE")
        val smallSampleFlashes = part1(smallSampleInput11, 2)
        println("total flashes: $smallSampleFlashes")
        assert(smallSampleFlashes == 9)

        println("P1 LARGE SAMPLE")
        val largeSampleFlashes10 = part1(largeSampleInput11, 10)
        println("flashes after 10 steps: $largeSampleFlashes10")
        assert(largeSampleFlashes10 == 204)
        val largeSampleFlashes100 = part1(largeSampleInput11, 100)
        println("flashes after 100 steps: $largeSampleFlashes100")
        assert(largeSampleFlashes100 == 1656)

        println("Part 1")
        val part1Flashes = part1(input11, 100)
        println("(p1 answer) flashes after 100 steps: $part1Flashes") // 1721
    }

    fun part1(inputString: String, steps: Int): Int {
        val octoGrid = OctopusGrid.fromInputString(inputString)
        println("initial state:")
        println(octoGrid)

        for(i in 1..steps) {
            octoGrid.stepSimulation()
            //println("after $i steps")
            //println(octoGrid)
        }

        return octoGrid.totalFlashes()
    }
}