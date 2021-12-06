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
    private val yxgrid: List<List<Int>>;

    init {
        yxgrid = MutableList(ySize) {
            MutableList(xSize) { 0 }
        };
    }

    fun draw(line: Line) {
        val xStep = if(line.x2 == line.x1) 0 else
            if(line.x2 > line.x1) 1 else -1
        val yStep = if(line.y2 == line.y1) 0 else
            if(line.y2 > line.y1) 1 else -1

        //WON'T WORK, `step` MUST BE POSITIVE!!!!!
        for(x in line.x1..line.x2 step xStep) {
            for(y in line.y1..line.y2 step xStep) {

            }
        }
        
    }

    override
    fun toString(): String {
        return yxgrid.map { row ->
            row.map { cell ->
                if(cell == 0) "." else cell.toString()
            }.joinToString(separator = "")
        }.joinToString(separator = "\n")
    }
}

object Puzzle05 {
    fun run() {
        val input05 = File("${Main.aocRoot}/other/05/input05").readText().trim()
        val parsedInput = parseInput(input05)

        samplePart1()
    }

    private fun parseInput(input: String): List<Line> {
        return input.split("\n").map {
            Line.fromInputString(it)
        }
    }

    private fun samplePart1() {
        val parsedSampleInput = parseInput(SAMPLE_INPUT)

        var xMax = 0
        var yMax = 0
        for(line in parsedSampleInput) {
            xMax = maxOf(xMax, line.x1, line.x2)
            yMax = maxOf(yMax, line.y1, line.y2)
        }

        val grid = Grid(xMax+1, yMax+1)

    }

    val SAMPLE_INPUT = """
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

    val SAMPLE_OUTPUT_P1 = """
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
}