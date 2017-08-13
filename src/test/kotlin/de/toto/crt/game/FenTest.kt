package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class FenTest {


    @Test
    fun testPieces() {
        assertTrue(Position.fromFEN("r7/8/8/8/8/8/8/8 w KQkq - 0 1").square("a8").piece == Piece.BLACK_ROOK)
        assertTrue(Position.fromFEN("8/8/8/8/8/8/8/R7 w KQkq - 0 1").square("a1").piece == Piece.WHITE_ROOK)
        with(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")){
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
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").whiteToMove)
        assertFalse(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1").whiteToMove)
    }

    @Test
    fun testCastlingRights() {
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").
                castlingRights().contentEquals(CastlingRight.values()))
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1").
                castlingRight.isEmpty())
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kq - 0 1").
                castlingRights().contentEquals(arrayOf(CastlingRight.WHITE_SHORT, CastlingRight.BLACK_LONG)))
    }

    @Test
    fun testEnPassantField() {
        assertNull(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").enPassantField)
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq e2 0 1").enPassantField == "e2")
    }

    @Test
    fun testHalfMoveCount() {
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").halfMoveCount == 0)
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 49 1").halfMoveCount == 49)
    }

    @Test
    fun testMoveNumber() {
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").moveNumber == 1)
        assertTrue(Position.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 42").moveNumber == 42)
    }

}

private fun Position.castlingRights() = castlingRight.toTypedArray()