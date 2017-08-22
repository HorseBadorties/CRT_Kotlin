package de.toto.crt.game.rules

import de.toto.crt.game.Position
import de.toto.crt.game.rules.*
import org.junit.Test

import org.junit.Assert.*

import de.toto.crt.game.rules.CastlingRight.*

class MoveParserTest {

    @Test
    fun createNextFromSAN() {
        var pos = fromFEN(FEN_STARTPOSITION)
        var pos2 = pos.createNextFromSAN("e4")
        assertTrue(pos.next.contains(pos2))
        assertTrue(pos2.previous == pos)
        pos = fromFEN(FEN_STARTPOSITION)
        pos = pos.createNextFromSANs("1.e4 e5 2.Nf3 Nc6 3.Bc4 d6 4.0-0 Bg4 5.Nc3 Qe7 6.a3 0-0-0")
        assertTrue(pos.whiteToMove)
    }

    @Test
    fun whiteToMove() {
        var pos = fromFEN(FEN_STARTPOSITION)
        pos = pos.createNextFromSANs("1.e4 d5 2.Nf3 d4")
        assertTrue(pos.whiteToMove)
        pos = pos.createNextFromSANs("3.c4")
        assertFalse(pos.whiteToMove)
    }

    @Test
    fun castlingRights() {
        var pos = fromFEN(FEN_STARTPOSITION)
        pos = pos.createNextFromSANs("1.e4 e5 2.h4 h5")
        assertTrue(pos.hasCastlingRight(WHITE_SHORT))
        assertTrue(pos.hasCastlingRight(BLACK_SHORT))
        pos = pos.createNextFromSANs("3.Ke2")
        assertFalse(pos.hasCastlingRight(WHITE_SHORT))
        assertFalse(pos.hasCastlingRight(WHITE_LONG))
        pos = pos.createNextFromSANs("3..Rh7")
        assertFalse(pos.hasCastlingRight(BLACK_SHORT))
        assertTrue(pos.hasCastlingRight(BLACK_LONG))
        pos = fromFEN("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1")
        assertTrue(pos.hasCastlingRight(BLACK_SHORT))
        assertTrue(pos.hasCastlingRight(BLACK_LONG))
        pos = pos.createNextFromSAN("Rxh8")
        assertFalse(pos.hasCastlingRight(WHITE_SHORT))
        assertFalse(pos.hasCastlingRight(BLACK_SHORT))
        assertTrue(pos.hasCastlingRight(BLACK_LONG))
        pos = fromFEN(FEN_STARTPOSITION)
        pos = pos.createNextFromSANs("1.e4 e5 2.Nf3 Nc6 3.Bc4 d6 4.0-0")
        assertFalse(pos.hasCastlingRight(WHITE_SHORT))
        assertFalse(pos.hasCastlingRight(WHITE_LONG))
        assertTrue(pos.hasCastlingRight(BLACK_SHORT))
        assertTrue(pos.hasCastlingRight(BLACK_LONG))
        pos = pos.createNextFromSANs("4...Bg4 5.h3 Qe7 6.a3 0-0-0")
        assertTrue(pos.castlingRight.isEmpty())
    }

    @Test
    fun enPassantField() {
        var pos = fromFEN(FEN_STARTPOSITION)
        pos = pos.createNextFromSANs("1.e4 d5 2.Nf3 d4")
        assertTrue(pos.enPassantField == null)
        pos = pos.createNextFromSANs("3.c4")
        assertTrue(pos.enPassantField == "c3")
    }

    @Test
    fun halfMoveCount() {
        var pos = fromFEN(FEN_STARTPOSITION)
        assertTrue(pos.halfMoveCount == 0)
        pos = pos.createNextFromSANs("1.e4 e5 2.Nf3")
        assertTrue(pos.halfMoveCount == 1)
        pos = pos.createNextFromSANs("2...Nf6")
        assertTrue(pos.halfMoveCount == 2)
        pos = pos.createNextFromSANs("3.Bc4")
        assertTrue(pos.halfMoveCount == 3)
        pos = pos.createNextFromSANs("3...Bc5 4.0-0")
        assertTrue(pos.halfMoveCount == 5)
        pos = pos.createNextFromSANs("4...d6")
        assertTrue(pos.halfMoveCount == 0)
    }

    @Test
    fun moveNumber() {
        var pos = fromFEN(FEN_STARTPOSITION)
        assertTrue(pos.moveNumber == 0)
        pos = pos.createNextFromSAN("e4")
        assertTrue(pos.moveNumber == 1)
        pos = pos.createNextFromSAN("c5")
        assertTrue(pos.moveNumber == 1)
        pos = pos.createNextFromSAN("Nf3")
        assertTrue(pos.moveNumber == 2)
        pos = pos.createNextFromSAN("d6")
        assertTrue(pos.moveNumber == 2)
    }

    @Test
    fun promotion() {
        var pos = fromFEN("4k3/P7/8/8/8/8/8/4K3 w - - 0 1")
        pos = pos.createNextFromSAN("a8=Q")
        assertTrue(pos.square("a8").piece == Piece.WHITE_QUEEN)

        pos = fromFEN("4k3/P7/8/8/8/8/8/4K3 w - - 0 1")
        pos = pos.createNextFromSAN("a8Q")
        assertTrue(pos.square("a8").piece == Piece.WHITE_QUEEN)
    }

    @Test
    fun enPassant() {
        var pos = fromFEN(FEN_STARTPOSITION)
        pos = pos.createNextFromSANs("1.e4 d5 2.Nf3 d4 3.c4 dxc3")
        assertTrue(pos.square("c3").piece == Piece.BLACK_PAWN)
        assertTrue(pos.square("c4").isEmpty)
        assertTrue(pos.square("d4").isEmpty)
    }

    @Test
    fun nullMove() {
        var pos = fromFEN(FEN_STARTPOSITION)
        pos = pos.createNextFromSANs("1.e4 d5 2.-- d4 3.c4 dxc3")
        assertTrue(pos.square("c3").piece == Piece.BLACK_PAWN)
    }

}

fun Position.createNextFromSANs(moves: String): Position {
    var result = this
    for (move in moves.split(" ")) {
        result = result.createNextFromSAN(move.removeMoveNumber())
    }
    return result
}

private fun String.removeMoveNumber() = substringAfterLast('.')
