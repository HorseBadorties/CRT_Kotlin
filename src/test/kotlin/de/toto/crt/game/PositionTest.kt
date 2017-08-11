package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class PositionTest {

    private val LOOP_COUNT = 1 //1_000_000

    @Test
    fun constructorWithFen() {
        assertNotNull(Position(FEN_EMPTY_BOARD))
        assertNotNull(Position(FEN_STARTPOSITION))
    }


    fun doFailConstructorWithFen(fen: String) {
        try {
            Position(fen)
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

    @Test
    fun getPiecesByColor() {
        assertTrue(Position(FEN_EMPTY_BOARD).getPiecesByColor(true).size == 0)
        assertTrue(Position(FEN_EMPTY_BOARD).getPiecesByColor(false).size == 0)
        assertTrue(Position(FEN_STARTPOSITION).getPiecesByColor(true).size == 16)
        assertTrue(Position(FEN_STARTPOSITION).getPiecesByColor(false).size == 16)
        assertTrue(Pos("Ne4", "Re5", "Ke1", "ke8").getPiecesByColor(false).size == 1)
        assertTrue(Pos("Ne4", "Re5", "Ke1", "ke8").getPiecesByColor(true).size == 3)
    }

}

// create a Position using FEN-Notation for Pieces, like "ke8" for "black king on e8 or "Ke1" for "white king on e1"
fun Pos(vararg pieces : String): Position {
    val result = Position()
    result.squares().forEach({ it.piece = null })
    for (p in pieces) {
        result.square(p.substring(1, 3)).piece = Piece.getPieceByFenChar(p[0])
    }
    return result
}
