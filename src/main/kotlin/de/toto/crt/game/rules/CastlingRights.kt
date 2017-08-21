package de.toto.crt.game.rules

import de.toto.crt.game.rules.CastlingRight.*
import de.toto.crt.game.Position
import de.toto.crt.game.rules.Piece.PieceType.*

enum class CastlingRight {

    WHITE_SHORT, WHITE_LONG, BLACK_SHORT, BLACK_LONG;

    val isWhite:Boolean  get() = this == WHITE_SHORT || this == WHITE_LONG
    val isShortCastle: Boolean get() = this == WHITE_SHORT || this == BLACK_SHORT

}

fun Position.hasCastlingRight(right: CastlingRight) = castlingRight.contains(right)

fun Position.setCastlingRights(vararg rights: CastlingRight) {
    castlingRight.clear()
    castlingRight.addAll(rights)
}

fun Position.checkCastleRights(): Position {
    checkCastleRight(WHITE_SHORT)
    checkCastleRight(WHITE_LONG)
    checkCastleRight(BLACK_SHORT)
    checkCastleRight(BLACK_LONG)
    return this
}

private fun Position.checkCastleRight(right: CastlingRight) {
    if (!hasCastlingRight(right)) return
    val rookSquare = square(backRank(right.isWhite), if (right.isShortCastle) 8 else 1)
    val kingSquare = square(backRank(right.isWhite), 5)
    when {
        rookSquare.piece != Piece.get(ROOK, right.isWhite) -> castlingRight.remove(right)
        kingSquare.piece != Piece.get(KING, right.isWhite) -> castlingRight.remove(right)
    }
}




