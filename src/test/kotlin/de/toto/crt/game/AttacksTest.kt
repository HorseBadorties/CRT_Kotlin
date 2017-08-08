package de.toto.crt.game

import org.junit.Assert
import org.junit.Test

class AttacksTest {

    private val LOOP_COUNT = 1 //1_000_000

    @Test
    fun kingAttacks() {
        val pos = Position()
        Assert.assertTrue(pos.kingAttacks("b2", "c3"))
        Assert.assertTrue(pos.kingAttacks("a1", "b1"))
        Assert.assertTrue(pos.kingAttacks("a1", "a2"))
        Assert.assertFalse(pos.kingAttacks("a1", "c3"))
        Assert.assertFalse(pos.kingAttacks("a1", "a3"))
        Assert.assertFalse(pos.kingAttacks("a1", "c1"))
        Assert.assertFalse(pos.kingAttacks("a1", "a1"))
    }

    @Test
    fun rookAttacks() {
        var pos = Position()
        for (i in 1..LOOP_COUNT) {
            Assert.assertTrue(pos.rookAttacks("d4", "d1"))
            pos.square("c1").piece = Piece.WHITE_ROOK
            Assert.assertTrue(pos.rookAttacks("a1", "c1"))
            Assert.assertTrue(pos.rookAttacks("a1", "a8"))
            Assert.assertTrue(pos.rookAttacks("a1", "c1"))
            Assert.assertFalse(pos.rookAttacks("a1", "d1"))
            Assert.assertFalse(pos.rookAttacks("a1", "h1"))
            Assert.assertFalse(pos.rookAttacks("a1", "a1"))
            pos = Position()
        }
    }

    @Test
    fun bishopAttacks() {
        var pos = Position()
        for (i in 1..LOOP_COUNT) {
            Assert.assertTrue(pos.bishopAttacks("a1", "b2"))
            Assert.assertTrue(pos.bishopAttacks("a1", "h8"))
            Assert.assertTrue(pos.bishopAttacks("a8", "h1"))
            Assert.assertTrue(pos.bishopAttacks("h1", "b7"))
            Assert.assertTrue(pos.bishopAttacks("h8", "g7"))
            Assert.assertTrue(pos.bishopAttacks("a1", "h8"))
            Assert.assertTrue(pos.bishopAttacks("d4", "f6"))
            Assert.assertTrue(pos.bishopAttacks("d4", "a7"))
            Assert.assertTrue(pos.bishopAttacks("d4", "a1"))
            Assert.assertTrue(pos.bishopAttacks("d4", "f2"))
            Assert.assertFalse(pos.bishopAttacks("a1", "c1"))
            Assert.assertFalse(pos.bishopAttacks("b1", "a3"))
            Assert.assertFalse(pos.bishopAttacks("a1", "a1"))
            pos.square("c2").piece = Piece.WHITE_ROOK
            Assert.assertFalse(pos.bishopAttacks("b1", "d3"))
            Assert.assertFalse(pos.bishopAttacks("d1", "b3"))
            Assert.assertFalse(pos.bishopAttacks("d3", "b1"))
            Assert.assertFalse(pos.bishopAttacks("b3", "d1"))
            pos = Position()
        }
    }

    @Test
    fun queenAttacks() {
        var pos = Position()
        for (i in 1..LOOP_COUNT) {
            Assert.assertTrue(pos.queenAttacks("d4", "a1"))
            Assert.assertTrue(pos.queenAttacks("d4", "g7"))
            Assert.assertTrue(pos.queenAttacks("d4", "g1"))
            Assert.assertTrue(pos.queenAttacks("d4", "a7"))
            Assert.assertTrue(pos.queenAttacks("d4", "d2"))
            Assert.assertTrue(pos.queenAttacks("d4", "d1"))
            Assert.assertTrue(pos.queenAttacks("d4", "d8"))
            Assert.assertTrue(pos.queenAttacks("d4", "a4"))
            Assert.assertTrue(pos.queenAttacks("d4", "h4"))
            Assert.assertFalse(pos.queenAttacks("d4", "a2"))
            Assert.assertFalse(pos.queenAttacks("d4", "f8"))
            Assert.assertFalse(pos.queenAttacks("d4", "d4"))
            Assert.assertFalse(pos.queenAttacks("d4", "a8"))
            Assert.assertFalse(pos.queenAttacks("d4", "e1"))
            pos.square("c2").piece = Piece.WHITE_ROOK
        }
    }

    @Test
    fun knightAttacks() {
        val pos = Position()
        for (i in 1..LOOP_COUNT) {
            Assert.assertTrue(pos.knightAttacks("d4", "c6"))
            Assert.assertTrue(pos.knightAttacks("d4", "e6"))
            Assert.assertTrue(pos.knightAttacks("d4", "b5"))
            Assert.assertTrue(pos.knightAttacks("d4", "f5"))
            Assert.assertTrue(pos.knightAttacks("d4", "f3"))
            Assert.assertTrue(pos.knightAttacks("d4", "b3"))
            Assert.assertTrue(pos.knightAttacks("d4", "c2"))
            Assert.assertTrue(pos.knightAttacks("d4", "e2"))
            Assert.assertFalse(pos.knightAttacks("d4", "e3"))
            Assert.assertFalse(pos.knightAttacks("d4", "e3"))
            Assert.assertFalse(pos.knightAttacks("d4", "d4"))
            Assert.assertFalse(pos.knightAttacks("d4", "a1"))
        }
    }

    @Test
    fun pawnAttacks() {
        val pos = Position()
        for (i in 1..LOOP_COUNT) {
            Assert.assertTrue(pos.pawnAttacks(PAWN_WHITE, "d4", "c5"))
            Assert.assertTrue(pos.pawnAttacks(PAWN_WHITE, "d4", "e5"))
            Assert.assertFalse(pos.pawnAttacks(PAWN_BLACK, "d4", "c5"))
            Assert.assertFalse(pos.pawnAttacks(PAWN_BLACK, "d4", "e5"))
            Assert.assertTrue(pos.pawnAttacks(PAWN_WHITE, "a2", "b3"))
            Assert.assertTrue(pos.pawnAttacks(PAWN_BLACK, "a2", "b1"))
            Assert.assertFalse(pos.pawnAttacks(PAWN_WHITE, "a2", "a2"))
        }
    }

}


val PAWN_WHITE = true
val PAWN_BLACK = false

// some helper functions
fun Position.knightAttacks(from: String, to: String) = knightAttacks(this.square(from), this.square(to))
fun Position.bishopAttacks(from: String, to: String) = bishopAttacks(this.square(from), this.square(to))
fun Position.rookAttacks(from: String, to: String) = rookAttacks(this.square(from), this.square(to))
fun Position.queenAttacks(from: String, to: String) = queenAttacks(this.square(from), this.square(to))
fun Position.kingAttacks(from: String, to: String) = kingAttacks(this.square(from), this.square(to))
fun Position.pawnAttacks(isWhitePawn: Boolean, from: String, to: String) =
        pawnAttacks(isWhitePawn, this.square(from), this.square(to))