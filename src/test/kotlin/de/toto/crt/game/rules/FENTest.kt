package de.toto.crt.game.rules

import de.toto.crt.game.Position
import de.toto.crt.game.fromPGN
import de.toto.crt.game.pos
import de.toto.crt.game.rules.CastlingRight
import de.toto.crt.game.rules.Piece
import de.toto.crt.game.rules.fromFEN
import org.junit.Test

import org.junit.Assert.*

class FenTest {


    @Test
    fun pieces() {
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
    fun sideToMove() {
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").whiteToMove)
        assertFalse(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1").whiteToMove)
    }

    @Test
    fun castlingRights() {
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").
                castlingRights().contentEquals(CastlingRight.values()))
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1").
                castlingRight.isEmpty())
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kq - 0 1").
                castlingRights().contentEquals(arrayOf(CastlingRight.WHITE_SHORT, CastlingRight.BLACK_LONG)))
    }

    @Test
    fun enPassantField() {
        assertNull(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").enPassantField)
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq e2 0 1").enPassantField == "e2")
    }

    @Test
    fun halfMoveCount() {
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").halfMoveCount == 0)
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 49 1").halfMoveCount == 49)
    }

    @Test
    fun moveNumber() {
        //Position.moveNumber is the actual move number while the FEN fullmove number (number of the next move)
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").moveNumber == 0)
        assertTrue(fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 42").moveNumber == 41)
    }

    @Test
    fun toFen() {
        assertEquals(fromFEN(FEN_STARTPOSITION).toFEN(),
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        assertEquals(fromPGN("1. d4 Nf6 1-0").pos("1... Nf6")?.toFEN(),
                "rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 1 2")
        assertEquals(fromPGN("1. e4 Nf6 2. Nf3 e6 3. Bc4 Be7 4. 0-0 1-0").pos("4. 0-0")?.toFEN(),
                "rnbqk2r/ppppbppp/4pn2/8/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 3 4")
    }

}

private fun Position.castlingRights() = castlingRight.toTypedArray()