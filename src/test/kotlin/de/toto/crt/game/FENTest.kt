package de.toto.crt.game

import de.toto.crt.game.rules.CastlingRight
import de.toto.crt.game.rules.Piece
import de.toto.crt.game.rules.fromFEN
import org.junit.Test

import org.junit.Assert.*

class FenTest {


    @Test
    fun testPieces() {
        assertTrue(fromFEN("r7/8/8/8/8/8/8/8 w KQkq - 0 1").square("a8").piece == Piece.BLACK_ROOK)
        assertTrue(fromFEN("8/8/8/8/8/8/8/R7 w KQkq - 0 1").square("a1").piece == Piece.WHITE_ROOK)
        with(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")){
           assertTrue(square("a1").piece == Piece.WHITE_ROOK)
           assertTrue(square("d1").piece == Piece.WHITE_QUEEN)
           assertTrue(square("e1").piece == Piece.WHITE_KING)
           assertTrue(square("a2").piece == Piece.WHITE_PAWN)
           assertTrue(square("a3").isEmpty)
           assertTrue(square("a7").piece == Piece.BLACK_PAWN)
           assertTrue(square("e8").piece == Piece.BLACK_KING)
           assertTrue(square("d8").piece == Piece.BLACK_QUEEN)
           assertTrue(square("a8").piece == Piece.BLACK_ROOK)
        }
    }

    @Test
    fun testSideToMove() {
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").whiteToMove)
        assertFalse(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1").whiteToMove)
    }

    @Test
    fun testCastlingRights() {
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").
                castlingRights().contentEquals(CastlingRight.values()))
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1").
                castlingRight.isEmpty())
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kq - 0 1").
                castlingRights().contentEquals(arrayOf(CastlingRight.WHITE_SHORT, CastlingRight.BLACK_LONG)))
    }

    @Test
    fun testEnPassantField() {
        assertNull(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").enPassantField)
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq e2 0 1").enPassantField == "e2")
    }

    @Test
    fun testHalfMoveCount() {
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").halfMoveCount == 0)
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 49 1").halfMoveCount == 49)
    }

    @Test
    fun testMoveNumber() {
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").moveNumber == 1)
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 42").moveNumber == 42)
    }

}

private fun Position.castlingRights() = castlingRight.toTypedArray()