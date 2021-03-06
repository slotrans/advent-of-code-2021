package net.blergh.advent2021

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.max
import kotlin.math.pow

typealias Vector2 = Point2

class Probe(private val initialPosition: Point2, private val initialVelocity: Vector2) {
    var position = initialPosition
        private set
    var velocity = initialVelocity
        private set

    fun step() {
        // The probe's x position increases by its x velocity.
        // The probe's y position increases by its y velocity.
        // Due to drag, the probe's x velocity changes by 1 toward the value 0;
        //   that is, it decreases by 1 if it is greater than 0, increases by 1 if it is less than 0, or does not change if it is already 0.
        // Due to gravity, the probe's y velocity decreases by 1.

        position = Point2(position.x + velocity.x, position.y + velocity.y)

        val dx = if(velocity.x > 0) {
            velocity.x - 1
        } else if(velocity.x < 0) {
            velocity.x + 1
        } else { // == 0
            0
        }
        velocity = Vector2(dx, velocity.y - 1)
    }
}

class TargetArea {
    val xRange: IntRange
    val yRange: IntRange

    constructor(inputString: String) {
        val dataPart = inputString.split(": ")[1]
        val (xPart, yPart) = dataPart.split(", ")
        val xRangeStr = xPart.split("=")[1]
        val yRangeStr = yPart.split("=")[1]

        val (x1, x2) = xRangeStr.split("..").map { it.toInt() }
        val (y1, y2) = yRangeStr.split("..").map { it.toInt() }

        xRange = if(x1 < x2) (x1..x2) else (x2..x1)
        yRange = if(y1 < y2) (y1..y2) else (y2..y1)
    }

    fun isPointInside(point: Point2): Boolean {
        return point.x in xRange && point.y in yRange
    }

    // assumes that we're firing at the target area from the left
    fun isPointBeyond(point: Point2): Boolean {
        return point.x > xRange.last && point.y < yRange.first
    }
}

object Puzzle17 {
    fun findMaxHeight(targetArea: TargetArea, xVelocityLimit: Int, yVelocityLimit: Int): Int {
        var globalMaxHeight = Int.MIN_VALUE
        for(xVel in 1..xVelocityLimit) {
            for(yVel in 1..yVelocityLimit) {
                var maxHeight = Int.MIN_VALUE
                print("firing at velocity ($xVel,$yVel)...")
                val probe = Probe(Point2(0,0), Vector2(xVel,yVel))
                //maybe also hard-limit iterations?
                while(true) {
                    probe.step()
                    maxHeight = max(maxHeight, probe.position.y)

                    if(targetArea.isPointInside(probe.position)) {
                        print("hit!")
                        if(maxHeight > globalMaxHeight) {
                            globalMaxHeight = maxHeight
                            print(" new height achieved = $maxHeight")
                        }
                        break
                    }

                    if(targetArea.isPointBeyond(probe.position)) {
                        print("passed the target")
                        break
                    }

                    if(probe.position.y < targetArea.yRange.first && probe.velocity.x == 0) {
                        print("vertical trajectory: fell short or overshot")
                        break
                    }
                }
                println()
            }
        }
        return globalMaxHeight
    }

    fun findAllHittingVelocities(targetArea: TargetArea, xVelocityRange: IntRange, yVelocityRange: IntRange): Set<Vector2> {
        val hittingVelocities = mutableSetOf<Vector2>()

        for(xVel in xVelocityRange) {
            for(yVel in yVelocityRange) {
                print("firing at velocity ($xVel,$yVel)...")
                val probe = Probe(Point2(0,0), Vector2(xVel,yVel))
                while(true) {
                    probe.step()

                    if(targetArea.isPointInside(probe.position)) {
                        print("hit!")
                        hittingVelocities.add(Vector2(xVel, yVel))
                        break
                    }

                    if(targetArea.isPointBeyond(probe.position)) {
                        print("passed the target")
                        break
                    }

                    if(probe.position.y < targetArea.yRange.first && probe.velocity.x == 0) {
                        print("vertical trajectory: fell short or overshot")
                        break
                    }
                }
                println()
            }
        }

        return hittingVelocities
    }

