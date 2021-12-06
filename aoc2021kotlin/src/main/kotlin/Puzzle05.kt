package net.blergh.advent2021

import java.io.File

class Line(var x1: Int, var y1: Int, var x2: Int, var y2: Int) {
    fun isHorizontal(): Boolean {
        return y1 == y2
    }

    fun isVertical(): Boolean {
        return x1 == x2
    }

    companion object {
        fun fromInputString(lineString: String): Line {
            val (first, second) = lineString.split(" -> ")
            val (x1str, y1str) = first.split(",")
            val (x2str, y2str) = second.split(",")
            return Line(x1str.toInt(), y1str.toInt(), x2str.toInt(), y2str.toInt())
        }
    }
}

class Grid(private val xSize: Int, private val ySize: Int) {
    private val yxgrid = Array(ySize) {
        Array(xSize) {
            0
        }
    }

    private fun interpolatePoints(line: Line): List<Pair<Int, Int>> {
        val xStep = if(line.x2 == line.x1) 0 else
            if(line.x2 > line.x1) 1 else -1
        val yStep = if(line.y2 == line.y1) 0 else
            if(line.y2 > line.y1) 1 else -1

        val out = mutableListOf<Pair<Int, Int>>()
        var x = line.x1
        var y = line.y1
        var shouldStop = false
        while(!shouldStop) {
            if(x == line.x2 && y == line.y2) {
                shouldStop = true
            }

            out.add(Pair(x, y))

            x += xStep
            y += yStep
        }
        return out
    }

    fun draw(line: Line, enableDiagonal: Boolean = false) {
        if(!enableDiagonal) {
            if(!(line.isHorizontal() or line.isVertical())) {
                return
            }
        }

        for((x, y) in interpolatePoints(line)) {
            yxgrid[y][x] += 1
            val idk = yxgrid[y]
            idk[x]
        }
    }

    fun count2PlusOverlaps(): Int {
        return yxgrid.sumOf { row ->
            row.count { it >= 2 }
        }

// alternate:
//        yxgrid.flatMap { row->
//            row.map { cell ->
//                cell
//            }
//        }.filter { it >= 2 }.count()
    }

    override
    fun toString(): String {
        return yxgrid.joinToString(separator = "\n") { row ->
            row.joinToString(separator = "") { cell ->
                if (cell == 0) "." else cell.toString()
            }
        }
    }
}

object Puzzle05 {
    fun run() {
        val input05 = File("${Main.aocRoot}/other/05/input05").readText().trim()
        val parsedInput = parseInput(input05)

        //samplePart1()
        part1(parsedInput)
        //samplePart2()
        part2(parsedInput)
    }

    private fun parseInput(input: String): List<Line> {
        return input.split("\n").map {
            Line.fromInputString(it)
        }
    }

    private fun getGridDimensions(lines: List<Line>): Pair<Int, Int> {
        var xMax = 0
        var yMax = 0
        for(line in lines) {
            xMax = maxOf(xMax, line.x1, line.x2)
            yMax = maxOf(yMax, line.y1, line.y2)
        }

        return Pair(xMax+1, yMax+1)
    }

    private fun part1(lines: List<Line>) {
        println("Part 1")

        val (xSize, ySize) = getGridDimensions(lines)
        val grid = Grid(xSize, ySize)
        for(line in lines) {
            grid.draw(line)
        }

        val p1Answer = grid.count2PlusOverlaps()
        println("(p1 answer) count of 2+: $p1Answer") // 5442
    }

    private fun part2(lines: List<Line>) {
        println("Part 2")

        val (xSize, ySize) = getGridDimensions(lines)
        val grid = Grid(xSize, ySize)
        for(line in lines) {
            grid.draw(line, enableDiagonal = true)
        }

        val p2Answer = grid.count2PlusOverlaps()
        println("(p2 answer) count of 2+: $p2Answer") // 19571
    }

    private fun samplePart1() {
        val parsedSampleInput = parseInput(SAMPLE_INPUT)

        val (xSize, ySize) = getGridDimensions(parsedSampleInput)
        val grid = Grid(xSize, ySize)
        for(line in parsedSampleInput) {
            grid.draw(line)
        }
        println(grid)
        assert(grid.toString() == SAMPLE_OUTPUT_P1)

        val p1Answer = grid.count2PlusOverlaps()
        println("(sample p1 answer) count of 2+: $p1Answer")
        assert(p1Answer == 5)
    }

    private fun samplePart2() {
        val parsedSampleInput = parseInput(SAMPLE_INPUT)

        val (xSize, ySize) = getGridDimensions(parsedSampleInput)
        val grid = Grid(xSize, ySize)
        for(line in parsedSampleInput) {
            grid.draw(line, enableDiagonal = true)
        }
        println(grid)
        assert(grid.toString() == SAMPLE_OUTPUT_P2)

        val p2Answer = grid.count2PlusOverlaps()
        println("(sample p2 answer) count of 2+: $p2Answer")
        assert(p2Answer == 12)
    }

    private val SAMPLE_INPUT = """
        0,9 -> 5,9
        8,0 -> 0,8
        9,4 -> 3,4
        2,2 -> 2,1
        7,0 -> 7,4
        6,4 -> 2,0
        0,9 -> 2,9
        3,4 -> 1,4
        0,0 -> 8,8
        5,5 -> 8,2
    """.trimIndent()

    private val SAMPLE_OUTPUT_P1 = """
        .......1..
        ..1....1..
        ..1....1..
        .......1..
        .112111211
        ..........
        ..........
        ..........
        ..........
        222111....
    """.trimIndent()

    private val SAMPLE_OUTPUT_P2 = """
        1.1....11.
        .111...2..
        ..2.1.111.
        ...1.2.2..
        .112313211
        ...1.2....
        ..1...1...
        .1.....1..
        1.......1.
        222111....
    """.trimIndent()
}