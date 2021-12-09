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

    fun adjacentPoints(x: Int, y: Int): Set<Pair<Int, Int>> {
        return setOf(Pair(x,y-1), Pair(x,y+1), Pair(x-1,y), Pair(x+1,y))
    }

    // only valid if called with the x,y of a low point
    fun getRiskLevel(x: Int, y: Int): Int {
        return yxGrid[y][x] + 1
    }

    fun findLowPoints(): Set<Pair<Int, Int>> {
        return yxGrid.flatMapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                if(isLowPoint(x, y)) Pair(x, y) else null
            }
        }.filterNotNull().toSet()
    }

    fun getTotalRiskLevel(): Int {
        return findLowPoints().sumOf {
            getRiskLevel(it.first, it.second)
        }
    }

    // may(??) only work if called with the x,y of a low point
    fun getBasinSize(x: Int, y: Int): Int {
        return getBasinSize(x, y, mutableSetOf())
    }

    private fun getBasinSize(x: Int, y: Int, basinPoints: MutableSet<Pair<Int, Int>>): Int {
        //println("getBasinSize (recursive) checking ($x, $y)")
        // this point
        var size = 1
        basinPoints.add(Pair(x, y))

        for(p in adjacentPoints(x, y)) {
            //if we haven't collected that point already...
            if(p !in basinPoints) {
                val x2 = p.first
                val y2 = p.second
                val height = yxGrid.getOrNull(y2)?.getOrNull(x2)
                //...and that point exists and isn't height 9, fire off a recursive call
                if(height != null && height < 9) {
                    size += getBasinSize(x2, y2, basinPoints)
                }
            }
        }

        return size
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
        samplePart2()
        part2(input09)
    }

    fun part1(stringInput: String) {
        println("Part 1")

        val heightmap = Heightmap.fromStringInput(stringInput)
        val totalRiskLevel = heightmap.getTotalRiskLevel()
        println("(p1 answer) total risk level: $totalRiskLevel") // 594
    }

    fun samplePart1() {
        println("SAMPLE P1")

        val heightmap = Heightmap.fromStringInput(SAMPLE_INPUT)
        val totalRiskLevel = heightmap.getTotalRiskLevel()
        println("total risk level: $totalRiskLevel")
        assert(totalRiskLevel == 15)
    }

    fun part2(stringInput: String) {
        println("Part 2")

        val heightmap = Heightmap.fromStringInput(stringInput)
        val lowPoints = heightmap.findLowPoints()
        val basinSizeList = mutableListOf<Int>()
        for(lp in lowPoints) {
            val basinSize = heightmap.getBasinSize(lp.first, lp.second)
            basinSizeList.add(basinSize)
            println("lp $lp has basin of size $basinSize")
        }
        val threeLargest = basinSizeList.sortedDescending().take(3)
        val p2answer = threeLargest.reduceRight { elem, next -> elem * next }
        println("(p2 answer) product of 3 largest basin sizes $threeLargest: $p2answer") // 858494
    }

    fun samplePart2() {
        println("SAMPLE P2")

        val heightmap = Heightmap.fromStringInput(SAMPLE_INPUT)
        val lowPoints = heightmap.findLowPoints()
        val basinSizeList = mutableListOf<Int>()
        for(lp in lowPoints) {
            val basinSize = heightmap.getBasinSize(lp.first, lp.second)
            basinSizeList.add(basinSize)
            println("lp $lp has basin of size $basinSize")
        }
        val threeLargest = basinSizeList.sortedDescending().take(3)
        val p2answer = threeLargest.reduceRight { elem, next -> elem * next }
        println("product of 3 largest basin sizes $threeLargest: $p2answer")
        assert(threeLargest == listOf(14, 9, 9))
        assert(p2answer == 1134)
    }

    private val SAMPLE_INPUT = """
        2199943210
        3987894921
        9856789892
        8767896789
        9899965678
    """.trimIndent().trim()
}