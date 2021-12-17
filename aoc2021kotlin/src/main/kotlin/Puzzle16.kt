package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

const val LITERAL_TYPE = 4

const val LENGTH_TYPE = 0
const val COUNT_TYPE = 1

abstract class Packet(version: Int, typeID: Int) {
    abstract fun versionSum(): Int
}

data class LiteralPacket(
    val version: Int,
    val literalValue: Long,
): Packet(version, LITERAL_TYPE) {
    override
    fun versionSum(): Int {
        return version
    }
}

data class OperatorPacket(
    val version: Int,
    val typeID: Int,
    //val lengthTypeID: Int,
    val subPackets: List<Packet>
): Packet(version, typeID) {
    init {
        assert(typeID != LITERAL_TYPE)
    }

    override
    fun versionSum(): Int {
        return version + subPackets.sumOf { it.versionSum() }
    }
}

object Puzzle16 {
    fun run() {
        val input16 = File("${Main.aocRoot}/other/16/input16").readText().trim()
        val inputBits = inputHexToBinary(input16)

        println("Part 1")
        val (bitsConsumed, packets) = parseBits(inputBits)
        println("sum of packet versions = ${packets.first().versionSum()}") //
    }

    val HEX_TO_BIN = mapOf(
        '0' to listOf('0', '0', '0', '0'),
        '1' to listOf('0', '0', '0', '1'),
        '2' to listOf('0', '0', '1', '0'),
        '3' to listOf('0', '0', '1', '1'),
        '4' to listOf('0', '1', '0', '0'),
        '5' to listOf('0', '1', '0', '1'),
        '6' to listOf('0', '1', '1', '0'),
        '7' to listOf('0', '1', '1', '1'),
        '8' to listOf('1', '0', '0', '0'),
        '9' to listOf('1', '0', '0', '1'),
        'A' to listOf('1', '0', '1', '0'),
        'B' to listOf('1', '0', '1', '1'),
        'C' to listOf('1', '1', '0', '0'),
        'D' to listOf('1', '1', '0', '1'),
        'E' to listOf('1', '1', '1', '0'),
        'F' to listOf('1', '1', '1', '1'),
    )

    fun inputHexToBinary(inputString: String): List<Char> {
        return inputString.toCharArray().flatMap {
            HEX_TO_BIN[it]!!
        }
    }

    fun binaryToInt(inputBits: List<Char>): Int {
        return inputBits.joinToString(separator = "").toInt(2)
    }

    fun binaryToLong(inputBits: List<Char>): Long {
        return inputBits.joinToString(separator = "").toLong(2)
    }

    fun parseBits(inputBits: List<Char>, maxPackets: Int = Int.MAX_VALUE): Pair<Int, List<Packet>> { // (bitsConsumed, packets)
        val out = mutableListOf<Packet>()

        var idx = 0
        var packetsConsumed = 0
        while(idx+3 < inputBits.size && packetsConsumed < maxPackets) {
            val version = binaryToInt(inputBits.slice(idx until idx+3))
            idx += 3

            if(idx+3 > inputBits.size) {
                val bitsRemaining = inputBits.drop(idx).count()
                val zeroBitsRemaining = inputBits.drop(idx).count { it == '0' }
                println("exiting after version, $zeroBitsRemaining of $bitsRemaining remaining are zero")
                break
            }

            val typeID = binaryToInt(inputBits.slice(idx until idx+3))
            idx += 3

            if((typeID == LITERAL_TYPE && idx+5 > inputBits.size) ||    // literal packets are at least 5 bits after the version and type
                (typeID != LITERAL_TYPE && idx+11 > inputBits.size)) {  // operator packets are at least 11 bits after the version and type
                val bitsRemaining = inputBits.drop(idx).count()
                val zeroBitsRemaining = inputBits.drop(idx).count { it == '0' }
                println("exiting after typeID, $zeroBitsRemaining of $bitsRemaining remaining are zero")
                break
            }

            if(typeID == LITERAL_TYPE) {
                var numberBits = mutableListOf<Char>()
                var keepReading = true
                while(keepReading) {
                    val continuationBit = inputBits[idx]
                    val fourNumberBits = inputBits.slice(idx+1 until idx+5)
                    idx += 5

                    if(continuationBit == '0') keepReading = false

                    numberBits.addAll(fourNumberBits)
                }
                val literalValue = binaryToLong(numberBits)
                val packet = LiteralPacket(version, literalValue)
                out.add(packet)
                packetsConsumed++
            }
            else { // an operator packet
                val lengthTypeID = inputBits[idx].digitToInt() // length type is only 1 bit
                idx += 1

                if(lengthTypeID == LENGTH_TYPE) {
                    val subPacketLength = binaryToInt(inputBits.slice(idx until idx+15))
                    idx += 15

                    val (bitsConsumed, subPackets) = parseBits(inputBits.slice(idx until idx+subPacketLength))
                    assert(bitsConsumed == subPacketLength)
                    idx += subPacketLength

                    val packet = OperatorPacket(version, typeID, subPackets)
                    out.add(packet)
                    packetsConsumed++
                } else if(lengthTypeID == COUNT_TYPE) {
                    val subPacketCount = binaryToInt(inputBits.slice(idx until idx+11))
                    idx += 11

                    val (bitsConsumed, subPackets) = parseBits(inputBits.drop(idx), subPacketCount)
                    idx += bitsConsumed

                    val packet = OperatorPacket(version, typeID, subPackets)
                    out.add(packet)
                    packetsConsumed++
                } else {
                    throw Exception("unknown length type $lengthTypeID")
                }
            }
        }

        return Pair(idx, out)
    }

