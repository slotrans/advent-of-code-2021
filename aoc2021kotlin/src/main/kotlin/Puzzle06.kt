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

class ExtendedFishSimulation(private val initialState: List<Int>) {
    private var timerMap: Map<Int, Long> = initialState.groupingBy { it }
        .eachCount()
        .entries
        .associate { it.key to it.value.toLong() } // boy this is a mess... sure would be nice if you could assign an Int into a Long

    fun fishCount(): Long {
        return timerMap.values.sumOf { it } // this awkward form is required because .sum() returns Int
    }

    fun step() {
        val newTimerMap: MutableMap<Int, Long> = (0..8).associateWith { 0L }.toMutableMap()

        // body of the loop *relies* on this ordering
        for(timerValue in 8 downTo 0) {
            val fishCount = timerMap.getOrDefault(timerValue, 0)
            if(timerValue == 0) {
                newTimerMap[8] = fishCount
                newTimerMap[6] = newTimerMap[6]!! + fishCount
                /* BTW:
                    newTimerMap[6] += fishCount  doesn't compile, suggested fixes are:
                    newTimerMap[6] = newTimerMap[6]?.plus(fishCount)  which also doesn't compile, error: "Type mismatch: inferred type is Int? but TypeVariable(V) was expected"
                    newTimerMap[6]!! += fishCount  which also doesn't compile, error: "Variable expected"
                 */
            }
            else {
                newTimerMap[timerValue-1] = fishCount
            }
        }

        timerMap = newTimerMap
    }
}

object Puzzle06 {
    fun run() {
        val input06 = File("${Main.aocRoot}/other/06/input06").readText().trim()
        val initialState = input06.split(",").map { it.toInt() }

        //samplePart1()
        part1(initialState)
        //samplePart2()
        part2(initialState)
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

    private fun part2(initialState: List<Int>) {
        println("Part 2")

        val fishSim = ExtendedFishSimulation(initialState)
        for(day in 1..256) {
            fishSim.step()
            println("simulation day $day, fish count: ${fishSim.fishCount()}")
        }
        // 1,644,874,076,764
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

    private fun samplePart2() {
        println("Part 2 SAMPLE")

        val initialState = SAMPLE_INPUT.split(",").map { it.toInt() }
        val fishSim = ExtendedFishSimulation(initialState)

        for(i in 1..18) {
            fishSim.step()
        }
        val after18 = fishSim.fishCount()
        println("fish count after 18 days: $after18")
        assert(after18 == 26L)

        for(i in 19..80) {
            fishSim.step()
        }
        val after80 = fishSim.fishCount()
        println("fish count after 80 days: $after80")
        assert(after80 == 5934L)

        for(i in 81..256) {
            fishSim.step()
        }
        val after256 = fishSim.fishCount()
        println("fish count after 256 days: $after256")
        assert(after256 == 26984457539L)
    }

    private const val SAMPLE_INPUT = "3,4,3,1,2"
}