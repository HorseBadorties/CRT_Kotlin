package de.toto.crt.game

import org.junit.Assert.*
import org.junit.Test

class SquareTest {

    fun failConstructor(rank: Int, file: Int) {
        try {
            Square(rank, file)
            fail("IllegalArgumentException expected")
        } catch(e: IllegalArgumentException) {}
    }

    @Test
    fun failConstructor() {
        failConstructor(1,0)
        failConstructor(1,9)
        failConstructor(1,10)
        failConstructor(0,1)
        failConstructor(9,1)
        failConstructor(10,1)
    }

    @Test
    fun passConstructor() {
        assertTrue(Square(1,1).name == "a1")
        assertTrue(Square(1,8).name == "h1")
        assertTrue(Square(8,1).name == "a8")
        assertTrue(Square(8,8).name == "h8")
    }

    @Test
    fun passFromName() {
        assertTrue(Square.fromName("a1").file == 1)
        assertTrue(Square.fromName("b1").file == 2)
        assertTrue(Square.fromName("c1").file == 3)
        assertTrue(Square.fromName("d1").file == 4)
        assertTrue(Square.fromName("e1").file == 5)
        assertTrue(Square.fromName("f1").file == 6)
        assertTrue(Square.fromName("g1").file == 7)
        assertTrue(Square.fromName("h1").file == 8)
    }

    fun failFromName(name: String) {
        try {
            Square.fromName(name)
            fail("IllegalArgumentException expected")
        } catch(e: IllegalArgumentException) {}
    }

    @Test
    fun allFailFromName() {
        failFromName("a0")
        failFromName("a9")
        failFromName("a10")
        failFromName(" a1")
        failFromName("a1 ")
        failFromName("A1")
        failFromName("i1")
        failFromName("a")
        failFromName("1")
        failFromName("1a")
    }

    @Test
    fun isWhite() {
        assertFalse(Square.fromName("a1").isWhite)
        assertTrue(Square.fromName("a8").isWhite)
        assertTrue(Square.fromName("h1").isWhite)
        assertFalse(Square.fromName("h8").isWhite)
    }

    @Test
    fun name() {
        assertTrue(Square(1,1).name == "a1")
        assertTrue(Square(1,8).name == "h1")
        assertTrue(Square(3,3).name == "c3")
        assertTrue(Square(8,1).name == "a8")
        assertTrue(Square(8,8).name == "h8")

        assertFalse(Square(8,8).name == "H8")
        assertFalse(Square(8,8).name == " h8")
        assertFalse(Square(8,8).name == "h 8")
    }

    @Test
    fun nameWithPiecePrefix() {
        assertTrue(Square.fromName("a1", Piece.WHITE_ROOK).nameWithPiecePrefix == "Ra1")
        assertTrue(Square.fromName("a3").nameWithPiecePrefix == "a3")
    }

    @Test
    fun nameWithPieceFigurine() {
        assertTrue(Square.fromName("a1", Piece.WHITE_ROOK).nameWithPieceFigurine == "♜a1")
        assertTrue(Square.fromName("a3").nameWithPieceFigurine == "a3")
    }

    @Test
    fun isEmpty() {
        assertFalse(Square.fromName("a1", Piece.WHITE_ROOK).isEmpty)
        assertTrue(Square.fromName("a1", null).isEmpty)
    }

    @Test
    fun equals() {
        assertTrue(Square(1, 1) == Square.fromName("a1"))
        assertTrue(Square.fromName("a1") == Square.fromName("a1"))
        assertTrue(Square(1, 2) != Square.fromName("a1"))
        assertTrue(Square.fromName("a1") != Square.fromName("b1"))
        assertFalse(Square.fromName("a1").equals(null))
    }

    @Test
    fun testHashCode() {
        assertTrue(Square.fromName("a1").hashCode() == Square.fromName("a1").hashCode())
        assertTrue(Square.fromName("a2").hashCode() != Square.fromName("a1").hashCode())
        assertTrue(Square.fromName("h1").hashCode() != Square.fromName("a1").hashCode())
    }

}