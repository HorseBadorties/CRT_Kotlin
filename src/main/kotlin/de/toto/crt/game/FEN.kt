package de.toto.crt.game

const val FEN_STARTPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"
const val FEN_EMPTY_BOARD = "8/8/8/8/8/8/8/8 w KQkq - 0 0"

/**
 * Constructs a Position according to the provided `fen`
 */
fun Position.Companion.fromFEN(fen: String): Position {
    val fenFields = fen.trim().split(" ")
    try {
        val epField = if (fenFields[3] == "-") null else fenFields[3]
        with(Position("", fenFields[1] == "w", epField, fenFields[4].toInt(), fenFields[5].toInt(), null)) {
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
            setCastlingRights(fenFields[2])
            return this
        }
    } catch (ex: Exception) {
        throw IllegalArgumentException("failed to parse FEN: " + fen, ex)
    }
}

fun Position.setCastlingRights(fenString: String) {
    val rights = mutableListOf<CastlingRight>()
    if (fenString.contains("K")) rights.add(CastlingRight.WHITE_SHORT)
    if (fenString.contains("Q")) rights.add(CastlingRight.WHITE_LONG)
    if (fenString.contains("k")) rights.add(CastlingRight.BLACK_SHORT)
    if (fenString.contains("q")) rights.add(CastlingRight.BLACK_LONG)
    setCastlingRights(*rights.toTypedArray())
}
