package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class PositionTest {

    @Test
    fun square() {
        assertTrue(Position().square(0,0).name == "a1")
        assertTrue(Position().square(0,7).name == "h1")
        assertTrue(Position().square(1,0).name == "a2")
        assertTrue(Position().square(2,7).name == "h3")
    }

    fun doFailSquare(rank: Int, file: Int) {
        try {
            Position().square(rank, file)
            fail("IllegalArgumentException expected")
        } catch (e: IllegalArgumentException) {}
    }

    fun doFailSquare(name: String) {
        try {
            Position().square(name)
            fail("IllegalArgumentException expected")
        } catch (e: IllegalArgumentException) {}
    }

    @Test
    fun failSquares() {
        doFailSquare(0,8)
        doFailSquare(0,-1)
        doFailSquare(8,8)
        doFailSquare("A1")
        doFailSquare("a0")
        doFailSquare("i1")

    }

    @Test
    fun kingAttacks() {
        val pos = Position()
        assertTrue(kingAttacks(pos.square("b2"), pos.square("c3")))
        assertTrue(kingAttacks(pos.square("a1"), pos.square("b1")))
        assertTrue(kingAttacks(pos.square("a1"), pos.square("a2")))
        assertFalse(kingAttacks(pos.square("a1"), pos.square("c3")))
        assertFalse(kingAttacks(pos.square("a1"), pos.square("a3")))
        assertFalse(kingAttacks(pos.square("a1"), pos.square("c1")))
    }

    @Test
    fun rookAttacks() {
        val pos = Position()
        pos.square("c1").piece = Piece.WHITE_ROOK
        assertTrue(rookAttacks(pos.square("a1"), pos.square("c1"), pos))
        assertTrue(rookAttacks(pos.square("a1"), pos.square("a8"), pos))
        assertTrue(rookAttacks(pos.square("a1"), pos.square("c1"), pos))
        assertFalse(rookAttacks(pos.square("a1"), pos.square("d1"), pos))
    }


}