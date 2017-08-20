package de.toto.crt.game

import org.junit.Assert
import org.junit.Test

class AttacksTest {

    @Test
    fun kingAttacks() {
        Assert.assertTrue(Position().kingAttacks("b2", "c3"))
        Assert.assertTrue(Position().kingAttacks("a1", "b1"))
        Assert.assertTrue(Position().kingAttacks("a1", "a2"))
        Assert.assertFalse(Position().kingAttacks("a1", "c3"))
        Assert.assertFalse(Position().kingAttacks("a1", "a3"))
        Assert.assertFalse(Position().kingAttacks("a1", "c1"))
        Assert.assertFalse(Position().kingAttacks("a1", "a1"))
    }

    @Test
    fun rookAttacks() {
        Assert.assertTrue(Position().rookAttacks("d4", "d1"))
        Assert.assertTrue(Pos("Rc1").rookAttacks("a1", "c1"))
        Assert.assertTrue(Pos("Rc1").rookAttacks("a1", "a8"))
        Assert.assertTrue(Pos("Rc1").rookAttacks("a1", "c1"))
        Assert.assertFalse(Pos("bc1").rookAttacks("a1", "d1"))
        Assert.assertFalse(Pos("bc1").rookAttacks("a1", "h1"))
        Assert.assertFalse(Pos("bc1").rookAttacks("a1", "a1"))
    }

    @Test
    fun bishopAttacks() {
        Assert.assertTrue(Position().bishopAttacks("a1", "b2"))
        Assert.assertTrue(Position().bishopAttacks("a1", "h8"))
        Assert.assertTrue(Position().bishopAttacks("a8", "h1"))
        Assert.assertTrue(Position().bishopAttacks("h1", "b7"))
        Assert.assertTrue(Position().bishopAttacks("h8", "g7"))
        Assert.assertTrue(Position().bishopAttacks("a1", "h8"))
        Assert.assertTrue(Position().bishopAttacks("d4", "f6"))
        Assert.assertTrue(Position().bishopAttacks("d4", "a7"))
        Assert.assertTrue(Position().bishopAttacks("d4", "a1"))
        Assert.assertTrue(Position().bishopAttacks("d4", "f2"))
        Assert.assertFalse(Position().bishopAttacks("a1", "c1"))
        Assert.assertFalse(Position().bishopAttacks("b1", "a3"))
        Assert.assertFalse(Position().bishopAttacks("a1", "a1"))
        Assert.assertFalse(Pos("Rc2").bishopAttacks("b1", "d3"))
        Assert.assertFalse(Pos("Rc2").bishopAttacks("d1", "b3"))
        Assert.assertFalse(Pos("Rc2").bishopAttacks("d3", "b1"))
        Assert.assertFalse(Pos("Rc2").bishopAttacks("b3", "d1"))
    }

    @Test
    fun queenAttacks() {
        Assert.assertTrue(Position().queenAttacks("d4", "a1"))
        Assert.assertTrue(Position().queenAttacks("d4", "g7"))
        Assert.assertTrue(Position().queenAttacks("d4", "g1"))
        Assert.assertTrue(Position().queenAttacks("d4", "a7"))
        Assert.assertTrue(Position().queenAttacks("d4", "d2"))
        Assert.assertTrue(Position().queenAttacks("d4", "d1"))
        Assert.assertTrue(Position().queenAttacks("d4", "d8"))
        Assert.assertTrue(Position().queenAttacks("d4", "a4"))
        Assert.assertTrue(Position().queenAttacks("d4", "h4"))
        Assert.assertFalse(Position().queenAttacks("d4", "a2"))
        Assert.assertFalse(Position().queenAttacks("d4", "f8"))
        Assert.assertFalse(Position().queenAttacks("d4", "d4"))
        Assert.assertFalse(Position().queenAttacks("d4", "a8"))
        Assert.assertFalse(Position().queenAttacks("d4", "e1"))
    }

    @Test
    fun knightAttacks() {
        Assert.assertTrue(Position().knightAttacks("d4", "c6"))
        Assert.assertTrue(Position().knightAttacks("d4", "e6"))
        Assert.assertTrue(Position().knightAttacks("d4", "b5"))
        Assert.assertTrue(Position().knightAttacks("d4", "f5"))
        Assert.assertTrue(Position().knightAttacks("d4", "f3"))
        Assert.assertTrue(Position().knightAttacks("d4", "b3"))
        Assert.assertTrue(Position().knightAttacks("d4", "c2"))
        Assert.assertTrue(Position().knightAttacks("d4", "e2"))
        Assert.assertFalse(Position().knightAttacks("d4", "e3"))
        Assert.assertFalse(Position().knightAttacks("d4", "e3"))
        Assert.assertFalse(Position().knightAttacks("d4", "d4"))
        Assert.assertFalse(Position().knightAttacks("d4", "a1"))
    }

    @Test
    fun pawnAttacks() {
        Assert.assertTrue(Position().pawnAttacks(Piece.WHITE_PAWN, "d4", "c5"))
        Assert.assertTrue(Position().pawnAttacks(Piece.WHITE_PAWN, "d4", "e5"))
        Assert.assertFalse(Position().pawnAttacks(Piece.BLACK_PAWN, "d4", "c5"))
        Assert.assertFalse(Position().pawnAttacks(Piece.BLACK_PAWN, "d4", "e5"))
        Assert.assertTrue(Position().pawnAttacks(Piece.WHITE_PAWN, "a2", "b3"))
        Assert.assertTrue(Position().pawnAttacks(Piece.BLACK_PAWN, "a2", "b1"))
        Assert.assertFalse(Position().pawnAttacks(Piece.WHITE_PAWN, "a2", "a2"))
    }

}

// some helper functions
private fun Position.pieceAttacks(piece: Piece, from: String, to: String): Boolean {
    val sFrom = square(from)
    sFrom.piece = piece
    return squareIsAttackedBy(this.square(to), piece.isWhite)
}

private fun Position.knightAttacks(from: String, to: String) = pieceAttacks(Piece.WHITE_KNIGHT, from ,to)
private fun Position.bishopAttacks(from: String, to: String) = pieceAttacks(Piece.WHITE_BISHOP, from ,to)
private fun Position.rookAttacks(from: String, to: String) = pieceAttacks(Piece.WHITE_ROOK, from ,to)
private fun Position.queenAttacks(from: String, to: String) = pieceAttacks(Piece.WHITE_QUEEN, from ,to)
private fun Position.kingAttacks(from: String, to: String) = pieceAttacks(Piece.WHITE_KING, from ,to)
private fun Position.pawnAttacks(piece: Piece, from: String, to: String) = pieceAttacks(piece, from ,to)