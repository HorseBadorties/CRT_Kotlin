package de.toto.crt.game

import de.toto.crt.game.rules.*
import org.junit.Test

import org.junit.Assert.*

class PositionTest {

    @Test
    fun equal() {
        assertEquals(fromFEN(FEN_EMPTY_BOARD), fromFEN(FEN_EMPTY_BOARD))
        assertEquals(fromFEN(FEN_STARTPOSITION), fromFEN(FEN_STARTPOSITION))
        // equal position after repetition
        assertEquals(
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Qe2"),
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Qe2 Qe7 3.Qd1 Qd8 4.Qe2"))
    }

    @Test
    fun notEqual() {
        assertNotEquals(fromFEN(FEN_EMPTY_BOARD), fromFEN(FEN_STARTPOSITION))
        assertNotEquals(fromFEN(FEN_EMPTY_BOARD), null)
        // different side to move:
        assertNotEquals(
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Qe2"),
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Qf3 Qe7 3.Qe2 Qd8"))
        // different castling rights:
        assertNotEquals(
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Nf3 Nf6 3.Bc4 Bc5"),
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Nf3 Nf6 3.Bc4 Bc5 4.Ke2 Qe7 5.Ke1 Qd8"))
        // different en passant fields
        assertNotEquals(
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 d5 2.Nf3 d4 3.c4"),
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 d5 2.Nf3 d4 3.c4 Qd7 4.Qe2 Qd8 5.Qd1"))
    }


    @Test
    fun testHashCode() {
        assertEquals(fromFEN(FEN_EMPTY_BOARD).hashCode(), fromFEN(FEN_EMPTY_BOARD).hashCode())
        assertEquals(fromFEN(FEN_STARTPOSITION).hashCode(), fromFEN(FEN_STARTPOSITION).hashCode())
        assertNotEquals(fromFEN(FEN_EMPTY_BOARD).hashCode(), fromFEN(FEN_STARTPOSITION).hashCode())
        assertEquals(fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Qe2").hashCode(),
                fromFEN(FEN_STARTPOSITION).createNextFromSANs("1.e4 e5 2.Qe2 Qe7 3.Qd1 Qd8 4.Qe2").hashCode())
    }

    @Test
    fun constructorWithFen() {
        assertNotNull(fromFEN(FEN_EMPTY_BOARD))
        assertNotNull(fromFEN(FEN_STARTPOSITION))
    }

    @Test
    fun failConstructorWithFen() {
        doFailConstructorWithFen("illegal FEN")
        doFailConstructorWithFen("8/8/8/8/8/8/8/8/P7 w KQkq - 0 1")
        doFailConstructorWithFen("PPPPPPPPP/8/8/8/8/8/8/8 w KQkq - 0 1")
    }

    @Test
    fun square() {
        assertEquals(Position().square(1, 1).name, "a1")
        assertEquals(Position().square(1, 8).name, "h1")
        assertEquals(Position().square(2, 1).name, "a2")
        assertEquals(Position().square(3, 8).name, "h3")
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



    private fun doFailSquare(rank: Int, file: Int) {
        try {
            Position().square(rank, file)
            fail("ArrayIndexOutOfBoundsException expected")
        } catch (e: ArrayIndexOutOfBoundsException) { }
    }

    private fun doFailSquare(name: String) {
        try {
            Position().square(name)
            fail("ArrayIndexOutOfBoundsException expected")
        } catch (e: ArrayIndexOutOfBoundsException) { }
    }

    private fun doFailConstructorWithFen(fen: String) {
        try {
            fromFEN(fen)
            fail("IllegalArgumentException expected")
        } catch (e: IllegalArgumentException) {
        }
    }

}

// create a Position using FEN-Notation for Pieces, like "ke8" for "black king on e8 or "Ke1" for "white king on e1"
fun Pos(pieces : String, whiteToMove: Boolean = true): Position {
    with (Position(whiteToMove = whiteToMove)) {
        squares.flatten().forEach({ it.piece = null })
        for (p in pieces.split(" ")) {
            square(p.substring(1, 3)).piece = Piece.getPieceByFenChar(p[0])
        }
        return this
    }
}
