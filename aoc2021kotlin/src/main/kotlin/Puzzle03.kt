package net.blergh.advent2021

import java.io.File

object Puzzle03 {
    fun run() {
        val input03 = File("${Main.aocRoot}/other/03/input03").readText().trim()
        val bitStrings = input03.split("\n");

        part1(bitStrings)
    }

    private fun part1(bitStrings: List<String>) {
        // we can safely assume each bitString is the same length
        val bitFreqArray = Array<Int>(bitStrings.first().length) { _ -> 0 }

        bitStrings.forEach {
            it.mapIndexed { i, bitVal ->
                if(bitVal == '1') {
                    bitFreqArray[i] += 1
                }
            }
        }

        val gammaBits = bitFreqArray.map {
            if(it.toDouble() / bitStrings.size > 0.5) 1 else 0
        }
        val epsilonBits = gammaBits.map {
            if(it == 1) 0 else 1
        }

        val gammaRate = Integer.parseInt(gammaBits.joinToString(separator = ""), 2)
        val epsilonRate = Integer.parseInt(epsilonBits.joinToString(separator = ""), 2)

        println("gammaRate=$gammaRate")
        println("epsilonRate=$epsilonRate")
        val powerConsumption = gammaRate * epsilonRate;
        println("(p1 answer) power consumption = $powerConsumption") // 775304
    }
}