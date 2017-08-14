package de.toto.crt.game

import de.toto.crt.game.CastlingRight.*

enum class CastlingRight {

    WHITE_SHORT, WHITE_LONG, BLACK_SHORT, BLACK_LONG;

    fun white() = this == WHITE_SHORT || this == WHITE_LONG
    fun shortCastle() = this == WHITE_SHORT || this == BLACK_SHORT

}

fun Position.hasCastlingRight(right: CastlingRight) = castlingRight.contains(right)

fun Position.setCastlingRights(vararg rights: CastlingRight) {
    castlingRight.clear()
    castlingRight.addAll(rights)
}

fun Position.checkCastleRights() {
    checkCastleRight(WHITE_SHORT)
    checkCastleRight(WHITE_LONG)
    checkCastleRight(BLACK_SHORT)
    checkCastleRight(BLACK_LONG)
}

private fun Position.checkCastleRight(right: CastlingRight) {
    if (hasCastlingRight(right)) {
        val white = right.white()
        val shortCastle = right.shortCastle()
        val rookSquare = square(backRank(white), if (shortCastle) 8 else 1)
        val kingSquare = square(backRank(white), 5)
        if (rookSquare.piece != Piece.get(Piece.PieceType.ROOK, white)) {
            castlingRight.remove(right)
        } else if (kingSquare.piece != Piece.get(Piece.PieceType.KING, white)) {
            castlingRight.remove(right)
        }
    }
}




