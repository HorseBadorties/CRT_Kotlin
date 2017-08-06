package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class PositionTest {

    private val LOOP_COUNT = 1 //1_000_000

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
    fun kingAttacks() {
        val pos = Position()
        assertTrue(pos.kingAttacks("b2", "c3"))
        assertTrue(pos.kingAttacks("a1", "b1"))
        assertTrue(pos.kingAttacks("a1", "a2"))
        assertFalse(pos.kingAttacks("a1", "c3"))
        assertFalse(pos.kingAttacks("a1", "a3"))
        assertFalse(pos.kingAttacks("a1", "c1"))
        assertFalse(pos.kingAttacks("a1", "a1"))
    }

    @Test
    fun rookAttacks() {
        val pos = Position()
        for (i in 1..LOOP_COUNT) {
            assertTrue(pos.rookAttacks("d4", "d1"))
            pos.square("c1").piece = Piece.WHITE_ROOK
            assertTrue(pos.rookAttacks("a1", "c1"))
            assertTrue(pos.rookAttacks("a1", "a8"))
            assertTrue(pos.rookAttacks("a1", "c1"))
            assertFalse(pos.rookAttacks("a1", "d1"))
            assertFalse(pos.rookAttacks("a1", "d1"))
            assertFalse(pos.rookAttacks("a1", "a1"))
            val pos = Position()
        }
    }

    @Test
    fun bishopAttacks() {
        val pos = Position()
        for (i in 1..LOOP_COUNT) {
            assertTrue(pos.bishopAttacks("a1", "b2"))
            assertTrue(pos.bishopAttacks("a1", "h8"))
            assertTrue(pos.bishopAttacks("a8", "h1"))
            assertTrue(pos.bishopAttacks("h1", "b7"))
            assertTrue(pos.bishopAttacks("h8", "g7"))
            assertTrue(pos.bishopAttacks("a1", "h8"))
            assertTrue(pos.bishopAttacks("d4", "f6"))
            assertTrue(pos.bishopAttacks("d4", "a7"))
            assertTrue(pos.bishopAttacks("d4", "a1"))
            assertTrue(pos.bishopAttacks("d4", "f2"))
            assertFalse(pos.bishopAttacks("a1", "c1"))
            assertFalse(pos.bishopAttacks("b1", "a3"))
            assertFalse(pos.bishopAttacks("a1", "a1"))
            pos.square("c2").piece = Piece.WHITE_ROOK
            assertFalse(pos.bishopAttacks("b1", "d3"))
            assertFalse(pos.bishopAttacks("d1", "b3"))
            assertFalse(pos.bishopAttacks("d3", "b1"))
            assertFalse(pos.bishopAttacks("b3", "d1"))
        }
    }

    @Test
    fun queenAttacks() {
        val pos = Position()
        for (i in 1..LOOP_COUNT) {
            assertTrue(pos.queenAttacks("d4", "a1"))
            assertTrue(pos.queenAttacks("d4", "g7"))
            assertTrue(pos.queenAttacks("d4", "g1"))
            assertTrue(pos.queenAttacks("d4", "a7"))
            assertTrue(pos.queenAttacks("d4", "d2"))
            assertTrue(pos.queenAttacks("d4", "d1"))
            assertTrue(pos.queenAttacks("d4", "d8"))
            assertTrue(pos.queenAttacks("d4", "a4"))
            assertTrue(pos.queenAttacks("d4", "h4"))
            assertFalse(pos.queenAttacks("d4", "a2"))
            assertFalse(pos.queenAttacks("d4", "f8"))
            assertFalse(pos.queenAttacks("d4", "d4"))
            assertFalse(pos.queenAttacks("d4", "a8"))
            assertFalse(pos.queenAttacks("d4", "e1"))
            pos.square("c2").piece = Piece.WHITE_ROOK
        }
    }

    @Test
    fun knightAttacks() {
        val pos = Position()
        for (i in 1..LOOP_COUNT) {
            assertTrue(pos.knightAttacks("d4", "c6"))
            assertTrue(pos.knightAttacks("d4", "e6"))
            assertTrue(pos.knightAttacks("d4", "b5"))
            assertTrue(pos.knightAttacks("d4", "f5"))
            assertTrue(pos.knightAttacks("d4", "f3"))
            assertTrue(pos.knightAttacks("d4", "b3"))
            assertTrue(pos.knightAttacks("d4", "c2"))
            assertTrue(pos.knightAttacks("d4", "e2"))
            assertFalse(pos.knightAttacks("d4", "e3"))
            assertFalse(pos.knightAttacks("d4", "e3"))
            assertFalse(pos.knightAttacks("d4", "d4"))
            assertFalse(pos.knightAttacks("d4", "a1"))
        }
    }

    @Test
    fun pawnAttacks() {
        val pos = Position()
        for (i in 1..LOOP_COUNT) {
            assertTrue(pos.pawnAttacks(WHITE_PAWN, "d4", "c5"))
            assertTrue(pos.pawnAttacks(WHITE_PAWN, "d4", "e5"))
            assertFalse(pos.pawnAttacks(BLACK_PAWN, "d4", "c5"))
            assertFalse(pos.pawnAttacks(BLACK_PAWN, "d4", "e5"))
            assertTrue(pos.pawnAttacks(WHITE_PAWN, "a2", "b3"))
            assertTrue(pos.pawnAttacks(BLACK_PAWN, "a2", "b1"))
            assertFalse(pos.pawnAttacks(WHITE_PAWN, "a2", "a2"))
        }
    }

    @Test
    fun pawnCanMoveTo_Moves() {
        var pos = Position()
        for (i in 1..LOOP_COUNT) {
            assertTrue(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "d3"))
            assertTrue(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "d4"))
            assertFalse(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "d5"))
            assertFalse(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "d1"))
            assertTrue(pos.pawnCanMoveTo(BLACK_PAWN, "d2", "d1"))
            pos.square("d3").piece = Piece.BLACK_BISHOP
            assertFalse(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "d4"))
            pos = Position()
        }
    }

    @Test
    fun pawnCanMoveTo_Captures() {
        var pos = Position()
        for (i in 1..LOOP_COUNT) {
            assertFalse(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "c3"))
            assertFalse(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "e3"))
            pos.square("c3").piece = Piece.BLACK_BISHOP
            assertTrue(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "c3"))
            assertFalse(pos.pawnCanMoveTo(WHITE_PAWN, "d2", "e3"))

            assertFalse(pos.pawnCanMoveTo(BLACK_PAWN, "d2", "c1"))
            assertFalse(pos.pawnCanMoveTo(BLACK_PAWN, "d2", "e1"))
            pos.square("c1").piece = Piece.WHITE_BISHOP
            assertTrue(pos.pawnCanMoveTo(BLACK_PAWN, "d2", "c1"))
            assertFalse(pos.pawnCanMoveTo(BLACK_PAWN, "d2", "e1"))
            pos.square("c1").piece = Piece.BLACK_BISHOP
            assertFalse(pos.pawnCanMoveTo(BLACK_PAWN, "d2", "c1"))
            pos = Position()
        }
    }

    @Test
    fun pawnCanMoveTo_CapturesEnPassant() {
        for (i in 1..LOOP_COUNT) {
            //  TODO
        }
    }
}

//
val WHITE_PAWN = true
val BLACK_PAWN = false
// some helper functions
fun Position.knightAttacks(from: String, to: String) = knightAttacks(this.square(from), this.square(to))
fun Position.bishopAttacks(from: String, to: String) = bishopAttacks(this.square(from), this.square(to))
fun Position.rookAttacks(from: String, to: String) = rookAttacks(this.square(from), this.square(to))
fun Position.queenAttacks(from: String, to: String) = queenAttacks(this.square(from), this.square(to))
fun Position.kingAttacks(from: String, to: String) = kingAttacks(this.square(from), this.square(to))
fun Position.pawnAttacks(isWhitePawn: Boolean, from: String, to: String) =
        pawnAttacks(isWhitePawn, this.square(from), this.square(to))
fun Position.pawnCanMoveTo(isWhitePawn: Boolean, from: String, to: String) =
        pawnCanMoveTo(isWhitePawn, this.square(from), this.square(to))