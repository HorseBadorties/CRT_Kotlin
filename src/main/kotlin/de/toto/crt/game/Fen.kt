package de.toto.crt.game

const val FEN_STARTPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
const val FEN_EMPTY_BOARD = "8/8/8/8/8/8/8/8 w KQkq - 0 1"

class FenValues(var whiteToMove: Boolean = true,
                var castlingRight: String = "",
                var enPassantField: String = "",
                var halfMoveCount: Int = 0,
                var moveNumber: Int = 0)

fun Position.setFen(fen: String): FenValues {
    val result = FenValues()
    val fenFields = fen.trim().split(" ")
    try {
        var rank = 8; var file = 1
        for (fenChar in fenFields[0]) {
            if ('/' == fenChar) {
                rank--
                file = 1
            } else if (fenChar.isDigit()) {
                file += fenChar.toInt()
            } else {
                square(rank, file).piece = Piece.getPieceByFenChar(fenChar)
                file++
            }
        }
        result.whiteToMove = fenFields[1] == "w"
        result.castlingRight = fenFields[2]
        result.enPassantField = fenFields[3]
        result.halfMoveCount = fenFields[4].toInt()
        result.moveNumber = fenFields[5].toInt()
        return result
    } catch (ex: Exception) {
        throw IllegalArgumentException("failed to parse FEN: " + fen, ex)
    }
}
