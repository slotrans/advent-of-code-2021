package net.blergh.advent2021

import kotlin.system.exitProcess

class Main {
    companion object {
        val aocRoot: String = System.getenv("AOC2021_ROOT")

        @JvmStatic
        fun main(args: Array<String>) {
            when(args[0]) {
                "01" -> Puzzle01.run()
                "02" -> Puzzle02.run()
                "03" -> Puzzle03.run()
                "04" -> Puzzle04.run()
                else -> {
                    System.err.println("please specify a puzzle solution to run")
                    exitProcess(1)
                }
            }
        }
    }
}

