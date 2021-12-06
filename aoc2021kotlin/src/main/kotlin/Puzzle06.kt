package net.blergh.advent2021

import java.io.File

class FishSimulation(private val initialState: List<Int>) {
    val timers = initialState.toMutableList()

    fun step() {
        val newFish = mutableListOf<Int>()
        for(i in 0 until timers.size) {
            if(timers[i] == 0) {
                newFish.add(8)
                timers[i] = 6
            }
            else {
                timers[i] -= 1
            }
        }
        timers.addAll(newFish)
    }
}

object Puzzle06 {
    fun run() {
        val input06 = File("${Main.aocRoot}/other/06/input06").readText().trim()
        val initialState = input06.split(",").map { it.toInt() }

        //samplePart1()
        part1(initialState)
    }

    private fun part1(initialState: List<Int>) {
        println("Part 1")

        val fishSim = FishSimulation(initialState)
        for(day in 1..80) {
            fishSim.step()
            println("simulation day $day, fish count: ${fishSim.timers.size}")
        }
        // 362740
    }

    private fun samplePart1() {
        println("Part 1 SAMPLE")

        val initialState = SAMPLE_INPUT.split(",").map { it.toInt() }
        val fishSim = FishSimulation(initialState)

        println(initialState)
        for(i in 1..18) {
            fishSim.step()
            println(fishSim.timers)
        }
        println("fish count after 18 days: ${fishSim.timers.size}")
        assert(fishSim.timers.size == 26)

        for(i in 19..80) {
            fishSim.step()
        }
        println("fish count after 80 days: ${fishSim.timers.size}")
        assert(fishSim.timers.size == 5934)
    }

    private const val SAMPLE_INPUT = "3,4,3,1,2"
}