    /******************************************************************************************************************/

    class Puzzle16Test {
        @Test
        fun `hex to binary`() {
            assertEquals("110100101111111000101000".toCharArray().toList(), inputHexToBinary("D2FE28"))
            assertEquals("00111000000000000110111101000101001010010001001000000000".toCharArray().toList(), inputHexToBinary("38006F45291200"))
            assertEquals("11101110000000001101010000001100100000100011000001100000".toCharArray().toList(), inputHexToBinary("EE00D40C823060"))
        }

        @Test
        fun `literal packet first example`() {
            //val inputBits = "110100101111111000101".toCharArray().toList() // trailing three zeros omitted
            val inputBits = inputHexToBinary("D2FE28")
            val expected = LiteralPacket(6, 2021)
            val (bitsConsumed, packets) = parseBits(inputBits)
            assertEquals(expected, packets.first())
        }

        @Test
        fun `operator packet first example`() {
            //val inputBits = "0011100000000000011011110100010100101001000100100".toCharArray().toList() // trailing seven zeros omitted
            val inputBits = inputHexToBinary("38006F45291200")
            val expected = OperatorPacket(1, 6, listOf(
                LiteralPacket(6, 10),
                LiteralPacket(2, 20),
            ))
            val (bitsConsumed, packets) = parseBits(inputBits)
            assertEquals(expected, packets.first())
        }

        @Test
        fun `operator packet second example`() {
            //val inputBits = "111011100000000011010100000011001000001000110000011".toCharArray().toList() // trailing five zeros omitted
            val inputBits = inputHexToBinary("EE00D40C823060")
            val expected = OperatorPacket(7, 3, listOf(
                LiteralPacket(2, 1),
                LiteralPacket(4, 2),
                LiteralPacket(1, 3),
            ))
            val (bitsConsumed, packets) = parseBits(inputBits)
            assertEquals(expected, packets.first())
        }

        @Test
        fun `operator packet extra example 1`() {
            //8A004A801A8002F478 represents an operator packet (version 4) which contains
            // an operator packet (version 1) which contains
            // an operator packet (version 5) which contains
            // a literal value (version 6); this packet has a version sum of 16.
            val inputBits = inputHexToBinary("8A004A801A8002F478")
            val expected = OperatorPacket(4, 2, listOf(
                OperatorPacket(1, 2, listOf(
                    OperatorPacket(5, 2, listOf(
                        LiteralPacket(6, 15)
                    ))
                ))
            ))
            val (bitsConsumed, packets) = parseBits(inputBits)
            assertEquals(expected, packets.first())
            assertEquals(16, packets.first().versionSum())
        }

        @Test
        fun `operator packet extra example 2`() {
            //620080001611562C8802118E34 represents an operator packet (version 3) which contains
            // two sub-packets; each sub-packet is an operator packet that contains
            // two literal values. This packet has a version sum of 12.
            val inputBits = inputHexToBinary("620080001611562C8802118E34")
            val expected = OperatorPacket(3, 0, listOf(
                OperatorPacket(0, 0, listOf(
                    LiteralPacket(0, 10),
                    LiteralPacket(5, 11),
                )),
                OperatorPacket(1, 0, listOf(
                    LiteralPacket(0,12),
                    LiteralPacket(3,13),
                )),
            ))
            val (bitsConsumed, packets) = parseBits(inputBits)
            assertEquals(expected, packets.first())
            assertEquals(12, packets.first().versionSum())
        }

        @Test
        fun `operator packet extra example 3`() {
            //C0015000016115A2E0802F182340 has the same structure as the previous example,
            // but the outermost packet uses a different length type ID.
            // This packet has a version sum of 23.
            val inputBits = inputHexToBinary("C0015000016115A2E0802F182340")
            val (bitsConsumed, packets) = parseBits(inputBits)
            val outerPacket = packets.first()
            assertTrue(outerPacket is OperatorPacket)
            if(outerPacket is OperatorPacket) {
                assertTrue(outerPacket.subPackets[0] is OperatorPacket)
                assertTrue(outerPacket.subPackets[1] is OperatorPacket)
            }
            assertEquals(23, outerPacket.versionSum())
        }

        @Test
        fun `operator packet extra example 4`() {
            //A0016C880162017C3686B18A3D4780 is an operator packet that contains
            // an operator packet that contains
            // an operator packet that contains
            // five literal values; it has a version sum of 31.
            val inputBits = inputHexToBinary("A0016C880162017C3686B18A3D4780")
            val (bitsConsumed, packets) = parseBits(inputBits)
            val outerPacket = packets.first()
            assertTrue(outerPacket is OperatorPacket)
            if(outerPacket is OperatorPacket) {
                val innerPacket1 = outerPacket.subPackets[0]
                assertTrue(innerPacket1 is OperatorPacket)
                if(innerPacket1 is OperatorPacket) {
                    val innerPacket2 = innerPacket1.subPackets[0]
                    assertTrue(innerPacket2 is OperatorPacket)
                    if(innerPacket2 is OperatorPacket) {
                        assertTrue(innerPacket2.subPackets[0] is LiteralPacket)
                        assertTrue(innerPacket2.subPackets[1] is LiteralPacket)
                        assertTrue(innerPacket2.subPackets[2] is LiteralPacket)
                        assertTrue(innerPacket2.subPackets[3] is LiteralPacket)
                        assertTrue(innerPacket2.subPackets[4] is LiteralPacket)
                    }
                }
            }
            assertEquals(31, outerPacket.versionSum())
        }
    }
}