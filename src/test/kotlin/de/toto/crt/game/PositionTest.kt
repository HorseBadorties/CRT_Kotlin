package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class PositionTest {

    private val LOOP_COUNT = 1 //1_000_000

    @Test
    fun square() {
        assertTrue(Position().square(1, 1).name == "a1")
        assertTrue(Position().square(1, 8).name == "h1")
        assertTrue(Position().square(2, 1).name == "a2")
        assertTrue(Position().square(3, 8).name == "h3")
    }

    fun doFailSquare(rank: Int, file: Int) {
        try {
            Position().square(rank, file)
            fail("IllegalArgumentException expected")
        } catch (e: IllegalArgumentException) {
        }
    }

    fun doFailSquare(name: String) {
        try {
            Position().square(name)
            fail("IllegalArgumentException expected")
        } catch (e: IllegalArgumentException) {
        }
    }

    @Test
    fun failSquares() {
        doFailSquare(0, 8)
        doFailSquare(0, -1)
        doFailSquare(9, 9)
        doFailSquare("A1")
        doFailSquare("a0")
        doFailSquare("i1")
    }

}
