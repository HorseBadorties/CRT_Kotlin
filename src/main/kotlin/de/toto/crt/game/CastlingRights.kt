package de.toto.crt.game

fun Position.castlingRights() = castlingRight.toTypedArray()

fun Position.hasCastleRight(right: Position.CastlingRight) = castlingRight.contains(right)

fun Position.hasCastleRight(shortCastles: Boolean, isWhite: Boolean): Boolean {
    if (isWhite) {
        if (shortCastles) return hasCastleRight(Position.CastlingRight.WHITE_SHORT)
        else return hasCastleRight(Position.CastlingRight.WHITE_LONG)
    } else {
        if (shortCastles) return hasCastleRight(Position.CastlingRight.BLACK_SHORT)
        else return hasCastleRight(Position.CastlingRight.BLACK_LONG)
    }
}

fun Position.defineCastleRights(vararg rights: Position.CastlingRight): Position {
    castlingRight.clear()
    castlingRight.addAll(rights)
    return this
}

fun Position.setCastleRights(vararg rights: Position.CastlingRight): Position {
    castlingRight.addAll(rights)
    return this
}

fun Position.removeCastleRights(vararg rights: Position.CastlingRight): Position {
    castlingRight.removeAll(rights)
    return this
}

