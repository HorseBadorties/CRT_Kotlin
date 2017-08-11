package de.toto.crt.game

enum class CastlingRight { WHITE_SHORT, WHITE_LONG, BLACK_SHORT, BLACK_LONG }

fun Position.castlingRights() = castlingRight.toTypedArray()

fun Position.hasCastleRight(right: CastlingRight) = castlingRight.contains(right)

fun Position.hasCastleRight(shortCastles: Boolean, isWhite: Boolean): Boolean {
    if (isWhite) {
        if (shortCastles) return hasCastleRight(CastlingRight.WHITE_SHORT)
        else return hasCastleRight(CastlingRight.WHITE_LONG)
    } else {
        if (shortCastles) return hasCastleRight(CastlingRight.BLACK_SHORT)
        else return hasCastleRight(CastlingRight.BLACK_LONG)
    }
}

fun Position.defineCastleRights(vararg rights: CastlingRight): Position {
    castlingRight.clear()
    castlingRight.addAll(rights)
    return this
}

fun Position.defineCastleRights(fenString: String): Position {
    val rights = mutableListOf<CastlingRight>()
    if (fenString.contains("K")) rights.add(CastlingRight.WHITE_SHORT)
    if (fenString.contains("Q")) rights.add(CastlingRight.WHITE_LONG)
    if (fenString.contains("k")) rights.add(CastlingRight.BLACK_SHORT)
    if (fenString.contains("q")) rights.add(CastlingRight.BLACK_LONG)
    return defineCastleRights(*rights.toTypedArray())
}

fun Position.setCastleRights(vararg rights: CastlingRight): Position {
    castlingRight.addAll(rights)
    return this
}

fun Position.removeCastleRights(vararg rights: CastlingRight): Position {
    castlingRight.removeAll(rights)
    return this
}



