package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class PositionTest {


    @Test
    fun constructorWithFen() {
        assertNotNull(Position.fromFEN(FEN_EMPTY_BOARD))
        assertNotNull(Position.fromFEN(FEN_STARTPOSITION))
    }


    fun doFailConstructorWithFen(fen: String) {
        try {
            Position.fromFEN(fen)
            fail("IllegalArgumentException expected")
        } catch (e: IllegalArgumentException) {
        }
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

    fun doFailSquare(rank: Int, file: Int) {
        try {
            Position().square(rank, file)
            fail("ArrayIndexOutOfBoundsException expected")
        } catch (e: ArrayIndexOutOfBoundsException) {
        }
    }

    fun doFailSquare(name: String) {
        try {
            Position().square(name)
            fail("ArrayIndexOutOfBoundsException expected")
        } catch (e: ArrayIndexOutOfBoundsException) {
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

    @Test
    fun getPiecesByColor() {
        assertTrue(Position.fromFEN(FEN_EMPTY_BOARD).getPiecesByColor(true).size == 0)
        assertTrue(Position.fromFEN(FEN_EMPTY_BOARD).getPiecesByColor(false).size == 0)
        assertTrue(Position.fromFEN(FEN_STARTPOSITION).getPiecesByColor(true).size == 16)
        assertTrue(Position.fromFEN(FEN_STARTPOSITION).getPiecesByColor(false).size == 16)
        assertTrue(Pos("Ne4 Re5 Ke1 ke8").getPiecesByColor(false).size == 1)
        assertTrue(Pos("Ne4 Re5 Ke1 ke8").getPiecesByColor(true).size == 3)
    }

}

// create a Position using FEN-Notation for Pieces, like "ke8" for "black king on e8 or "Ke1" for "white king on e1"
fun Pos(pieces : String, whiteToMove: Boolean = true): Position {
    val result = Position(whiteToMove = whiteToMove)
    result.squares().forEach({ it.piece = null })
    for (p in pieces.split(" ")) {
        result.square(p.substring(1, 3)).piece = Piece.getPieceByFenChar(p[0])
    }
    return result
}
