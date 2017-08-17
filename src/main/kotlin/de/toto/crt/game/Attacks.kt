package de.toto.crt.game

import de.toto.crt.game.Piece.PieceType.*

/**
 * Is any Piece of color `byWhitePieces` attacking the `square`?
 */
fun Position.isAttacked(square: Square, byWhitePieces: Boolean): Boolean {
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
private fun Position.kingAttacks(square: Square, other: Square): Boolean {
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

private fun Position.checkSquares(square: Square, other: Square, rankIncr: Int, fileIncr: Int): Boolean {
    var rank = square.rank + rankIncr
    var file = square.file + fileIncr
    while (rank in 1..8 && file in 1..8) {
        val s = square(rank, file)
        if (other == s) return true
        if (!s.isEmpty) break
        rank += rankIncr
        file += fileIncr
    }
    return false
}

/**
 * With a ROOK on square does he attack the other Square?
 */
private fun Position.rookAttacks(square: Square, other: Square): Boolean {
    // up
    if (square.rank < other.rank && square.file == other.file) {
        if (checkSquares(square, other, 1, 0)) return true
    }
    // down
    if (square.rank > other.rank && square.file == other.file) {
        if (checkSquares(square, other, -1, 0)) return true
    }
    // right
    if (square.file < other.file && square.rank == other.rank) {
        if (checkSquares(square, other, 0, 1)) return true
    }
    // left
    if (square.file > other.file && square.rank == other.rank) {
        if (checkSquares(square, other, 0, -1)) return true
    }
    return false
}
/**
 * With a BISHOP on square does he attack the other Square?
 */
private fun Position.bishopAttacks(square: Square, other: Square): Boolean {
    // up-right
    if (square.rank < other.rank && square.file < other.file) {
        if (checkSquares(square, other, 1, 1)) return true
    }
    // up-left
    if (square.rank < other.rank && square.file > other.file) {
        if (checkSquares(square, other, 1, -1)) return true
    }
    // down-right
    if (square.rank > other.rank && square.file < other.file) {
        if (checkSquares(square, other, -1, 1)) return true
    }
    // down-left
    if (square.rank > other.rank && square.file > other.file) {
        if (checkSquares(square, other, -1, -1)) return true
    }
    return false
}

/**
 * With a QUEEN on square does she attack the other Square?
 */
private fun Position.queenAttacks(square: Square, other: Square): Boolean {
    return rookAttacks(square, other) || bishopAttacks(square, other)
}

/**
 * With a KNIGHT on square does it attack the other Square?
 */
private fun Position.knightAttacks(square: Square, other: Square): Boolean {
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

fun Position.getPiecesByPiece(piece: Piece) = squares().filter { !it.isEmpty && it.piece == piece }

fun Position.getPiecesByColor(white: Boolean) =
        squares().filter { !it.isEmpty && it.piece?.isWhite == white }

/**
 * With a PAWN on square does he attack the other Square?
 */
private fun Position.pawnAttacks(isWhitePawn: Boolean, square: Square, other: Square): Boolean {
    val rank = square.rank + if (isWhitePawn) 1 else -1
    return other.rank == rank
            && (other.file == square.file + 1 || other.file == square.file - 1)
}

private fun Square.match(_rank: Int, _file: Int) = rank == _rank && file == _file