package de.toto.crt.game

import de.toto.crt.game.Piece.PieceType.*

fun Position.possibleMoves(square: Square): List<Square> {
    when (square.piece?.type) {
        KING -> return kingCanMoveTo(square)
        QUEEN -> return queenCanMoveTo(square)
        ROOK -> return rookCanMoveTo(square)
        BISHOP -> return bishopCanMoveTo(square)
        KNIGHT -> return knightCanMoveTo(square)
        PAWN -> return pawnCanMoveTo(square)
    }
    return emptyList()
}

fun Position.possibleMoves(squareName: String) = possibleMoves(square(squareName))

/**
 * With a `KING` on `from`, which squares can he move to or capture on?
 */
private fun Position.kingCanMoveTo(from: Square): List<Square> {
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
    val isWhite = from.piece!!.isWhite
    if (!isSquareAttacked(from, !isWhite) && hasCastleRight(true, isWhite)) {
        val fSquare = square(from.rank, 6)
        val gSquare = square(from.rank, 7)
        if (fSquare.isEmpty && !isSquareAttacked(fSquare, !isWhite)
                && gSquare.isEmpty && !isSquareAttacked(gSquare, !isWhite))
        {
            addSquare(from, gSquare, result)
        }
    }
    if (hasCastleRight(false, isWhite)) {
        val dSquare = square(from.rank, 4)
        val cSquare = square(from.rank, 3)
        if (dSquare.isEmpty && !isSquareAttacked(dSquare, !isWhite)
                && cSquare.isEmpty && !isSquareAttacked(cSquare, !isWhite))
        {
            addSquare(from, cSquare, result)
        }
    }
    return result
}

/**
 * With a `QUEEN` on `from`, which squares can she move to or capture on?
 */
private fun Position.queenCanMoveTo(from: Square): List<Square> {
    return rookCanMoveTo(from) + bishopCanMoveTo(from)
}

/**
 * With a `ROOK` on `from`, which squares can he move to or capture on?
 */
private fun Position.rookCanMoveTo(from: Square): List<Square> {
    val result = mutableListOf<Square>()
    // up
    for (_rank in (from.rank + 1)..8) {
        if (!addSquare(from, square(_rank, from.file), result).isEmpty) break
    }
    // down
   for (_rank in from.rank - 1 downTo 1) {
       if (!addSquare(from, square(_rank, from.file), result).isEmpty) break
   }
    // right
    for (_file in (from.file + 1)..8) {
        if (!addSquare(from, square(from.rank, _file), result).isEmpty) break
    }
    // left
    for (_file in from.file - 1 downTo 1) {
        if (!addSquare(from, square(from.rank, _file), result).isEmpty) break
    }
    return result
}

/**
 * With a `BISHOP` on `from`, which squares can he move to or capture on?
 */
private fun Position.bishopCanMoveTo(from: Square): List<Square> {
    val result = mutableListOf<Square>()

    // up-right
    var rank = from.rank
    var file = from.file
    while (++rank <= 8 && ++file <= 8) {
        if (!addSquare(from, square(rank, file), result).isEmpty) break
    }
    // up-left
    rank = from.rank
    file = from.file
    while (++rank <= 8 && --file >= 1) {
        if (!addSquare(from, square(rank, file), result).isEmpty) break
    }
    // down-right
    rank = from.rank
    file = from.file
    while (--rank >= 1 && ++file <= 8) {
        if (!addSquare(from, square(rank, file), result).isEmpty) break
    }
    // down-left
    rank = from.rank
    file = from.file
    while (--rank >= 1 && --file >= 1) {
        if (!addSquare(from, square(rank, file), result).isEmpty) break
    }

    return result
}

/**
 * With a `KNIGHT` on `from`, which squares can he move to or capture on?
 */
private fun Position.knightCanMoveTo(from: Square): List<Square> {
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
private fun Position.pawnCanMoveTo(from: Square): List<Square> {
    val result = mutableListOf<Square>()
    val isWhite = from.piece!!.isWhite
    val startRank = if (isWhite) 2 else 7
    // try move one square
    val squareInFront = square(if (isWhite) from.rank + 1 else from.rank - 1, from.file)
    addSquare(from, squareInFront, result)
    if (from.rank == startRank && squareInFront.isEmpty) {
        // try move two squares
        addSquare(from, square(if (isWhite) from.rank + 2 else from.rank - 2, from.file), result)
    }
    // try normal or e.p. capture
    TODO()
    return result
}

/**
 * Adds `square` to `list` unless the square is occupied by one of our own pieces
 * and unless the resulting position would leave our king in check
 */
private fun Position.addSquare(from: Square, to: Square, list: MutableList<Square>): Square {
    val toPiece = to.piece
    val isWhite = from.piece!!.isWhite
    if (toPiece == null || toPiece.isWhite != isWhite) {
        to.piece = from.piece
        from.piece = null
        val kingsSquare = getPiecesByPiece(if (isWhite) Piece.WHITE_KING else Piece.BLACK_KING).first()
        if (!isSquareAttacked(kingsSquare, !isWhite)) {
            list.add(to)
        }
        from.piece = to.piece
        to.piece = toPiece
    }
    return to
}

/**
 * Returns `addSquare(isWhite, square, list)`.
 * Returns `null` if `rank` or `file` are invalid
 */
private fun Position.addSquare(from: Square, toRank: Int, toFile: Int, list: MutableList<Square>): Square? {
    if (toRank !in 1..8 || toFile !in 1..8) return null
    with (square(toRank, toFile)) {
        return addSquare(from, this, list)
    }
}
