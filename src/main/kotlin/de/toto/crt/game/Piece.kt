package de.toto.crt.game

/**
 * Enumeration of all chess pieces, including their associated FEN and PGN characters.
 */
enum class Piece (val type: PieceType, val color: PieceColor, val fenChar: Char, val pgnChar: Char) {

    WHITE_KING(PieceType.KING, PieceColor.WHITE, 'K', 'K'),
    WHITE_QUEEN(PieceType.QUEEN, PieceColor.WHITE, 'Q', 'Q'),
    WHITE_ROOK(PieceType.ROOK, PieceColor.WHITE, 'R', 'R'),
    WHITE_BISHOP(PieceType.BISHOP, PieceColor.WHITE, 'B', 'B'),
    WHITE_KNIGHT(PieceType.KNIGHT, PieceColor.WHITE, 'N', 'N'),
    WHITE_PAWN(PieceType.PAWN, PieceColor.WHITE, 'P', ' '),
    BLACK_KING(PieceType.KING, PieceColor.BLACK, 'k', 'K'),
    BLACK_QUEEN(PieceType.QUEEN, PieceColor.BLACK, 'q', 'Q'),
    BLACK_ROOK(PieceType.ROOK, PieceColor.BLACK, 'r', 'R'),
    BLACK_BISHOP(PieceType.BISHOP, PieceColor.BLACK, 'b', 'B'),
    BLACK_KNIGHT(PieceType.KNIGHT, PieceColor.BLACK, 'n', 'N'),
    BLACK_PAWN(PieceType.PAWN, PieceColor.BLACK, 'p', ' ');

    val isWhite = color == PieceColor.WHITE

    enum class PieceType {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    enum class PieceColor {
        WHITE, BLACK, NONE
    }

    val figurine: String
        get() {
            when (type) {
                PieceType.KING -> return if (isWhite) "\u265A" else "\u2654"
                PieceType.QUEEN -> return if (isWhite) "\u265B" else "\u2655"
                PieceType.ROOK -> return if (isWhite) "\u265C" else "\u2656"
                PieceType.BISHOP -> return if (isWhite) "\u265D" else "\u2657"
                PieceType.KNIGHT -> return if (isWhite) "\u265E" else "\u2658"
                PieceType.PAWN -> return if (isWhite) "\u265F" else "\u2659"
            }
        }

    companion object {
        @JvmStatic
        fun getPieceByFenChar(fenChar: Char) = Piece.values().first { it.fenChar == fenChar }

        @JvmStatic
        fun getPieceByPGNCharAndColor(pgnChar: Char, white: Boolean) =
                Piece.values().first { it.pgnChar == pgnChar && it.isWhite == white }
    }
}

