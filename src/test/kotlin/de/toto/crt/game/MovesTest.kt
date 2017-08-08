package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class MovesTest {

    @Test
    fun kingPossibleMoves() {
        assertTrue(Position("Ke1", "ke8").possibleMoves("e1").contains("d1","f1","d2","e2","f2"))
    }

    @Test
    fun queenPossibleMoves() {
        assertTrue(Position("Qa1", "Ke1", "ke8").possibleMoves("a1").contains("a8"))
        assertFalse(Position("Qa1", "Ke1", "ke8").possibleMoves("a1").contains("e1"))
        assertTrue(Position("Qa1", "Ke1", "ke8").possibleMoves("a1").contains("h8"))
        assertTrue(Position("Qa1", "Ke1", "ke8").possibleMoves("a1").contains("b2"))
        assertTrue(Position(Position.FEN_STARTPOSITION).possibleMoves("d1").isEmpty())
        assertFalse(Position("ra1", "Qb1", "Ke1", "ke8").possibleMoves("b1").contains("b2"))
        assertTrue(Position("ra1", "Qb1", "Ke1", "ke8").possibleMoves("b1").equal("a1", "c1", "d1"))
        assertTrue(Position("ba3", "Qb2", "Kc1", "ke8").possibleMoves("b2").equal("a3"))
        val p = Position("ba3", "Qb2", "Kc1", "ke8")
        p.possibleMoves("b2")
        assertTrue(p.square("a3").piece == Piece.BLACK_BISHOP)
        assertTrue(p.square("b2").piece == Piece.WHITE_QUEEN)
    }


    @Test
    fun rookPossibleMoves() {
        assertTrue(Position("Ra1", "Ke1", "ke8").possibleMoves("a1").contains("a8"))
        assertFalse(Position("Ra1", "Ke1", "ke8").possibleMoves("a1").contains("e1"))
        assertFalse(Position("Ra1", "Ke1", "ke8").possibleMoves("a1").contains("f1"))
        assertFalse(Position("Ra1", "Ke1", "ke8").possibleMoves("a1").contains("b2"))
        assertTrue(Position(Position.FEN_STARTPOSITION).possibleMoves("a1").isEmpty())
        assertFalse(Position("ra1", "Rb1", "Ke1", "ke8").possibleMoves("b1").contains("b2"))
        assertTrue(Position("ra1", "Rb1", "Ke1", "ke8").possibleMoves("b1").equal("a1", "c1", "d1"))
        assertTrue(Position("ba3", "Rb2", "Kc1", "ke8").possibleMoves("b2").isEmpty())
    }

    @Test
    fun bishopPossibleMoves() {
        assertTrue(Position("Ba1", "Ke1", "ke8").possibleMoves("a1").contains("h8"))
        assertTrue(Position("Ba1", "Rc3", "Ke1", "ke8").possibleMoves("a1").equal("b2"))
    }

    @Test
    fun knightPossibleMoves() {
        assertTrue(Position("Na1", "Ke1", "ke8").possibleMoves("a1").equal("c2", "b3"))
        assertTrue(Position("Nd4", "Ke1", "ke8").possibleMoves("d4").size == 8)
        // PIN
        assertTrue(Position("Ne4", "re5", "Ke1", "ke8").possibleMoves("d4").isEmpty())
        // only move to avoid check
        assertTrue(Position("Nc1", "re5", "Ke1", "ke8").possibleMoves("c1").equal("e2"))
        assertTrue(Position("Nf3", "re5", "Ke1", "ke8").possibleMoves("f3").equal("e5"))
    }

    @Test
    fun pawnPossibleMoves() {
    }

    @Test
    fun emptySquarePossibleMoves() {
        assertTrue(Position("Ke1", "ke8").possibleMoves("a1").isEmpty())
    }

}

fun List<Square>.contains(square: String) = any { it.name == square }
fun List<Square>.contains(vararg squares: String) = squares.all { contains(it) }
fun List<Square>.equal(vararg squares: String) = squares.size == size && squares.all { contains(it) }