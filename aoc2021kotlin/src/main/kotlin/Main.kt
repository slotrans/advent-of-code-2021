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
                "05" -> Puzzle05.run()
                "06" -> Puzzle06.run()
                "07" -> Puzzle07.run()
                "08" -> Puzzle08.run()
                "09" -> Puzzle09.run()
                "10" -> Puzzle10.run()
                "11" -> Puzzle11.run()
                "12" -> Puzzle12.run()
                "13" -> Puzzle13.run()
                else -> {
                    System.err.println("please specify a puzzle solution to run")
                    exitProcess(1)
                }
            }
        }
    }
}

