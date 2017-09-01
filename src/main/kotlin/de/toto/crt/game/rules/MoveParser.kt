package de.toto.crt.game.rules

import de.toto.crt.game.Position
import de.toto.crt.game.rules.Piece.PieceType.*

/**
 * Construct the next Position from a SAN move String, either `asMainline` (default) or as a new variation.
 */
fun Position.createNextFromSAN(san: String, asMainline: Boolean = true): Position {
    try {
        val result = parseNextFromSAN(san, asMainline)
        // add the new Position to our list of `next` Positions
        next.add(if (asMainline) 0 else next.size, result)
        return result
    } catch (ex: Exception) {
        throw IllegalArgumentException("failed to parse SAN $san as next move for ${moveWithMovenumber()}", ex)
    }
}

val Position.squaresOfMove: List<Square>
    get() {
        if (previous == null) return listOf()
        with (previous!!.parseSAN(move)) {
            return if (isNullMove) listOf() else listOf(fromSquare!!, toSquare!!)
        }
    }

data class MoveInfo(
        val isNullMove: Boolean = false,
        val isLongCastles: Boolean = false,
        val isShortCastles: Boolean = false,
        val isCapture: Boolean = false,
        val isCheck: Boolean = false,
        val isMate: Boolean = false,
        val isPromotion: Boolean = false,
        val promotionPiece: Piece? = null,
        val piece: Piece? = null,
        val fromSquare: Square?,
        val toSquare: Square?)

fun Position.parseSAN(san: String): MoveInfo {
    // handle null-move
    if (san == "--") {
        return MoveInfo(isNullMove = true, fromSquare = null, toSquare = null)
    }

    val isMate = san.endsWith("#")
    val isCheck = isMate || san.endsWith("+")
    val isCapture = san.contains('x')

    // handle castling - consider both "0-0" (zeros) and "O-O" (Os)
    val isLongCastles = san.startsWith("0-0-0") || san.startsWith("O-O-O")
    val isShortCastles = !isLongCastles && (san.startsWith("0-0") || san.startsWith("O-O"))
    if (isLongCastles || isShortCastles) {
        val squares = castlingSquares(isLongCastles)
        return MoveInfo(isLongCastles = isLongCastles,
                isShortCastles = isShortCastles,
                isCheck = isCheck,
                isMate = isMate,
                piece = Piece.get(KING, whiteToMove),
                fromSquare = squares.first,
                toSquare = squares.second)
    }

    var move = san.removeSuffix("+").removeSuffix("#")
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
    val toSquare = square(move.takeLast(2))
    move = move.dropLast(2)
    val fromSquare = when {
        move.length == 2 -> square(move)
        else -> findSquare { isFromSquare(it, move, piece, toSquare, isCapture) }
    }
    return MoveInfo(isCapture = isCapture,
            isCheck = isCheck,
            isMate = isMate,
            isPromotion = promotionPiece != null,
            promotionPiece = promotionPiece,
            piece = piece,
            fromSquare = fromSquare,
            toSquare = toSquare)
}

private fun Position.parseNextFromSAN(san: String, asMainline: Boolean): Position {
    with (parseSAN(san)) {
        if (isNullMove) return createPosition(san, null, 0, asMainline).checkCastleRights()
        if (isLongCastles || isShortCastles)
            return createPosition(san, null, halfMoveCount + 1, asMainline).castle(isLongCastles)

        val newEnPassantField = getEnPassantField(piece!!, fromSquare!!, toSquare!!)
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
}

// TODO cleanup code
private fun Position.getEnPassantField(piece: Piece, from: Square, to: Square): String? {
    // was it a two-squares pawn move?
    if (piece.type == PAWN && Math.abs(from.rank - to.rank) == 2) {
        // is there an opponent pawn on a square next to `to` ?
        val opponentPawn = Piece.get(PAWN, !whiteToMove)
        if (to.file > 1 && square(to.rank, to.file - 1).piece == opponentPawn
                || to.file < 8 && square(to.rank, to.file + 1).piece == opponentPawn) {
            return square(advanceOneRank(to, !whiteToMove), to.file).name
        }
    }
    return null
}

// use some heuristics before calling the "expensive" `legalMovesFrom`
private fun Position.isFromSquare(
        s: Square, // the square to check
        from: String, // either empty or contains a rank number or file character
        piece: Piece, // the piece that is moving to `toSquare`
        toSquare: Square, // the target square of the move
        isCapture: Boolean
) = when {
    s.piece != piece -> false
    s.piece?.type == PAWN && !isCapture && s.file != toSquare.file -> false
    s.piece?.type == PAWN && isCapture && Math.abs(s.file - toSquare.file) > 1 -> false
    s.piece?.type == BISHOP && s.isWhite != toSquare.isWhite -> false
    s.piece?.type == ROOK && s.rank != toSquare.rank && s.file != toSquare.file -> false
    s.piece?.type == KNIGHT && (Math.abs(s.rank - toSquare.rank) > 2 || Math.abs(s.file - toSquare.file) > 2) -> false
    (from.isEmpty() || s.name.contains(from)) && legalMovesFrom(s).contains(toSquare) -> true
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

/**
 * `first` contains the King's square, `second` the sqaure where the King moves to
 */
private fun Position.castlingSquares(long: Boolean): Pair<Square, Square> {
    // the moving color is `!whiteToMove` here ...!
    val rank = backRank(!whiteToMove)
    val from = square(rank, 5)
    val to = if (long) square(rank, 3) else square(rank, 7)
    return Pair(from, to)
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
    square(fromSquare.rank, fromSquare.file).piece = null
    square(toSquare.rank, toSquare.file).piece = promotionPiece ?: piece
    checkCastleRights()
}

private fun Position.doEnPassantMove(fromSquare: Square, toSquare: Square, piece: Piece) {
    square(fromSquare.name).piece = null
    square(toSquare.name).piece = piece
    val rankOfEnemyPawn = toSquare.rank + if (piece.isWhite) -1 else 1
    square(rankOfEnemyPawn, toSquare.file).piece = null
    checkCastleRights()
}
