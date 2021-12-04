package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.math.exp

object Puzzle03 {
    fun run() {
        val input03 = File("${Main.aocRoot}/other/03/input03").readText().trim()
        val bitStrings = input03.split("\n");

        part1(bitStrings)
        part2(bitStrings)
    }

    /* unused
    private fun _mostCommonBit(bitStrings: List<String>, position: Int): Int {
        var zeroCount = 0;
        var oneCount = 0;
        bitStrings.map {
            if(it[position] == '1') oneCount++ else zeroCount++
        }

        val comp = oneCount.compareTo(zeroCount)
        return when {
            (comp > 0) -> 1
            (comp < 0) -> 0
            else -> 1 // "If 0 and 1 are equally common, keep values with a 1 in the position being considered."
        }
    }
    */

    private fun getBitFrequency(bitStrings: List<String>): Array<Int> {
        // we can safely assume each bitString is the same length
        val bitFreqArray = Array<Int>(bitStrings.first().length) { _ -> 0 }

        bitStrings.forEach {
            it.mapIndexed { i, bitVal ->
                if(bitVal == '1') {
                    bitFreqArray[i] += 1
                }
            }
        }

        return bitFreqArray
    }

    private fun mostCommonBits(bitStrings: List<String>): Array<Char> {
        val bitFreqArray = getBitFrequency(bitStrings)
        val mostCommonBits = Array<Char>(bitFreqArray.size) { _ -> '0' }

        bitFreqArray.mapIndexed { i, freq ->
            if(freq.toDouble() / bitStrings.size >= 0.5) {
                mostCommonBits[i] = '1'
            }
        }

        return mostCommonBits
    }

    private fun invertBitArray(bitArray: Array<Char>): Array<Char> {
        val inverted = bitArray.clone()
        for(i in inverted.indices) {
            inverted[i] = if(inverted[i] == '1') '0' else '1'
        }
        return inverted
    }

    private fun bitArrayToInt(bitArray: Array<Char>): Int {
        return Integer.parseInt(bitArray.joinToString(separator = ""), 2)
    }

    private fun part1(bitStrings: List<String>) {
        val gammaBits = mostCommonBits(bitStrings)
        val epsilonBits = invertBitArray(gammaBits)

        println("gammaBits: $gammaBits")
        println("epsilonBits: $epsilonBits")

        val gammaRate = bitArrayToInt(gammaBits)
        val epsilonRate = bitArrayToInt(epsilonBits)

        println("gammaRate=$gammaRate") // 199
        println("epsilonRate=$epsilonRate") // 3896
        val powerConsumption = gammaRate * epsilonRate;
        println("(p1 answer) power consumption = $powerConsumption") // 775304
    }

    private fun getOxygenGeneratorRating(bitStrings: List<String>): Int {
        var tempBitStrings = bitStrings.toList()
        for(i in tempBitStrings.first().indices) {
            val mostCommonBits = mostCommonBits(tempBitStrings)
            tempBitStrings = tempBitStrings.filter {
                it[i] == mostCommonBits[i]
            }
            if(tempBitStrings.size == 1) {
                break
            }
        }
        val oxygenBitArray = tempBitStrings.first().toCharArray().toTypedArray()
        return bitArrayToInt(oxygenBitArray)
    }

    private fun getCO2ScrubberRating(bitStrings: List<String>): Int {
        var tempBitStrings = bitStrings.toList()
        for(i in tempBitStrings.first().indices) {
            val leastCommonBits = invertBitArray(mostCommonBits(tempBitStrings))
            tempBitStrings = tempBitStrings.filter {
                it[i] == leastCommonBits[i]
            }
            if(tempBitStrings.size == 1) {
                break
            }
        }
        val CO2ScrubberBitArray = tempBitStrings.first().toCharArray().toTypedArray()
        return bitArrayToInt(CO2ScrubberBitArray)
    }

    private fun part2(bitStrings: List<String>) {
        val oxygenGeneratorRating = getOxygenGeneratorRating(bitStrings)
        val co2ScrubberRating = getCO2ScrubberRating(bitStrings)
        val lifeSupportRating = oxygenGeneratorRating * co2ScrubberRating

        println("oxygen generator rating = $oxygenGeneratorRating") // 509
        println("CO2 scrubber rating = $co2ScrubberRating") // 2693
        println("(p2 answer) life support rating = $lifeSupportRating") // 1370737
    }


    class Puzzle03Test {
        val sampleInput = """
        00100
        11110
        10110
        10111
        10101
        01111
        00111
        11100
        10000
        11001
        00010
        01010
    """.trimIndent()
        val sampleBitStrings = sampleInput.split("\n")
        val sampleMostCommonBits = arrayOf('1', '0', '1', '1', '0')
        val sampleLeastCommonBits = arrayOf('0', '1', '0', '0', '1')

        @Test
        fun `bit frequency in sample input`() {
            val bitFrequency = getBitFrequency(sampleBitStrings)
            val expected = arrayOf(7, 5, 8, 7, 5)
            assertArrayEquals(bitFrequency, expected)
        }

        @Test
        fun `most common bits in sample input`() {
            val mostCommonBits = mostCommonBits(sampleBitStrings)
            assertArrayEquals(mostCommonBits, sampleMostCommonBits)
        }

        @Test
        fun `least common bits in sample input`() {
            val leastCommonBits = invertBitArray(mostCommonBits(sampleBitStrings))
            assertArrayEquals(leastCommonBits, sampleLeastCommonBits)
        }

        @Test
        fun `1st sample bit array to int`() {
            val intFromBits = bitArrayToInt(sampleMostCommonBits)
            val expected = 22
            assertEquals(intFromBits, expected)
        }

        @Test
        fun `2nd sample bit array to int`() {
            val intFromBits = bitArrayToInt(sampleLeastCommonBits)
            val expected = 9
            assertEquals(intFromBits, expected)
        }

        @Test
        fun `oxygen generator rating for sample input`() {
            val oxygen = getOxygenGeneratorRating(sampleBitStrings)
            val expected = 23
            assertEquals(oxygen, expected)
        }

        @Test
        fun `co2 scrubber rating for sample input`() {
            val scrubber = getCO2ScrubberRating(sampleBitStrings)
            val expected = 10
            assertEquals(scrubber, expected)
        }
    }
}

