package de.toto.crt.game

enum class CastlingRight { WHITE_SHORT, WHITE_LONG, BLACK_SHORT, BLACK_LONG }

fun Position.hasCastlingRight(right: CastlingRight) = castlingRight.contains(right)

fun Position.hasCastlingRight(shortCastles: Boolean, isWhite: Boolean): Boolean {
    if (isWhite) {
        if (shortCastles) return hasCastlingRight(CastlingRight.WHITE_SHORT)
        else return hasCastlingRight(CastlingRight.WHITE_LONG)
    } else {
        if (shortCastles) return hasCastlingRight(CastlingRight.BLACK_SHORT)
        else return hasCastlingRight(CastlingRight.BLACK_LONG)
    }
}

fun Position.defineCastlingRights(vararg rights: CastlingRight): Position {
    castlingRight.clear()
    castlingRight.addAll(rights)
    return this
}

fun Position.defineCastlingRights(fenString: String): Position {
    val rights = mutableListOf<CastlingRight>()
    if (fenString.contains("K")) rights.add(CastlingRight.WHITE_SHORT)
    if (fenString.contains("Q")) rights.add(CastlingRight.WHITE_LONG)
    if (fenString.contains("k")) rights.add(CastlingRight.BLACK_SHORT)
    if (fenString.contains("q")) rights.add(CastlingRight.BLACK_LONG)
    return defineCastlingRights(*rights.toTypedArray())
}

fun Position.removeCastlingRight(shortCastles: Boolean, isWhite: Boolean): Position {
    if (isWhite) {
        if (shortCastles) castlingRight.remove(CastlingRight.WHITE_SHORT)
        else castlingRight.remove(CastlingRight.WHITE_LONG)
    } else {
        if (shortCastles) castlingRight.remove(CastlingRight.BLACK_SHORT)
        else castlingRight.remove(CastlingRight.BLACK_LONG)
    }
    return this
}

private const val SHORT_CASTLES = true
private const val LONG_CASTLES = false

fun Position.checkCastleRights() {
    checkCastleRight(SHORT_CASTLES, true)
    checkCastleRight(SHORT_CASTLES, false)
    checkCastleRight(LONG_CASTLES, true)
    checkCastleRight(LONG_CASTLES, false)
}

private fun Position.checkCastleRight(shortCastles: Boolean, whiteToMove: Boolean) {
    if (hasCastlingRight(shortCastles, whiteToMove)) {
        val rookSquare = square(if (whiteToMove) 1 else 8, if (shortCastles) 8 else 1)
        val kingSquare = square(if (whiteToMove) 1 else 8, 5)
        if (rookSquare.piece != if (whiteToMove) Piece.WHITE_ROOK else Piece.BLACK_ROOK) {
            removeCastlingRight(shortCastles, whiteToMove)
        } else if (kingSquare.piece != if (whiteToMove) Piece.WHITE_KING else Piece.BLACK_KING) {
            removeCastlingRight(shortCastles, whiteToMove)
        }
    }
}



