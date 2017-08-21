package de.toto.crt.game.rules

import de.toto.crt.game.Position
import de.toto.crt.game.rules.Piece.PieceType.*

/**
 * Returns a list of `Square`s where in this `Position` the `Piece` on `square` can legally move to or capture on.
 * Returns an empty list if `square` is empty or the `Piece` can not move or capture at all.
 */
fun Position.legalMovesFrom(square: Square): List<Square> {
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
 * With a `KING` on `from`, which squares can he move to or capture on?
 */
private fun Position.kingMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()
    with (from) {
        trySquare(this, rank + 1, file, result)
        trySquare(this, rank + 1, file - 1, result)
        trySquare(this, rank + 1, file + 1, result)
        trySquare(this, rank, file - 1, result)
        trySquare(this, rank, file + 1, result)
        trySquare(this, rank - 1, file, result)
        trySquare(this, rank - 1, file - 1, result)
        trySquare(this, rank - 1, file + 1, result)
    }

    // try castling
    fun checkCastlingPossible(intermediateSquare: Square, kingsTargetSquare: Square) {
        if (emptyAndUnattacked(!whiteToMove, intermediateSquare, kingsTargetSquare)) {
            trySquare(from, kingsTargetSquare, result)
        }
    }
    if (!squareIsAttackedBy(from, !whiteToMove)) {
        if (hasCastlingRight(if (whiteToMove) CastlingRight.WHITE_SHORT else CastlingRight.BLACK_SHORT)) {
            checkCastlingPossible(square(from.rank, 6), square(from.rank, 7))
        }
        if (hasCastlingRight(if (whiteToMove) CastlingRight.WHITE_LONG else CastlingRight.BLACK_LONG)) {
            checkCastlingPossible(square(from.rank, 4), square(from.rank, 3))
        }
    }
    return result
}

private fun Position.emptyAndUnattacked(byWhite: Boolean, vararg squares: Square) =
        squares.all { it.isEmpty && !squareIsAttackedBy(it, byWhite) }

/**
 * With a `ROOK` on `from`, which squares can he move to or capture on?
 */
private fun Position.rookMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()

    // up
    trySquares(from, 0, 1, result)
    // down
    trySquares(from, 0, -1, result)
    // right
    trySquares(from, 1, 0, result)
    // left
    trySquares(from, -1, 0, result)

    return result
}

/**
 * With a `BISHOP` on `from`, which squares can he move to or capture on?
 */
private fun Position.bishopMovesFrom(from: Square): List<Square> {
    val result = mutableListOf<Square>()

    // up-right
    trySquares(from, 1, 1, result)
    // up-left
    trySquares(from,1, -1, result)
    // down-right
    trySquares(from, -1, 1, result)
    // down-left
    trySquares(from,-1, -1, result)

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
        trySquare(from, rank + 2, file + 1, result)
        trySquare(from, rank + 2, file - 1, result)
        trySquare(from, rank + 1, file + 2, result)
        trySquare(from, rank + 1, file - 2, result)
        trySquare(from, rank - 1, file + 2, result)
        trySquare(from, rank - 1, file - 2, result)
        trySquare(from, rank - 2, file + 1, result)
        trySquare(from, rank - 2, file - 1, result)
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
            trySquare(from, this, result)
            if (from.rank == startRank) {
                // try move two squares
                trySquare(from, square(if (whiteToMove) from.rank + 2 else from.rank - 2, from.file), result)
            }
        }
    }
    // try normal or e.p. captures
    val epField = if (enPassantField != null) square(enPassantField) else null
    fun checkSquare(square: Square) {
        if (square.hasPieceOfColor(!whiteToMove) || (square == epField && square.isEmpty)) {
            trySquare(from, square, result, square == epField)
        }
    }
    if (from.file > 1) checkSquare(square(advanceOneRank(from, whiteToMove), from.file - 1))
    if (from.file < 8) checkSquare(square(advanceOneRank(from, whiteToMove), from.file + 1))
    return result
}

private fun Square.hasPieceOfColor(white: Boolean) =  piece?.isWhite == white

fun advanceOneRank(from: Square, white: Boolean) = if (white) from.rank + 1 else from.rank - 1

/**
 * Adds `square` to `list` unless the square is occupied by one of our own pieces
 * and unless the resulting position would leave our king in check
 */
private fun Position.trySquare(from: Square, to: Square, list: MutableList<Square>, isEnPassant: Boolean = false) {
    if (to.hasPieceOfColor(whiteToMove)) return

    val undoMoves = object {
        val squares = mutableMapOf<Square, Piece?>()
        fun add(vararg s: Square) = s.forEach { squares.put(it, it.piece) }
    }

    undoMoves.add(from, to)
    // temporarily do the move
    to.piece = from.piece
    from.piece = null
    if (isEnPassant) {
        with (square(to.rank + if (whiteToMove) -1 else 1, to.file)) {
            undoMoves.add(this)
            this.piece = null
        }
    }
    try {
        // check if the resulting position is legal
        val king = Piece.get(KING, whiteToMove)
        if (!squareIsAttackedBy(findSquare { it.piece == king }, !whiteToMove)) {
            // move is possible and legal - add it to the list
            list.add(to)
        }
    } finally {
        // restore original position
        undoMoves.squares.forEach { it.key.piece = it.value }
    }
}

/**
 * Returns `trySquare(from, square, list)`.
 * Returns `null` if `rank` or `file` are invalid
 */
private fun Position.trySquare(from: Square, toRank: Int, toFile: Int, list: MutableList<Square>) {
    if (toRank !in 1..8 || toFile !in 1..8) return
    trySquare(from, square(toRank, toFile), list)
}

/**
 * Tries to add squares to the `list` of legal moves while looping over the incremented rank and file
 * of the `from` square.
 */
private fun Position.trySquares(from: Square, rankIncr: Int, fileIncr: Int, list: MutableList<Square>) {
    var rank = from.rank + rankIncr
    var file = from.file + fileIncr
    while (rank in 1..8 && file in 1..8) {
        with(square(rank, file)) {
            trySquare(from, this, list)
            if (!this.isEmpty) {
                return
            }
        }
        rank += rankIncr
        file += fileIncr
    }
}
