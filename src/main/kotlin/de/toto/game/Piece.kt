package de.toto.game

/**
 * Enumeration of all chess pieces, including their associated FEN and PGN characters.
 */
enum class Piece (val type: PieceType, val isWhite: Boolean, val fenChar: Char, val pgnChar: Char) {

    WHITE_KING(PieceType.KING, true, 'K', 'K'),
    WHITE_QUEEN(PieceType.QUEEN, true, 'Q', 'Q'),
    WHITE_ROOK(PieceType.ROOK, true, 'R', 'R'),
    WHITE_BISHOP(PieceType.BISHOP, true, 'B', 'B'),
    WHITE_KNIGHT(PieceType.KNIGHT, true, 'N', 'N'),
    WHITE_PAWN(PieceType.PAWN, true, 'P', ' '),
    BLACK_KING(PieceType.KING, false, 'k', 'K'),
    BLACK_QUEEN(PieceType.QUEEN, false, 'q', 'Q'),
    BLACK_ROOK(PieceType.ROOK, false, 'r', 'R'),
    BLACK_BISHOP(PieceType.BISHOP, false, 'b', 'B'),
    BLACK_KNIGHT(PieceType.KNIGHT, false, 'n', 'N'),
    BLACK_PAWN(PieceType.PAWN, false, 'p', ' ');

    enum class PieceType {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
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
}

fun getPieceByFenChar(fenChar: Char) = Piece.values().first { it.fenChar == fenChar }