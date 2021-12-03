package net.blergh.advent2021

import java.io.File

object Puzzle01 {
    fun part1(measurements: List<Int>) {
        var increases = 0
        var lastDepth = measurements.first()

        for (depth in measurements.drop(1)) {
            if (depth > lastDepth) {
                increases++
            }
            lastDepth = depth
        }

        println("(p1 answer) increases = $increases") //answer: 1548
    }

    fun part2(measurements: List<Int>) {
        var increases = 0
        var lastWindow = measurements.take(3)

        for (i in 1..(measurements.size - 3)) {
            val window = measurements.slice(i..i + 2)
            if (window.sum() > lastWindow.sum()) {
                increases++
            }
            lastWindow = window
        }

        println("(p2 answer) increases = $increases") //answer: 1589
    }

    fun run() {
        val input01 = File("${Main.aocRoot}/other/01/input01").readText().trim()
        val measurements = input01.split("\n").map(Integer::parseInt)

        part1(measurements)
        part2(measurements)
    }
}