package de.toto.crt.game.rules

import de.toto.crt.game.Position

// https://en.wikipedia.org/wiki/Forsyth-Edwards_Notation

const val FEN_STARTPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
const val FEN_EMPTY_BOARD = "8/8/8/8/8/8/8/8 w KQkq - 0 1"

/**
 * Constructs a Position according to the provided `fen`
 */
fun fromFEN(fen: String): Position {
    val fenFields = fen.trim().split(" ")
    try {
        val whiteToMove = fenFields[1] == "w"
        var movenumber = fenFields[5].toInt()
        if (!whiteToMove || fen == FEN_STARTPOSITION) movenumber--
        with (Position(move = "",
                whiteToMove = whiteToMove,
                enPassantField = if (fenFields[3] == "-") null else fenFields[3],
                halfMoveCount = fenFields[4].toInt(),
                moveNumber = movenumber,
                previous = null)) {
            // Set up Pieces
            var rank = 8; var file = 1
            for (fenChar in fenFields[0]) {
                if ('/' == fenChar) {
                    rank--
                    file = 1
                } else if (fenChar.isDigit()) {
                    file += fenChar.toString().toInt()
                } else {
                    square(rank, file).piece = Piece.getPieceByFenChar(fenChar)
                    file++
                }
            }
            // Castling
            castlingRightsFromFEN(fenFields[2])
            return this
        }
    } catch (ex: Exception) {
        throw IllegalArgumentException("failed to parse FEN: $fen", ex)
    }
}

/**
 * returns the FEN of this position
 */
fun Position.toFEN(): String {
    val fenMoveNumber = if (moveNumber == 0 || whiteToMove) moveNumber + 1 else moveNumber
    return "${squaresToFEN()} ${if (whiteToMove) "w" else "b"} ${castlingRightsToFEN()} " +
        "${enPassantField ?: "-"} $halfMoveCount $fenMoveNumber"
}

fun Position.squaresToFEN(): String {
    val result = StringBuilder()
    for (rank in 8 downTo 1) {
        var emptySquareCounter = 0
        for (file in 1..8) {
            val piece = square(rank, file).piece
            if (piece != null) {
                if (emptySquareCounter > 0) result.append(emptySquareCounter)
                emptySquareCounter = 0
                result.append(piece.fenChar)
            } else emptySquareCounter++
        }
        if (emptySquareCounter > 0) result.append(emptySquareCounter)
        if (rank > 1) result.append("/")
    }
    return result.toString()
}

private fun Position.castlingRightsToFEN(): String {
    with (StringBuilder()) {
        if (hasCastlingRight(CastlingRight.WHITE_SHORT)) append("K")
        if (hasCastlingRight(CastlingRight.WHITE_LONG)) append("Q")
        if (hasCastlingRight(CastlingRight.BLACK_SHORT)) append("k")
        if (hasCastlingRight(CastlingRight.BLACK_LONG)) append("q")
        return if (isEmpty()) "-" else toString()
    }

}

private fun Position.castlingRightsFromFEN(fenString: String) {
    val rights = mutableListOf<CastlingRight>()
    if (fenString.contains("K")) rights.add(CastlingRight.WHITE_SHORT)
    if (fenString.contains("Q")) rights.add(CastlingRight.WHITE_LONG)
    if (fenString.contains("k")) rights.add(CastlingRight.BLACK_SHORT)
    if (fenString.contains("q")) rights.add(CastlingRight.BLACK_LONG)
    setCastlingRights(*rights.toTypedArray())
}
