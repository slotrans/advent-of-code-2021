package net.blergh.advent2021

import kotlin.system.exitProcess

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            when(args[0]) {
                "01" -> Puzzle01.run()
                "02" -> Puzzle02.run()
                "03" -> Puzzle03.run()
                else -> {
                    System.err.println("please specify a puzzle solution to run")
                    exitProcess(1)
                }
            }
        }
    }
}