    fun run() {
        val input17 = File("${Main.aocRoot}/other/17/input17").readText().trim()
        val targetArea = TargetArea(input17)

        println("Part 1")
        // velocity ranges are total guesses
        val xVelocityLimit = targetArea.xRange.last / 4
        val yVelocityLimit = targetArea.xRange.last  // not a typo
        val highest = findMaxHeight(targetArea, xVelocityLimit, yVelocityLimit)
        println("(p1 answer) greatest height of any hitting trajectory = $highest") // 4186

        println("Part 2")
        val xVelocityRange = 1..targetArea.xRange.last
        val yVelocityRange = targetArea.yRange.first..(targetArea.xRange.last*2)
        val hittingVelocities = findAllHittingVelocities(targetArea, xVelocityRange, yVelocityRange)
        println("(p2 answer) count of trajectories resulting in hit = ${hittingVelocities.size}") // 2709
    }

    /******************************************************************************************************************/

    class Puzzle17Test {
        val SAMPLE_INPUT = "target area: x=20..30, y=-10..-5"

        @Test
        fun `target area construction`() {
            val targetArea = TargetArea(SAMPLE_INPUT)
            assertEquals((20..30), targetArea.xRange)
            assertEquals((-10..-5), targetArea.yRange)
        }

        @Test
        fun `probe trajectory example 1`() {
            val targetArea = TargetArea(SAMPLE_INPUT)
            val probe = Probe(Point2(0,0), Vector2(7,2))
            val trajectory = listOf(
                Point2(7,2),
                Point2(13,3),
                Point2(18,3),
                Point2(22,2),
                Point2(25,0),
                Point2(27,-3),
                Point2(28,-7),
            )
            for(point in trajectory) {
                probe.step()
                assertEquals(point, probe.position)
            }
            assertTrue(targetArea.isPointInside(probe.position))
        }

        @Test
        fun `probe trajectory example 2`() {
            val targetArea = TargetArea(SAMPLE_INPUT)
            val probe = Probe(Point2(0, 0), Vector2(6, 3))
            val trajectory = listOf(
                Point2(6,3),
                Point2(11,5),
                Point2(15,6),
                Point2(18,6),
                Point2(20,5),
                Point2(21,3),
                Point2(21,0),
                Point2(21, -4),
                Point2(21, -9),
            )
            for(point in trajectory) {
                probe.step()
                assertEquals(point, probe.position)
            }
            assertTrue(targetArea.isPointInside(probe.position))
        }

        @Test
        fun `probe trajectory example 3`() {
            val targetArea = TargetArea(SAMPLE_INPUT)
            val probe = Probe(Point2(0, 0), Vector2(9, 0))
            val trajectory = listOf(
                Point2(9,0),
                Point2(17, -1),
                Point2(24, -3),
                Point2(30, -6),
            )
            for(point in trajectory) {
                probe.step()
                assertEquals(point, probe.position)
            }
            assertTrue(targetArea.isPointInside(probe.position))
        }

        @Test
        fun `probe trajectory example 4 no hit`() {
            val targetArea = TargetArea(SAMPLE_INPUT)
            val probe = Probe(Point2(0, 0), Vector2(17, -4))

            probe.step()
            assertEquals(Point2(17,-4), probe.position)
            assertFalse(targetArea.isPointBeyond(probe.position))

            probe.step()
            assertEquals(Point2(33, -9), probe.position)
            assertFalse(targetArea.isPointBeyond(probe.position))

            probe.step()
            assertEquals(Point2(48, -15), probe.position)
            assertTrue(targetArea.isPointBeyond(probe.position))
        }

