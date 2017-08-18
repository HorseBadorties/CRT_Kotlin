package de.toto.crt.game

import de.toto.crt.game.Piece.PieceType.*

/**
 * Returns a list of Squares where the Piece on `square` can legally move to or capture on.
 * Returns an empty list if `square` is empty or the Piece can not move or capture at all.
 */
fun Position.movesFrom(square: Square): List<Square> {
    when (square.piece?.type) {
        KING -> return kingMovesFrom(square)
        QUEEN -> return queenMovesFrom(square)
        ROOK -> return rookMovesFrom(square)
        BISHOP -> return bishopMovesFrom(square)
        KNIGHT -> return knightMovesFrom(square)
        PAWN -> return pawnMovesFrom(square)
    }
    return emptyList()
}

/**
 * Returns a list of Squares where the Piece on `square` can legally move to or capture on.
 * Returns an empty list if `square` is empty or the Piece can not move or capture at all.
 */
fun Position.movesFrom(squareName: String) = movesFrom(square(squareName))

/**
 * With a `KING` on `from`, which squares can he move to or capture on?
 */
private fun Position.kingMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()
    with (from) {
        addSquare(this, rank + 1, file, result)
        addSquare(this, rank + 1, file - 1, result)
        addSquare(this, rank + 1, file + 1, result)
        addSquare(this, rank, file - 1, result)
        addSquare(this, rank, file + 1, result)
        addSquare(this, rank - 1, file, result)
        addSquare(this, rank - 1, file - 1, result)
        addSquare(this, rank - 1, file + 1, result)
    }

    // try castling
    fun checkCastlingPossible(first: Square, second: Square) {
        if (emptyAndUnattacked(first, !whiteToMove) && emptyAndUnattacked(second, !whiteToMove)) {
            addSquare(from, second, result)
        }
    }
    if (!isAttacked(from, !whiteToMove)) {
        if (hasCastlingRight(if (whiteToMove) CastlingRight.WHITE_SHORT else CastlingRight.BLACK_SHORT)) {
            checkCastlingPossible(square(from.rank, 6), square(from.rank, 7))
        }
        if (hasCastlingRight((if (whiteToMove) CastlingRight.WHITE_LONG else CastlingRight.BLACK_LONG))) {
            checkCastlingPossible(square(from.rank, 4), square(from.rank, 3))
        }
    }
    return result
}

private fun Position.emptyAndUnattacked(square: Square, byWhite: Boolean) =
        square.isEmpty && !isAttacked(square, byWhite)

private fun Position.visitSquares(from: Square, rankIncr: Int, fileIncr: Int, list: MutableList<Square>) {
    var rank = from.rank + rankIncr
    var file = from.file + fileIncr
    while (rank in 1..8 && file in 1..8) {
        if (!addSquare(from, square(rank, file), list).isEmpty) break
        rank += rankIncr
        file += fileIncr
    }
}

/**
 * With a `ROOK` on `from`, which squares can he move to or capture on?
 */
private fun Position.rookMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()

    // up
    visitSquares(from, 0, 1, result)
    // down
    visitSquares(from, 0, -1, result)
    // right
    visitSquares(from, 1, 0, result)
    // left
    visitSquares(from, -1, 0, result)

    return result
}

/**
 * With a `BISHOP` on `from`, which squares can he move to or capture on?
 */
private fun Position.bishopMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()

    // up-right
    visitSquares(from, 1, 1, result)
    // up-left
    visitSquares(from,1, -1, result)
    // down-right
    visitSquares(from, -1, 1, result)
    // down-left
    visitSquares(from,-1, -1, result)

    return result
}

/**
 * With a `QUEEN` on `from`, which squares can she move to or capture on?
 */
private fun Position.queenMovesFrom(from: Square) = rookMovesFrom(from) + bishopMovesFrom(from)

/**
 * With a `KNIGHT` on `from`, which squares can he move to or capture on?
 */
private fun Position.knightMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()
    with (from) {
        addSquare(from, rank + 2, file + 1, result)
        addSquare(from, rank + 2, file - 1, result)
        addSquare(from, rank + 1, file + 2, result)
        addSquare(from, rank + 1, file - 2, result)
        addSquare(from, rank - 1, file + 2, result)
        addSquare(from, rank - 1, file - 2, result)
        addSquare(from, rank - 2, file + 1, result)
        addSquare(from, rank - 2, file - 1, result)
    }
    return result
}

/**
 * With a `PAWN` on `from`, which squares can he move to or capture on?
 */
private fun Position.pawnMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()
    val startRank = if (whiteToMove) 2 else 7
    // try move one square
    with (square(advanceOneRank(from, whiteToMove), from.file)) {
        if (this.isEmpty) {
            addSquare(from, this, result)
            if (from.rank == startRank) {
                // try move two squares
                addSquare(from, square(if (whiteToMove) from.rank + 2 else from.rank - 2, from.file), result)
            }
        }
    }
    // try normal or e.p. captures
    val epField = if (enPassantField != null) square(enPassantField) else null
    fun checkSquare(square: Square) {
        if (square.hasPieceOfColor(!whiteToMove) || (square == epField && square.isEmpty)) {
            addSquare(from, square, result)
        }
    }
    if (from.file > 1) {
        checkSquare(square(advanceOneRank(from, whiteToMove), from.file - 1))
    }
    if (from.file < 8) {
        checkSquare(square(advanceOneRank(from, whiteToMove), from.file + 1))
    }
    return result
}

private fun Square.hasPieceOfColor(white: Boolean) =  piece?.isWhite == white

fun advanceOneRank(from: Square, white: Boolean) = if (white) from.rank + 1 else from.rank - 1

/**
 * Adds `square` to `list` unless the square is occupied by one of our own pieces
 * and unless the resulting position would leave our king in check
 */
private fun Position.addSquare(from: Square, to: Square, list: MutableList<Square>): Square {
    val toPiece = to.piece
    if (toPiece == null || toPiece.isWhite != whiteToMove) {
        // temporarily move the piece to `to`
        to.piece = from.piece
        from.piece = null
        try {
            val kingsSquare = getPiecesByPiece(Piece.get(KING, whiteToMove)).first()
            if (!isAttacked(kingsSquare, !whiteToMove)) {
                list.add(to)
            }
        } finally {
            // restore original position
            from.piece = to.piece
            to.piece = toPiece
        }
    }
    return to
}

/**
 * Returns `addSquare(from, square, list)`.
 * Returns `null` if `rank` or `file` are invalid
 */
private fun Position.addSquare(from: Square, toRank: Int, toFile: Int, list: MutableList<Square>): Square? {
    if (toRank !in 1..8 || toFile !in 1..8) return null
    with (square(toRank, toFile)) {
        return addSquare(from, this, list)
    }
}
