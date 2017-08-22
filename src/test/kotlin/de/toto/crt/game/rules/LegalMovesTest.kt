package de.toto.crt.game.rules

import de.toto.crt.game.Pos
import de.toto.crt.game.Position
import de.toto.crt.game.rules.*
import org.junit.Test

import org.junit.Assert.*

class MovesTest {

    @Test
    fun kingPossibleMoves() {
        assertTrue(Pos("Ke1 ke8").legalMovesFrom("e1").equal("d1","f1","d2","e2","f2"))
        assertTrue(with (Pos("Ke1 Rh1 ke8")) {
            setCastlingRights(CastlingRight.WHITE_SHORT)
            legalMovesFrom("e1").equal("d1","f1","d2","e2","f2","g1")
        })
        assertTrue(with (Pos("Ke1 Rh1 Ra1 ke8 rd8")) {
            setCastlingRights(CastlingRight.WHITE_SHORT, CastlingRight.WHITE_LONG)
            legalMovesFrom("e1").equal("f1","e2","f2","g1")
        })
        assertTrue(with (Pos("Ke1 Rh1 Ra1 ke8 rc8")) {
            setCastlingRights(CastlingRight.WHITE_SHORT, CastlingRight.WHITE_LONG)
            legalMovesFrom("e1").equal("d1","f1","d2","e2","f2","g1")
        })
    }

    @Test
    fun queenPossibleMoves() {
        assertTrue(Pos("Qa1 Ke1 ke8").legalMovesFrom("a1").contains("a8"))
        assertFalse(Pos("Qa1 Ke1 ke8").legalMovesFrom("a1").contains("e1"))
        assertTrue(Pos("Qa1 Ke1 ke8").legalMovesFrom("a1").contains("h8"))
        assertTrue(Pos("Qa1 Ke1 ke8").legalMovesFrom("a1").contains("b2"))
        assertTrue(fromFEN(FEN_STARTPOSITION).legalMovesFrom("d1").isEmpty())
        assertFalse(Pos("ra1 Qb1 Ke1 ke8").legalMovesFrom("b1").contains("b2"))
        assertTrue(Pos("ra1 Qb1 Ke1 ke8").legalMovesFrom("b1").equal("a1", "c1", "d1"))
        assertTrue(Pos("ba3 Qb2 Kc1 ke8").legalMovesFrom("b2").equal("a3"))
        val p = Pos("ba3 Qb2 Kc1 ke8")
        p.legalMovesFrom("b2")
        assertTrue(p.square("a3").piece == Piece.BLACK_BISHOP)
        assertTrue(p.square("b2").piece == Piece.WHITE_QUEEN)
    }


    @Test
    fun rookPossibleMoves() {
        assertTrue(Pos("Ra1 Ke1 ke8").legalMovesFrom("a1").contains("a8"))
        assertFalse(Pos("Ra1 Ke1 ke8").legalMovesFrom("a1").contains("e1"))
        assertFalse(Pos("Ra1 Ke1 ke8").legalMovesFrom("a1").contains("f1"))
        assertFalse(Pos("Ra1 Ke1 ke8").legalMovesFrom("a1").contains("b2"))
        assertTrue(fromFEN(FEN_STARTPOSITION).legalMovesFrom("a1").isEmpty())
        assertFalse(Pos("ra1 Rb1 Ke1 ke8").legalMovesFrom("b1").contains("b2"))
        assertTrue(Pos("ra1 Rb1 Ke1 ke8").legalMovesFrom("b1").equal("a1", "c1", "d1"))
        assertTrue(Pos("ba3 Rb2 Kc1 ke8").legalMovesFrom("b2").isEmpty())
    }

    @Test
    fun bishopPossibleMoves() {
        assertTrue(Pos("Ba1 Ke1 ke8").legalMovesFrom("a1").contains("h8"))
        assertTrue(Pos("Ba1 Rc3 Ke1 ke8").legalMovesFrom("a1").equal("b2"))
        assertTrue(Pos("Bd4 Ke1 ke8").legalMovesFrom("d4").contains("b2", "b6", "g7", "f2"))
    }

    @Test
    fun knightPossibleMoves() {
        assertTrue(Pos("Na1 Ke1 ke8").legalMovesFrom("a1").equal("c2", "b3"))
        assertTrue(Pos("Nd4 Ke1 ke8").legalMovesFrom("d4").size == 8)
        // PIN
        assertTrue(Pos("Ne4 re5 Ke1 ke8").legalMovesFrom("d4").isEmpty())
        // only move to avoid check
        assertTrue(Pos("Nc1 re5 Ke1 ke8").legalMovesFrom("c1").equal("e2"))
        assertTrue(Pos("Nf3 re5 Ke1 ke8").legalMovesFrom("f3").equal("e5"))
    }

    @Test
    fun pawnPossibleMoves() {
        // from start rank
        assertTrue(Pos("Pe2 Ke1 ke8").legalMovesFrom("e2").equal("e3", "e4"))
        // normal "one move"
        assertTrue(Pos("Pe3 Ke1 ke8").legalMovesFrom("e3").equal("e4"))
        // blocked
        assertTrue(Pos("Pe3 Re4 Ke1 ke8").legalMovesFrom("e3").isEmpty())
        // pinned - can capture
        assertTrue(Pos("Pd2 Ke1 ke8 bc3").legalMovesFrom("d2").equal("c3"))
        // pinned
        assertTrue(Pos("Pd2 Ke1 ke8 bb4").legalMovesFrom("d2").isEmpty())
        // captures
        assertTrue(Pos("Pe2 Ke1 ke8 pd3 pf3").legalMovesFrom("e2").equal("e3", "e4", "d3", "f3"))

        // from start rank
        assertTrue(Pos("pe7 Ke1 ke8", false).legalMovesFrom("e7").equal("e6", "e5"))
        // normal "one move"
        assertTrue(Pos("pe6 Ke1 ke8", false).legalMovesFrom("e6").equal("e5"))
        // blocked
        assertTrue(Pos("pe3 Re2 Ke1 ke8", false).legalMovesFrom("e3").isEmpty())
        // pinned - can capture
        assertTrue(Pos("Bc6 Ke1 ke8 pd7", false).legalMovesFrom("d7").equal("c6"))
        // pinned
        assertTrue(Pos("Bb5 Ke1 ke8 pd7", false).legalMovesFrom("d7").isEmpty())
        // captures
        assertTrue(Pos("pe7 ke8 Ke1 Pd6 Pf6", false).legalMovesFrom("e7").equal("e6", "e5", "d6", "f6"))
    }

    @Test
    fun emptySquarePossibleMoves() {
        assertTrue(Pos("Ke1 ke8").legalMovesFrom("a1").isEmpty())
    }

}

private fun Position.legalMovesFrom(squareName: String) = legalMovesFrom(square(squareName))
private fun List<Square>.contains(square: String) = any { it.name == square }
private fun List<Square>.contains(vararg squares: String) = squares.all { contains(it) }
private fun List<Square>.equal(vararg squares: String) = squares.size == size && squares.all { contains(it) }