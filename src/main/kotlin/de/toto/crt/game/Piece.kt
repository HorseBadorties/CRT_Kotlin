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
                PieceType.KING -> return WHITE_KING.coloredFigurine
                PieceType.QUEEN -> return WHITE_QUEEN.coloredFigurine
                PieceType.ROOK -> return WHITE_ROOK.coloredFigurine
                PieceType.BISHOP -> return WHITE_BISHOP.coloredFigurine
                PieceType.KNIGHT -> return WHITE_KNIGHT.coloredFigurine
                PieceType.PAWN -> return WHITE_PAWN.coloredFigurine
            }
        }

    val coloredFigurine: String
        get() {
            when (type) {
                PieceType.KING -> return if (isWhite) "\u2654" else "\u265A"
                PieceType.QUEEN -> return if (isWhite) "\u2655" else "\u265B"
                PieceType.ROOK -> return if (isWhite) "\u2656" else "\u265C"
                PieceType.BISHOP -> return if (isWhite) "\u2657" else "\u265D"
                PieceType.KNIGHT -> return if (isWhite) "\u2658" else "\u265E"
                PieceType.PAWN -> return if (isWhite) "\u2659" else "\u265F"
            }
        }

    companion object {
        @JvmStatic
        fun getPieceByFenChar(fenChar: Char) = Piece.values().first { it.fenChar == fenChar }
    }
}

