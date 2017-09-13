package de.toto.crt.game.rules

import de.toto.crt.game.rules.Piece.PieceColor.BLACK
import de.toto.crt.game.rules.Piece.PieceColor.WHITE
import de.toto.crt.game.rules.Piece.PieceType.*

/**
 * Enumeration of all chess pieces, including their associated FEN and PGN characters.
 */
enum class Piece (val type: PieceType, val color: PieceColor, val fenChar: Char, val pgnChar: Char) {

    WHITE_KING      (KING, WHITE, 'K', 'K'),
    WHITE_QUEEN     (QUEEN, WHITE, 'Q', 'Q'),
    WHITE_ROOK      (ROOK, WHITE, 'R', 'R'),
    WHITE_BISHOP    (BISHOP, WHITE, 'B', 'B'),
    WHITE_KNIGHT    (KNIGHT, WHITE, 'N', 'N'),
    WHITE_PAWN      (PAWN, WHITE, 'P', ' '),
    BLACK_KING      (KING, BLACK, 'k', 'K'),
    BLACK_QUEEN     (QUEEN, BLACK, 'q', 'Q'),
    BLACK_ROOK      (ROOK, BLACK, 'r', 'R'),
    BLACK_BISHOP    (BISHOP, BLACK, 'b', 'B'),
    BLACK_KNIGHT    (KNIGHT, BLACK, 'n', 'N'),
    BLACK_PAWN      (PAWN, BLACK, 'p', ' ');

    val isWhite = color == WHITE

    enum class PieceType {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    enum class PieceColor {
        WHITE, BLACK, NONE
    }

    val figurine: String
        get() = when (type) {
            KING -> if (isWhite) "\u2654" else "\u265A"
            QUEEN -> if (isWhite) "\u2655" else "\u265B"
            ROOK -> if (isWhite) "\u2656" else "\u265C"
            BISHOP -> if (isWhite) "\u2657" else "\u265D"
            KNIGHT -> if (isWhite) "\u2658" else "\u265E"
            PAWN -> if (isWhite) "\u2659" else "\u265F"
        }


    companion object {
        @JvmStatic
        fun getPieceByFenChar(fenChar: Char) = Piece.values().first { it.fenChar == fenChar }

        @JvmStatic
        fun getPieceByPGNCharAndColor(pgnChar: Char, white: Boolean) =
                Piece.values().firstOrNull { it.pgnChar == pgnChar && it.isWhite == white }

        @JvmStatic
        fun get(type: PieceType, white: Boolean) =
                Piece.values().first { it.type == type && it.isWhite == white }
    }
}

