package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

import de.toto.crt.game.Position.*

class MovesTest {

    @Test
    fun kingPossibleMoves() {
        assertTrue(Position("Ke1", "ke8").movesFrom("e1").equal("d1","f1","d2","e2","f2"))
        assertTrue(with (Position("Ke1", "Rh1", "ke8")) {
            defineCastleRights(CastlingRight.WHITE_SHORT)
            movesFrom("e1").equal("d1","f1","d2","e2","f2","g1")
        })
        assertTrue(with (Position("Ke1", "Rh1", "Ra1", "ke8", "rd8")) {
            defineCastleRights(CastlingRight.WHITE_SHORT, CastlingRight.WHITE_LONG)
            movesFrom("e1").equal("f1","e2","f2","g1")
        })
        assertTrue(with (Position("Ke1", "Rh1", "Ra1", "ke8", "rc8")) {
            defineCastleRights(CastlingRight.WHITE_SHORT, CastlingRight.WHITE_LONG)
            movesFrom("e1").equal("d1","f1","d2","e2","f2","g1")
        })
    }

    @Test
    fun queenPossibleMoves() {
        assertTrue(Position("Qa1", "Ke1", "ke8").movesFrom("a1").contains("a8"))
        assertFalse(Position("Qa1", "Ke1", "ke8").movesFrom("a1").contains("e1"))
        assertTrue(Position("Qa1", "Ke1", "ke8").movesFrom("a1").contains("h8"))
        assertTrue(Position("Qa1", "Ke1", "ke8").movesFrom("a1").contains("b2"))
        assertTrue(Position(Position.FEN_STARTPOSITION).movesFrom("d1").isEmpty())
        assertFalse(Position("ra1", "Qb1", "Ke1", "ke8").movesFrom("b1").contains("b2"))
        assertTrue(Position("ra1", "Qb1", "Ke1", "ke8").movesFrom("b1").equal("a1", "c1", "d1"))
        assertTrue(Position("ba3", "Qb2", "Kc1", "ke8").movesFrom("b2").equal("a3"))
        val p = Position("ba3", "Qb2", "Kc1", "ke8")
        p.movesFrom("b2")
        assertTrue(p.square("a3").piece == Piece.BLACK_BISHOP)
        assertTrue(p.square("b2").piece == Piece.WHITE_QUEEN)
    }


    @Test
    fun rookPossibleMoves() {
        assertTrue(Position("Ra1", "Ke1", "ke8").movesFrom("a1").contains("a8"))
        assertFalse(Position("Ra1", "Ke1", "ke8").movesFrom("a1").contains("e1"))
        assertFalse(Position("Ra1", "Ke1", "ke8").movesFrom("a1").contains("f1"))
        assertFalse(Position("Ra1", "Ke1", "ke8").movesFrom("a1").contains("b2"))
        assertTrue(Position(Position.FEN_STARTPOSITION).movesFrom("a1").isEmpty())
        assertFalse(Position("ra1", "Rb1", "Ke1", "ke8").movesFrom("b1").contains("b2"))
        assertTrue(Position("ra1", "Rb1", "Ke1", "ke8").movesFrom("b1").equal("a1", "c1", "d1"))
        assertTrue(Position("ba3", "Rb2", "Kc1", "ke8").movesFrom("b2").isEmpty())
    }

    @Test
    fun bishopPossibleMoves() {
        assertTrue(Position("Ba1", "Ke1", "ke8").movesFrom("a1").contains("h8"))
        assertTrue(Position("Ba1", "Rc3", "Ke1", "ke8").movesFrom("a1").equal("b2"))
    }

    @Test
    fun knightPossibleMoves() {
        assertTrue(Position("Na1", "Ke1", "ke8").movesFrom("a1").equal("c2", "b3"))
        assertTrue(Position("Nd4", "Ke1", "ke8").movesFrom("d4").size == 8)
        // PIN
        assertTrue(Position("Ne4", "re5", "Ke1", "ke8").movesFrom("d4").isEmpty())
        // only move to avoid check
        assertTrue(Position("Nc1", "re5", "Ke1", "ke8").movesFrom("c1").equal("e2"))
        assertTrue(Position("Nf3", "re5", "Ke1", "ke8").movesFrom("f3").equal("e5"))
    }

    @Test
    fun pawnPossibleMoves() {
        // from start rank
        assertTrue(Position("Pe2", "Ke1", "ke8").movesFrom("e2").equal("e3", "e4"))
        // normal "one move"
        assertTrue(Position("Pe3", "Ke1", "ke8").movesFrom("e3").equal("e4"))
        // blocked
        assertTrue(Position("Pe3", "Re4", "Ke1", "ke8").movesFrom("e3").isEmpty())
        // pinned - can capture
        assertTrue(Position("Pd2", "Ke1", "ke8", "bc3").movesFrom("d2").equal("c3"))
        // pinned
        assertTrue(Position("Pd2", "Ke1", "ke8", "bb4").movesFrom("d2").isEmpty())
        assertTrue(Position("Pe2", "Ke1", "ke8", "pd3", "pf3").movesFrom("e2").equal("e3", "e4", "d3", "f3"))

        // from start rank
        assertTrue(Position("pe7", "Ke1", "ke8").movesFrom("e7").equal("e6", "e5"))
        // normal "one move"
        assertTrue(Position("Pe6", "Ke1", "ke8").movesFrom("e6").equal("e5"))
        // blocked
        assertTrue(Position("pe3", "Re2", "Ke1", "ke8").movesFrom("e3").isEmpty())
        // pinned - can capture
        assertTrue(Position("Bc6", "Ke1", "ke8", "pd7").movesFrom("d7").equal("c6"))
        // pinned
        assertTrue(Position("Bb5", "Ke1", "ke8", "pd7").movesFrom("d7").isEmpty())

        assertTrue(Position("Pe2", "Ke1", "ke8", "pd3", "pf3").movesFrom("e2").equal("e3", "e4", "d3", "f3"))
    }

    @Test
    fun emptySquarePossibleMoves() {
        assertTrue(Position("Ke1", "ke8").movesFrom("a1").isEmpty())
    }

}

fun List<Square>.contains(square: String) = any { it.name == square }
fun List<Square>.contains(vararg squares: String) = squares.all { contains(it) }
fun List<Square>.equal(vararg squares: String) = squares.size == size && squares.all { contains(it) }