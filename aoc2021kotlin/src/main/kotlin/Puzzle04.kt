package net.blergh.advent2021

import java.io.File

data class Tile(
    val bingoNum: Int,
    var marked: Boolean,
)

class Board(
    val boardId: Int,
    private val fiveInputLines: List<String>
)
{
    // name is a reminder that the indexing is e.g. foo[y][x] / foo[row][col]
    private val yxGrid: List<List<Tile>> = fiveInputLines.map { line ->
        line.trim().split("\\s+".toRegex()).map { bingoNumber ->
            Tile(Integer.parseInt(bingoNumber, 10), false)
        }
    }

    private var isWinner = false


    private fun isWinnerInRow(rowNum: Int): Boolean {
        val rowOfTiles = yxGrid[rowNum]
        for(tile in rowOfTiles) {
            if(!tile.marked) {
                return false
            }
        }
        return true
    }

    private fun isWinnerInColumn(colNum: Int): Boolean {
        val colOfTiles = yxGrid.map { row ->
            row[colNum]
        }
        for(tile in colOfTiles) {
            if(!tile.marked) {
                return false
            }
        }
        return true
    }


    fun markAndCheckForWin(bingoNum: Int): Boolean {
        for(colNum in 0..4) {
            for(rowNum in 0..4) {
                if(bingoNum == yxGrid[rowNum][colNum].bingoNum) {
                    yxGrid[rowNum][colNum].marked = true
                    isWinner = isWinnerInRow(rowNum) or isWinnerInColumn(colNum)
                    return isWinner
                }
            }
        }
        return false
    }

    fun isWinner(): Boolean {
        return isWinner
    }

    fun getScore(winningNumber: Int): Int {
        if(!isWinner) {
            return 0
        }

        return yxGrid.flatMap { row ->
            row.map { tile ->
                if(!tile.marked) tile.bingoNum else 0
            }
        }.sum() * winningNumber
    }

    override
    fun toString(): String {
        val name = "boardId=$boardId"
        val gridString = yxGrid.map { row ->
            row.map { tile ->
                val tf = if(tile.marked) "t" else "f"
                "${tile.bingoNum}${tf}"
            }.joinToString(separator = ",")
        }.joinToString(separator = "\n")

        return "$name\n$gridString"
    }
}

object Puzzle04 {
    fun run() {
        val input04 = File("${Main.aocRoot}/other/04/input04").readText().trim()
        val input04Chunks = input04.split("\n\n")

        val bingoSequence: List<Int> = input04Chunks[0].split(",").map { Integer.parseInt(it, 10) }
        val boards: List<Board> = input04Chunks.drop(1).mapIndexed { i, chunk ->
            val lines = chunk.split("\n")
            Board(i, lines)
        }

        var boardsStillInPlay = boards.size

        for (bingoNum in bingoSequence) {
            println("marking $bingoNum...")

            val remainingBoards = boards.filter { !it.isWinner() }
            for (board in remainingBoards) {
                if (board.markAndCheckForWin(bingoNum)) {
                    boardsStillInPlay -= 1

                    println("winner:\n$board")
                    println("$boardsStillInPlay boards remain")

                    //part 1
                    // To guarantee victory against the giant squid, figure out which board will win first.
                    // What will your final score be if you choose that board?
                    if (boardsStillInPlay == boards.size - 1) {
                        val score = board.getScore(winningNumber = bingoNum)
                        println("that was the FIRST winning board!")
                        println("(p1 answer) score = $score") // 23177
                    }

                    //part 2
                    // Figure out which board will win last.
                    // Once it wins, what would its final score be?
                    if (boardsStillInPlay == 0) {
                        val score = board.getScore(winningNumber = bingoNum)
                        println("that was the LAST winning board!")
                        println("(p2 answer) score = $score") // 6804
                    }
                }
            }
        }

        println("all numbers played")
    }
}