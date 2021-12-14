package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

object Puzzle13 {
    enum class FoldDirection { HORIZONTAL, VERTICAL }

    data class FoldInstruction(val direction: FoldDirection, val index: Int)

    class FoldableGrid(private val yxGrid: List<List<Char>>) {
        val xSize = yxGrid[0].size
        val ySize = yxGrid.size

        fun dotCount(): Int {
            return yxGrid.sumOf { row ->
                row.count { it == '#' }
            }
        }

        fun enactFold(instruction: FoldInstruction): FoldableGrid {
            return when(instruction.direction) {
                FoldDirection.HORIZONTAL -> enactHorizontalFold(instruction.index)
                FoldDirection.VERTICAL -> enactVerticalFold(instruction.index)
            }
        }

        private fun enactHorizontalFold(foldIndex: Int): FoldableGrid {
            // create a new grid from the part that isn't being folded
            val tempGrid = yxGrid.map { row ->
                row.slice(0 until foldIndex).toMutableList()
            }.toMutableList()

            // neither loop includes foldIndex, the part of the grid along the fold line is lost

            // mirror around the fold line, marked locations only
            var mirrorX = foldIndex-1
            for(x in foldIndex+1 until xSize) {
                for(y in 0 until ySize) {
                    if(yxGrid[y][x] == '#') {
                        tempGrid[y][mirrorX] = '#'
                    }
                }
                mirrorX -= 1
            }

            return FoldableGrid(tempGrid)
        }

        private fun enactVerticalFold(foldIndex: Int): FoldableGrid {
            // create a new grid from the part that isn't being folded
            val tempGrid = yxGrid.slice(0 until foldIndex).map {
                it.toMutableList()
            }.toMutableList()

            // neither loop includes foldIndex, the part of the grid along the fold line is lost

            // mirror around the fold line, marked locations only
            var mirrorY = foldIndex-1
            for(y in foldIndex+1 until ySize) {
                for(x in 0 until xSize) {
                    if(yxGrid[y][x] == '#') {
                        tempGrid[mirrorY][x] = '#'
                    }
                }
                mirrorY -= 1
            }

            return FoldableGrid(tempGrid)
        }

        override
        fun toString(): String {
            return yxGrid.joinToString(separator = "\n") { row ->
                row.joinToString(separator = "")
            }
            /* above is IDE-suggested shortening of:
            return yxgrid.map { row ->
                row.joinToString(separator = "")
            }.joinToString(separator = "\n")
            */
        }

        companion object {
            fun fromInputString(inputString: String): FoldableGrid {
                val (gridPart, foldPart) = inputString.split("\n\n")
                val markedPoints = gridPart.split("\n").map {
                    val (x, y) = it.split(",")
                    Point2(x.toInt(), y.toInt())
                }

                val xMax = markedPoints.maxOf { it.x }
                val yMax = markedPoints.maxOf { it.y }

                // build an empty grid
                val tempGrid: MutableList<MutableList<Char>> = mutableListOf()
                for(y in 0..yMax) {
                    val row = mutableListOf<Char>()
                    for(x in 0..xMax) {
                        row.add('.')
                    }
                    tempGrid.add(row)
                }

                for(point in markedPoints) {
                    tempGrid[point.y][point.x] = '#'
                }

                return FoldableGrid(tempGrid)
            }
        }
    }

    fun parseFoldInstructions(inputString: String): List<FoldInstruction> {
        val (gridPart, foldPart) = inputString.split("\n\n")
        return foldPart.split("\n").map {
            val (directionPart, indexPart) = it.split("=")
            val direction = if(directionPart == "fold along x") FoldDirection.HORIZONTAL else FoldDirection.VERTICAL
            val index = indexPart.toInt()
            FoldInstruction(direction, index)
        }
    }

    fun run() {
        val input13 = File("${Main.aocRoot}/other/13/input13").readText().trim()

        val grid = FoldableGrid.fromInputString(input13)
        val instructions = parseFoldInstructions(input13)

        val afterOneFold = grid.enactFold(instructions[0])

        println("(p1 answer) dots visible after 1 fold: ${afterOneFold.dotCount()}")
    }

    class Puzzle13Test {
        val SAMPLE_INPUT = """
            6,10
            0,14
            9,10
            0,3
            10,4
            4,11
            6,0
            6,12
            4,1
            0,13
            10,12
            3,4
            3,0
            8,4
            1,10
            2,14
            8,10
            9,0

            fold along y=7
            fold along x=5
        """.trimIndent().trim()

        @Test
        fun `fold intruction parsing`() {
            val foldInstructions = parseFoldInstructions(SAMPLE_INPUT)
            assertEquals(2, foldInstructions.size)
            assertEquals(FoldInstruction(FoldDirection.VERTICAL, 7), foldInstructions[0])
            assertEquals(FoldInstruction(FoldDirection.HORIZONTAL, 5), foldInstructions[1])
        }

        @Test
        fun `grid construction`() {
            val expected = """
                ...#..#..#.
                ....#......
                ...........
                #..........
                ...#....#.#
                ...........
                ...........
                ...........
                ...........
                ...........
                .#....#.##.
                ....#......
                ......#...#
                #..........
                #.#........
            """.trimIndent().trim()
            val grid = FoldableGrid.fromInputString(SAMPLE_INPUT)
            val computed = grid.toString()
            assertEquals(expected, computed)
        }

        @Test
        fun `dot count`() {
            val grid = FoldableGrid.fromInputString(SAMPLE_INPUT)
            val computed = grid.dotCount()
            assertEquals(18, computed)
        }

        @Test
        fun `first sample fold`() {
            val expected = """
                #.##..#..#.
                #...#......
                ......#...#
                #...#......
                .#.#..#.###
                ...........
                ...........
            """.trimIndent().trim()
            val grid = FoldableGrid.fromInputString(SAMPLE_INPUT)
            val foldInstruction = FoldInstruction(FoldDirection.VERTICAL, 7) // first instruction from sample
            val computed = grid.enactFold(foldInstruction)
            assertEquals(expected, computed.toString())
            assertEquals(17, computed.dotCount())
        }

        @Test
        fun `second sample fold`() { // not an ideal test because it depends on the above working also, but whatever
            val expected = """
                #####
                #...#
                #...#
                #...#
                #####
                .....
                .....
            """.trimIndent().trim()
            val grid = FoldableGrid.fromInputString(SAMPLE_INPUT)
            val foldInstruction1 = FoldInstruction(FoldDirection.VERTICAL, 7) // first instruction from sample
            val foldInstruction2 = FoldInstruction(FoldDirection.HORIZONTAL, 5) // second instruction from sample
            val afterOneFold = grid.enactFold(foldInstruction1)
            val afterTwoFolds = afterOneFold.enactFold(foldInstruction2)
            assertEquals(expected, afterTwoFolds.toString())
            assertEquals(16, afterTwoFolds.dotCount())
        }
    }
}