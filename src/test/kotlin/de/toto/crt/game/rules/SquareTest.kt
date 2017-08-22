package de.toto.crt.game.rules

import de.toto.crt.game.rules.Piece
import de.toto.crt.game.rules.Piece.*
import de.toto.crt.game.rules.Square
import org.junit.Assert.*
import org.junit.Test

class SquareTest {

    @Test
    fun isWhite() {
        assertFalse(Square(1, 1).isWhite)
        assertTrue(Square(1, 8).isWhite)
        assertTrue(Square(8, 1).isWhite)
        assertFalse(Square(8, 8).isWhite)
    }

    @Test
    fun name() {
        assertEquals(Square(1,1).name, "a1")
        assertEquals(Square(1,8).name, "h1")
        assertEquals(Square(3,3).name, "c3")
        assertEquals(Square(8,1).name, "a8")
        assertEquals(Square(8,8).name, "h8")

        assertNotEquals(Square(8,8).name, "H8")
        assertNotEquals(Square(8,8).name, " h8")
        assertNotEquals(Square(8,8).name, "h 8")
    }

    @Test
    fun nameWithPiecePrefix() {
        assertEquals(Square(1, 1, WHITE_ROOK).nameWithPiecePrefix, "Ra1")
        assertEquals(Square(3, 1).nameWithPiecePrefix, "a3")
    }

    @Test
    fun nameWithPieceFigurine() {
        assertEquals(Square(1, 1, BLACK_ROOK).nameWithPieceFigurine, "♜a1")
        assertEquals(Square(3, 1).nameWithPieceFigurine, "a3")
    }

    @Test
    fun isEmpty() {
        assertFalse(Square(1, 1, BLACK_ROOK).isEmpty)
        assertTrue(Square(3, 1).isEmpty)
    }

    @Test
    fun equals() {
        assertEquals(Square(1, 1), Square(1, 1))
        assertEquals(Square(1, 1, WHITE_BISHOP), Square(1, 1, WHITE_BISHOP))
    }

    @Test
    fun notEquals() {
        assertNotEquals(Square(1, 1), null)
        assertNotEquals(Square(1, 1, WHITE_BISHOP), Square(1, 1, BLACK_BISHOP))
        assertNotEquals(Square(1, 1, WHITE_BISHOP), Square(1, 2, WHITE_BISHOP))
    }


    @Test
    fun hashCodes() {
        assertEquals(Square(1, 1).hashCode(), Square(1, 1).hashCode())
        assertEquals(Square(1, 1, WHITE_BISHOP).hashCode(), Square(1, 1, WHITE_BISHOP).hashCode())

        assertNotEquals(Square(1, 1, WHITE_BISHOP).hashCode(), Square(1, 1, BLACK_BISHOP).hashCode())
        assertNotEquals(Square(1, 1, WHITE_BISHOP).hashCode(), Square(1, 2, WHITE_BISHOP).hashCode())
    }

}

private fun Square(rank: Int, file: Int, piece: Piece) = (Square(rank, file)).apply { this.piece = piece }