package de.toto.game

import de.toto.game.Piece
import de.toto.game.getPieceByFenChar
import org.junit.Test

import org.junit.Assert.*

class PieceTest {

    @Test
    fun count() {
        assertTrue(Piece.values().size == 12)
        assertTrue(Piece.PieceType.values().size == 6)
    }

    @Test
    fun getFigurine() {
        // not sure how to test it
    }

    @Test
    fun getColoredFigurine() {
        // not sure how to test it
    }

    @Test
    fun getType() {
        assertTrue(Piece.WHITE_KING.type == Piece.PieceType.KING)
        assertTrue(Piece.WHITE_QUEEN.type == Piece.PieceType.QUEEN)
        assertTrue(Piece.WHITE_ROOK.type == Piece.PieceType.ROOK)
        assertTrue(Piece.WHITE_BISHOP.type == Piece.PieceType.BISHOP)
        assertTrue(Piece.WHITE_KNIGHT.type == Piece.PieceType.KNIGHT)
        assertTrue(Piece.WHITE_PAWN.type == Piece.PieceType.PAWN)
        assertTrue(Piece.BLACK_KING.type == Piece.PieceType.KING)
        assertTrue(Piece.BLACK_QUEEN.type == Piece.PieceType.QUEEN)
        assertTrue(Piece.BLACK_ROOK.type == Piece.PieceType.ROOK)
        assertTrue(Piece.BLACK_BISHOP.type == Piece.PieceType.BISHOP)
        assertTrue(Piece.BLACK_KNIGHT.type == Piece.PieceType.KNIGHT)
        assertTrue(Piece.BLACK_PAWN.type == Piece.PieceType.PAWN)
    }

    @Test
    fun isWhite() {
        assertTrue(Piece.WHITE_KING.isWhite)
        assertTrue(Piece.WHITE_QUEEN.isWhite)
        assertTrue(Piece.WHITE_ROOK.isWhite)
        assertTrue(Piece.WHITE_BISHOP.isWhite)
        assertTrue(Piece.WHITE_KNIGHT.isWhite)
        assertTrue(Piece.WHITE_PAWN.isWhite)
        assertFalse(Piece.BLACK_KING.isWhite)
        assertFalse(Piece.BLACK_QUEEN.isWhite)
        assertFalse(Piece.BLACK_ROOK.isWhite)
        assertFalse(Piece.BLACK_BISHOP.isWhite)
        assertFalse(Piece.BLACK_KNIGHT.isWhite)
        assertFalse(Piece.BLACK_PAWN.isWhite)
    }

    @Test
    fun getFenChar() {
        assertTrue(Piece.WHITE_KING.fenChar == 'K')
        assertTrue(Piece.WHITE_QUEEN.fenChar == 'Q')
        assertTrue(Piece.WHITE_ROOK.fenChar == 'R')
        assertTrue(Piece.WHITE_BISHOP.fenChar == 'B')
        assertTrue(Piece.WHITE_KNIGHT.fenChar == 'N')
        assertTrue(Piece.WHITE_PAWN.fenChar == 'P')
        assertTrue(Piece.BLACK_KING.fenChar == 'k')
        assertTrue(Piece.BLACK_QUEEN.fenChar == 'q')
        assertTrue(Piece.BLACK_ROOK.fenChar == 'r')
        assertTrue(Piece.BLACK_BISHOP.fenChar == 'b')
        assertTrue(Piece.BLACK_KNIGHT.fenChar == 'n')
        assertTrue(Piece.BLACK_PAWN.fenChar == 'p')
    }

    @Test
    fun getPgnChar() {
        assertTrue(Piece.WHITE_KING.pgnChar == 'K')
        assertTrue(Piece.WHITE_QUEEN.pgnChar == 'Q')
        assertTrue(Piece.WHITE_ROOK.pgnChar == 'R')
        assertTrue(Piece.WHITE_BISHOP.pgnChar == 'B')
        assertTrue(Piece.WHITE_KNIGHT.pgnChar == 'N')
        assertTrue(Piece.WHITE_PAWN.pgnChar == ' ')
        assertTrue(Piece.BLACK_KING.pgnChar == 'K')
        assertTrue(Piece.BLACK_QUEEN.pgnChar == 'Q')
        assertTrue(Piece.BLACK_ROOK.pgnChar == 'R')
        assertTrue(Piece.BLACK_BISHOP.pgnChar == 'B')
        assertTrue(Piece.BLACK_KNIGHT.pgnChar == 'N')
        assertTrue(Piece.BLACK_PAWN.pgnChar == ' ')
    }

    @Test
    fun getPieceByFenChar() {
        assertTrue(getPieceByFenChar('b') == Piece.BLACK_BISHOP)
    }

}