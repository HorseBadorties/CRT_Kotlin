package de.toto.crt.game

import de.toto.crt.game.rules.FEN_EMPTY_BOARD
import de.toto.crt.game.rules.FEN_STARTPOSITION
import de.toto.crt.game.rules.Piece
import de.toto.crt.game.rules.fromFEN
import org.junit.Test

import org.junit.Assert.*

class PositionTest {

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
        assertTrue(Position().square(1, 1).name == "a1")
        assertTrue(Position().square(1, 8).name == "h1")
        assertTrue(Position().square(2, 1).name == "a2")
        assertTrue(Position().square(3, 8).name == "h3")
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
