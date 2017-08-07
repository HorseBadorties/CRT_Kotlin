package de.toto.crt.game

/**
 * With a `KING` on `square`, which squares can he move to or capture on?
 */
fun Position.kingCanMoveTo(isWhite: Boolean, square: Square): List<Square> {
    val result = mutableListOf<Square>()
    with (square) {
        addSquare(isWhite, rank + 1, file, result)
        addSquare(isWhite, rank + 1, file - 1, result)
        addSquare(isWhite, rank + 1, file + 1, result)
        addSquare(isWhite, rank, file - 1, result)
        addSquare(isWhite, rank, file + 1, result)
        addSquare(isWhite, rank - 1, file, result)
        addSquare(isWhite, rank - 1, file - 1, result)
        addSquare(isWhite, rank - 1, file + 1, result)
    }
    TODO("Castling")
    return result
}

/**
 * With a `QUEEN` on `square`, which squares can she move to or capture on?
 */
fun Position.queenCanMoveTo(isWhite: Boolean, square: Square): List<Square> {
    return rookCanMoveTo(isWhite, square) + bishopCanMoveTo(isWhite, square)
}

/**
 * With a `ROOK` on `square`, which squares can he move to or capture on?
 */
fun Position.rookCanMoveTo(isWhite: Boolean, square: Square): List<Square> {
    val result = mutableListOf<Square>()
    // up
    for (_rank in (square.rank + 1)..8) {
        if (!addSquare(isWhite, square(_rank, square.file), result).isEmpty) break
    }
    // down
   for (_rank in square.rank - 1 downTo 1) {
       if (!addSquare(isWhite, square(_rank, square.file), result).isEmpty) break
   }
    // right
    for (_file in (square.file + 1)..8) {
        if (!addSquare(isWhite, square(square.rank, _file), result).isEmpty) break
    }
    // left
    for (_file in square.file - 1 downTo 1) {
        if (!addSquare(isWhite, square(square.rank, _file), result).isEmpty) break
    }
    return result
}

/**
 * With a `BISHOP` on `square`, which squares can he move to or capture on?
 */
fun Position.bishopCanMoveTo(isWhite: Boolean, square: Square): List<Square> {
    val result = mutableListOf<Square>()

    // up-right
    var rank = square.rank + 1
    var file = square.file + 1
    while (rank <= 8 && file <= 8) {
        if (!addSquare(isWhite, square(rank, file), result).isEmpty) break
    }
    // up-left
    rank = square.rank + 1
    file = square.file - 1
    while (rank <= 8 && file >= 1) {
        if (!addSquare(isWhite, square(rank, file), result).isEmpty) break
    }
    // down-right
    rank = square.rank - 1
    file = square.file + 1
    while (rank >= 1 && file <= 8) {
        if (!addSquare(isWhite, square(rank, file), result).isEmpty) break
    }
    // down-left
    rank = square.rank - 1
    file = square.file - 1
    while (rank >= 1 && file >= 1) {
        if (!addSquare(isWhite, square(rank, file), result).isEmpty) break
    }

    return result
}

/**
 * With a `KNIGHT` on `square`, which squares can he move to or capture on?
 */
fun Position.knightCanMoveTo(isWhite: Boolean, square: Square): List<Square> {
    val result = mutableListOf<Square>()

    with (square) {
        addSquare(isWhite, rank + 2, file + 1, result)
        addSquare(isWhite, rank + 2, file - 1, result)
        addSquare(isWhite, rank + 1, file + 2, result)
        addSquare(isWhite, rank + 1, file - 2, result)
        addSquare(isWhite, rank - 1, file + 2, result)
        addSquare(isWhite, rank - 1, file - 2, result)
        addSquare(isWhite, rank - 2, file + 1, result)
        addSquare(isWhite, rank - 2, file - 1, result)
    }

    return result
}

/**
 * With a `PAWN` on `square`, which squares can he move to or capture on?
 */
fun Position.pawnCanMoveTo(isWhitePawn: Boolean, square: Square): List<Square> {
    val result = mutableListOf<Square>()
    TODO()
    return result
}

/**
 * Adds `square` to `list` unless the square is occupied by one of our own pieces
 * and unless the resulting position would leave our king in check
 */
private fun Position.addSquare(isWhite: Boolean, square: Square, list: MutableList<Square>): Square {
    val piece = square.piece
    if (piece == null || piece.isWhite != isWhite) {
        // TODO our king in check?
        list.add(square)
    }
    return square
}

/**
 * Returns `addSquare(isWhite, square, list)`.
 * Returns `null` if `rank` or `file` are invalid
 */
private fun Position.addSquare(isWhite: Boolean, rank: Int, file: Int, list: MutableList<Square>): Square? {
    if (rank !in 1..8 || file !in 1..8) return null
    with (square(rank, file)) {
        return addSquare(isWhite, this, list)
    }
}
