package net.blergh.advent2021

import java.io.File

enum class Direction {
    FORWARD, UP, DOWN
}

data class Instruction(
    val direction: Direction,
    val value: Int
)

object Puzzle02 {
    fun run() {
        val input02 = File("//wsl\$/Debian/home/yettern/apps/advent-of-code-2021/02/input02").readText().trim()
        val instructions = input02.split("\n").map {
            val (directionString, valueString) = it.split(" ")
            Instruction(
                direction=Direction.valueOf(directionString.uppercase()),
                value=Integer.parseInt(valueString)
            )
        }

        part1(instructions)
        part2(instructions)
    }

    private fun part1(instructions: List<Instruction>) {
        var position = 0
        var depth = 0
        for(inst in instructions) {
            when(inst.direction) {
                Direction.FORWARD -> position += inst.value
                Direction.UP -> depth -= inst.value
                Direction.DOWN -> depth += inst.value
            }
        }
        println("position = $position, depth = $depth") // 2003, 980
        val p1Answer = position * depth
        println("(p1 answer) position * depth = $p1Answer") // 1962940
    }

    private fun part2(instructions: List<Instruction>) {
        var aim = 0
        var position = 0
        var depth = 0
        for(inst in instructions) {
            when(inst.direction) {
                Direction.DOWN -> aim += inst.value
                Direction.UP -> aim -= inst.value
                Direction.FORWARD -> {
                    position += inst.value
                    depth += aim * inst.value
                }
            }
        }
        println("position = $position, depth = $depth, aim = $aim") // 2003, 905474, 980
        val p2Answer = position * depth
        println("(p2 answer) position * depth = $p2Answer") // 1813664422
    }
}