        @Test
        fun `max height of a trajectory that hits the sample target`() {
            val targetArea = TargetArea(SAMPLE_INPUT)
            val computed = findMaxHeight(targetArea, 20, 20)
            assertEquals(45, computed)
        }

        @Test
        fun `all hitting velocities for sample`() {
            val targetArea = TargetArea(SAMPLE_INPUT)
            val expected = setOf(
                Vector2(23,-10),
                Vector2(25,-9),
                Vector2(27,-5),
                Vector2(29,-6),
                Vector2(22,-6),
                Vector2(21,-7),
                Vector2(9,0),
                Vector2(27,-7),
                Vector2(24,-5),
                Vector2(25,-7),
                Vector2(26,-6),
                Vector2(25,-5),
                Vector2(6,8),
                Vector2(11,-2),
                Vector2(20,-5),
                Vector2(29,-10),
                Vector2(6,3),
                Vector2(28,-7),
                Vector2(8,0),
                Vector2(30,-6),
                Vector2(29,-8),
                Vector2(20,-10),
                Vector2(6,7),
                Vector2(6,4),
                Vector2(6,1),
                Vector2(14,-4),
                Vector2(21,-6),
                Vector2(26,-10),
                Vector2(7,-1),
                Vector2(7,7),
                Vector2(8,-1),
                Vector2(21,-9),
                Vector2(6,2),
                Vector2(20,-7),
                Vector2(30,-10),
                Vector2(14,-3),
                Vector2(20,-8),
                Vector2(13,-2),
                Vector2(7,3),
                Vector2(28,-8),
                Vector2(29,-9),
                Vector2(15,-3),
                Vector2(22,-5),
                Vector2(26,-8),
                Vector2(25,-8),
                Vector2(25,-6),
                Vector2(15,-4),
                Vector2(9,-2),
                Vector2(15,-2),
                Vector2(12,-2),
                Vector2(28,-9),
                Vector2(12,-3),
                Vector2(24,-6),
                Vector2(23,-7),
                Vector2(25,-10),
                Vector2(7,8),
                Vector2(11,-3),
                Vector2(26,-7),
                Vector2(7,1),
                Vector2(23,-9),
                Vector2(6,0),
                Vector2(22,-10),
                Vector2(27,-6),
                Vector2(8,1),
                Vector2(22,-8),
                Vector2(13,-4),
                Vector2(7,6),
                Vector2(28,-6),
                Vector2(11,-4),
                Vector2(12,-4),
                Vector2(26,-9),
                Vector2(7,4),
                Vector2(24,-10),
                Vector2(23,-8),
                Vector2(30,-8),
                Vector2(7,0),
                Vector2(9,-1),
                Vector2(10,-1),
                Vector2(26,-5),
                Vector2(22,-9),
                Vector2(6,5),
                Vector2(7,5),
                Vector2(23,-6),
                Vector2(28,-10),
                Vector2(10,-2),
                Vector2(11,-1),
                Vector2(20,-9),
                Vector2(14,-2),
                Vector2(29,-7),
                Vector2(13,-3),
                Vector2(23,-5),
                Vector2(24,-8),
                Vector2(27,-9),
                Vector2(30,-7),
                Vector2(28,-5),
                Vector2(21,-10),
                Vector2(7,9),
                Vector2(6,6),
                Vector2(21,-5),
                Vector2(27,-10),
                Vector2(7,2),
                Vector2(30,-9),
                Vector2(21,-8),
                Vector2(22,-7),
                Vector2(24,-9),
                Vector2(20,-6),
                Vector2(6,9),
                Vector2(29,-5),
                Vector2(8,-2),
                Vector2(27,-8),
                Vector2(30,-5),
                Vector2(24,-7),
            ) // keep collapsed because long list!
            val computed = findAllHittingVelocities(targetArea, 1..30, -10..10)
            assertEquals(expected, computed)
        }
    }
}