package de.toto.crt.game

import de.toto.crt.game.Piece.PieceType.*

/**
 * Construct the next Position from a SAN move String, either `asMainline` (default) or as a new variation.
 */
fun Position.createNextFromSAN(san: String, asMainline: Boolean = true): Position {
    try {
        val result = parseSAN(san, asMainline)
        // add the new Position to our list of `next` Positions
        next.add(if (asMainline) 0 else next.size, result)
        return result
    } catch (ex: Exception) {
        throw IllegalArgumentException("failed to parse SAN $san as next move for $moveWithMovenumber", ex)
    }
}

private fun Position.parseSAN(san: String, asMainline: Boolean): Position {
    // handle null-move
    if (san == "--") {
        return createPosition(san, null, 0, asMainline).checkCastleRights()
    }
    // handle castling - consider both "0-0" (zeros) and "O-O" (Os)
    val isLongCastles = san.startsWith("0-0-0") || san.startsWith("O-O-O")
    if (isLongCastles || san.startsWith("0-0") || san.startsWith("O-O")) {
        return createPosition(san, null, halfMoveCount + 1, asMainline).castle(isLongCastles)
    }

    // we don't use/validate check/checkmate as of yet (?!)
    var move = san.removeSuffix("+").removeSuffix("#")
    // capture?
    val isCapture = move.contains('x')
    if (isCapture) move = move.replace("x", "")
    // promotion? Consider both "c8=Q" and "c8Q"
    val promotionPiece = Piece.getPieceByPGNCharAndColor(move.last(), whiteToMove)
    if (promotionPiece != null) {
        move = move.dropLast(1)
        if (move.last() == '=') move = move.dropLast(1)
    }
    // which piece moved?
    val piece = Piece.getPieceByPGNCharAndColor(move.first(), whiteToMove) ?: Piece.get(PAWN, whiteToMove)
    if (piece.type != PAWN) move = move.drop(1)
    // from where to where?
    val (fromSquare, toSquare) = getFromAndToSquares(move, piece, isCapture)

    val newEnPassantField = getEnPassantField(piece, fromSquare, toSquare)
    val newHalfMoveCount = if (piece.type == PAWN || isCapture) 0 else halfMoveCount + 1
    // we got all information - create the new Position
    val result = createPosition(san, newEnPassantField, newHalfMoveCount, asMainline)
    // do the actual move
    if (enPassantField == toSquare.name && isCapture && piece.type == PAWN) {
        result.doEnPassantMove(fromSquare, toSquare, piece)
    } else {
        result.doNormalMove(fromSquare, toSquare, piece, promotionPiece)
    }
    return result
}

// TODO cleanup code
private fun Position.getEnPassantField(piece: Piece, fromSquare: Square, toSquare: Square): String? {
    if (piece.type == PAWN && Math.abs(fromSquare.rank - toSquare.rank) == 2) {
        val opponentPawn = Piece.get(PAWN, !whiteToMove)
        if (toSquare.file > 1 && square(toSquare.rank, toSquare.file - 1).piece == opponentPawn
                || toSquare.file < 8 && square(toSquare.rank, toSquare.file + 1).piece == opponentPawn) {
            return square(advanceOneRank(toSquare, !whiteToMove), toSquare.file).name
        }
    }
    return null
}

// TODO cleanup code
private fun Position.getFromAndToSquares(move: String, piece: Piece, isCapture: Boolean): Pair<Square, Square> {
    val toSquare = square(move.takeLast(2))
    val from = move.dropLast(2)
    if (from.length == 2) return Pair(square(from), toSquare)
    val fromSquare = filterPieces { isFromSquare(it, from, piece, toSquare, isCapture) }.firstOrNull()
            ?: throw IllegalArgumentException("failed to identify the square the move originated from")
    return Pair(fromSquare, toSquare)
}

// use some heuristics before calling the "expensive" `legalMovesFrom`
private fun Position.isFromSquare(fromSquare: Square, from: String, piece: Piece, toSquare: Square, isCapture: Boolean) = when {
    fromSquare.piece != piece -> false
    fromSquare.piece?.type == PAWN && !isCapture && fromSquare.file != toSquare.file -> false
    fromSquare.piece?.type == PAWN && isCapture && Math.abs(fromSquare.file - toSquare.file) > 1 -> false
    fromSquare.piece?.type == BISHOP && fromSquare.isWhite != toSquare.isWhite -> false
    fromSquare.piece?.type == ROOK && fromSquare.rank != toSquare.rank && fromSquare.file != toSquare.file -> false
    fromSquare.piece?.type == KNIGHT && (Math.abs(fromSquare.rank - toSquare.rank) > 2 || Math.abs(fromSquare.file - toSquare.file) > 2) -> false
    (from.isEmpty() || fromSquare.name.contains(from)) && legalMovesFrom(fromSquare).contains(toSquare) -> true
    else -> false
}

private fun Position.createPosition(
    san: String,
    newEnPassantField: String?,
    newHalfMoveCount: Int,
    asMainline: Boolean
): Position {
    val result = Position(
        previous = this, move = san, whiteToMove = !whiteToMove,
        enPassantField = newEnPassantField, halfMoveCount = newHalfMoveCount,
        moveNumber = if (whiteToMove) moveNumber + 1 else moveNumber,
        variationLevel = if (asMainline) variationLevel else variationLevel + 1
    )
    // copy over piece placements
    for (rank in 1..8) {
        for (file in 1..8) {
            result.square(rank, file).piece = this.square(rank, file).piece
        }
    }
    // and castling rights
    result.setCastlingRights(*this.castlingRight.toTypedArray())
    return result
}

private fun Position.castle(long: Boolean): Position {
    // the moving color is `!whiteToMove` here ...!
    val rank = backRank(!whiteToMove)
    if (long) {
        square(rank, 1).piece = null
        square(rank, 3).piece = Piece.get(KING, !whiteToMove)
        square(rank, 4).piece = Piece.get(ROOK, !whiteToMove)
        square(rank, 5).piece = null
    } else {
        square(rank, 8).piece = null
        square(rank, 7).piece = Piece.get(KING, !whiteToMove)
        square(rank, 6).piece = Piece.get(ROOK, !whiteToMove)
        square(rank, 5).piece = null
    }
    checkCastleRights()
    return this
}

private fun Position.doNormalMove(
    fromSquare: Square, toSquare: Square,
    piece: Piece, promotionPiece: Piece?
) {
    squares[fromSquare.rank-1][fromSquare.file-1].piece = null
    squares[toSquare.rank-1][toSquare.file-1].piece = promotionPiece ?: piece
    checkCastleRights()
}

private fun Position.doEnPassantMove(fromSquare: Square, toSquare: Square, piece: Piece) {
    square(fromSquare.name).piece = null
    square(toSquare.name).piece = piece
    val rankOfEnemyPawn = toSquare.rank + if (piece.isWhite) -1 else 1
    square(rankOfEnemyPawn, toSquare.file).piece = null
    checkCastleRights()
}
