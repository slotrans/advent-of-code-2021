package net.blergh.advent2021

import java.io.File

data class Tile(
    val bingoNum: Int,
    var marked: Boolean,
)

class Board(
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

    // didn't need this
    fun isWinner(): Boolean {
        for(i in 0..4) {
            if(isWinnerInRow(i)) {
                return true
            }
        }
        for(i in 0..4) {
            if(isWinnerInColumn(i)) {
                return true
            }
        }
        return false
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
        return yxGrid.map { row ->
            row.map { tile ->
                val tf = if(tile.marked) "t" else "f"
                "${tile.bingoNum}${tf}"
            }.joinToString(separator = ",")
        }.joinToString(separator = "\n")
    }
}

object Puzzle04 {
    fun run() {
        val input04 = File("${Main.aocRoot}/other/04/input04").readText().trim()
        val input04Chunks = input04.split("\n\n")

        val bingoSequence: List<Int> = input04Chunks[0].split(",").map { Integer.parseInt(it, 10) }
        val boards: List<Board> = input04Chunks.drop(1).map {
            val lines = it.split("\n")
            Board(lines)
        }

        part1(boards, bingoSequence)
    }

    private fun part1(boards: List<Board>, bingoSequence: List<Int>) {
        // To guarantee victory against the giant squid, figure out which board will win first.
        // What will your final score be if you choose that board?

        for(bingoNum in bingoSequence) {
            println("marking $bingoNum...")

            for(board in boards) {
                if(board.markAndCheckForWin(bingoNum)) {
                    val score = board.getScore(winningNumber = bingoNum)
                    println("winning board:\n$board")
                    println("(p1 answer) score = $score") // 23177
                    return
                }
            }
        }
    }
}