package de.toto.crt.game

import de.toto.crt.game.Piece.PieceType.*

/**
 * Is any Piece of color `byWhitePieces` attacking the `square`?
 */
fun Position.isSquareAttacked(square: Square, byWhitePieces: Boolean): Boolean {
    for (squareWithPiece in getPiecesByColor(byWhitePieces)) {
        when (squareWithPiece.piece?.type) {
            KING -> if (kingAttacks(squareWithPiece, square)) return true
            QUEEN -> if (queenAttacks(squareWithPiece, square)) return true
            ROOK -> if (rookAttacks(squareWithPiece, square)) return true
            BISHOP -> if (bishopAttacks(squareWithPiece, square)) return true
            KNIGHT -> if (knightAttacks(squareWithPiece, square)) return true
            PAWN -> if (pawnAttacks(byWhitePieces, squareWithPiece, square)) return true
        }
    }
    return false
}

/**
 * With a KING on square does he attack the other Square?
 */
fun Position.kingAttacks(square: Square, other: Square): Boolean {
    return with (square) {
        other.match(rank + 1, file) ||
                other.match(rank + 1, file - 1) ||
                other.match(rank + 1, file + 1) ||
                other.match(rank, file - 1) ||
                other.match(rank, file + 1) ||
                other.match(rank - 1, file) ||
                other.match(rank - 1, file - 1) ||
                other.match(rank - 1, file + 1)
    }
}

/**
 * With a ROOK on square does he attack the other Square?
 */
fun Position.rookAttacks(square: Square, other: Square): Boolean {
    // up
    if (square.rank < other.rank && square.file == other.file) {
        for (_rank in (square.rank + 1)..8) {
            val s = square(_rank, square.file)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // down
    if (square.rank > other.rank && square.file == other.file) {
        for (_rank in square.rank - 1 downTo 1) {
            val s = square(_rank, square.file)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // right
    if (square.file < other.file && square.rank == other.rank) {
        for (_file in (square.file + 1)..8) {
            val s = square(square.rank, _file)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // left
    if (square.file > other.file && square.rank == other.rank) {
        for (_file in square.file - 1 downTo 1) {
            val s = square(square.rank, _file)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    return false
}

/**
 * With a BISHOP on square does he attack the other Square?
 */
fun Position.bishopAttacks(square: Square, other: Square): Boolean {
    // up-right
    if (square.rank < other.rank && square.file < other.file) {
        var rank = square.rank + 1
        var file = square.file + 1
        while (rank <= 8 && file <= 8) {
            val s = square(rank++, file++)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // up-left
    if (square.rank < other.rank && square.file > other.file) {
        var rank = square.rank + 1
        var file = square.file - 1
        while (rank <= 8 && file >= 1) {
            val s = square(rank++, file--)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // down-right
    if (square.rank > other.rank && square.file < other.file) {
        var rank = square.rank - 1
        var file = square.file + 1
        while (rank >= 1 && file <= 8) {
            val s = square(rank--, file++)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // down-left
    if (square.rank > other.rank && square.file > other.file) {
        var rank = square.rank - 1
        var file = square.file - 1
        while (rank >= 1 && file >= 1) {
            val s = square(rank--, file--)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    return false
}

/**
 * With a QUEEN on square does she attack the other Square?
 */
fun Position.queenAttacks(square: Square, other: Square): Boolean {
    return rookAttacks(square, other) || bishopAttacks(square, other)
}

/**
 * With a KNIGHT on square does it attack the other Square?
 */
fun Position.knightAttacks(square: Square, other: Square): Boolean {
    val otherRank = other.rank
    val otherFile = other.file
    if (square.rank + 2 == otherRank && square.file + 1 == otherFile) return true
    if (square.rank + 2 == otherRank && square.file - 1 == otherFile) return true
    if (square.rank + 1 == otherRank && square.file + 2 == otherFile) return true
    if (square.rank + 1 == otherRank && square.file - 2 == otherFile) return true
    if (square.rank - 1 == otherRank && square.file + 2 == otherFile) return true
    if (square.rank - 1 == otherRank && square.file - 2 == otherFile) return true
    if (square.rank - 2 == otherRank && square.file + 1 == otherFile) return true
    if (square.rank - 2 == otherRank && square.file - 1 == otherFile) return true
    return false
}

/**
 * With a PAWN on square does he attack the other Square?
 */
fun Position.pawnAttacks(isWhitePawn: Boolean, square: Square, other: Square): Boolean {
    val rank = square.rank + if (isWhitePawn) 1 else -1
    return other.rank == rank
            && (other.file == square.file + 1 || other.file == square.file - 1)
}

private fun Square.match(_rank: Int, _file: Int) = rank == _rank && file == _file