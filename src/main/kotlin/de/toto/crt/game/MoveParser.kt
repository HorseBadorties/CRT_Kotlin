package de.toto.crt.game

import de.toto.crt.game.Piece.PieceType.*

/**
 * Construct the next Position from a PGN-SAN move String.
 */
fun Position.createNextFromSAN(san: String): Position = doCreateNextFromSAN(san, false)

/**
 * Construct a variation Position from a PGN-SAN move String.
 */
fun Position.createVariationFromSAN(san: String): Position = doCreateNextFromSAN(san, true)

private fun Position.doCreateNextFromSAN(san: String, asVariation: Boolean): Position {
    try {
        // we don't use/validate check/checkmate as of yet (?!)
        var move = san.removeSuffix("+").removeSuffix("#")

        val isCapture = move.contains('x')
        if (isCapture) move = move.replace("x", "")

        // consider both "0-0" (zeros) and "O-O" (Os)
        val longCastles = move.startsWith("0-0-0") || move.startsWith("O-O-O")
        val shortCastles = !longCastles && (move.startsWith("0-0") || move.startsWith("O-O"))

        // consider both "c8=Q" and "c8Q"
        val promotionPiece: Piece? =
                if (move.last() in PGN_PROMOTION_PIECES) Piece.getPieceByPGNCharAndColor(move.last(), whiteToMove) else null
        if (promotionPiece != null) {
            move = move.dropLast(1)
            if (move.last() == '=') move = move.dropLast(1)
        }

        var piece: Piece
        if (longCastles || shortCastles) {
            piece = Piece.get(KING, whiteToMove)
        } else {
            if (move[0] in PGN_PIECES) {
                piece = Piece.getPieceByPGNCharAndColor(move[0], whiteToMove)
                move = move.drop(1)
            } else {
                piece = Piece.get(PAWN, whiteToMove)
            }
        }

        val newHalfMoveCount = if (piece.type == PAWN && !isCapture) halfMoveCount + 1 else 0

        val toSquare = square(move.takeLast(2))
        move = move.dropLast(2)

        var fromSquare: Square? = null
        if (move.length == 2) {
            fromSquare = square(move)
        } else {
            for (possibleFrom in getPiecesByPiece(piece).filter { movesFrom(it).contains(toSquare) }) {
                if (move.isEmpty() || possibleFrom.name.contains(move)) {
                    fromSquare = possibleFrom
                    break
                }
            }
        }
        if (fromSquare == null) {
            throw IllegalArgumentException("failed to identify the square the move originated from")
        }

        var newEnPassantField: String? = null
        if (piece.type == PAWN && Math.abs(fromSquare.rank - toSquare.rank) == 2) {
            val opponentPawn = Piece.get(PAWN, !whiteToMove)
            if (toSquare.file > 1 && square(toSquare.rank, toSquare.file -1).piece == opponentPawn
                    || toSquare.file < 8 && square(toSquare.rank, toSquare.file + 1).piece == opponentPawn)
            {
                newEnPassantField = square(advanceOneRank(toSquare, !whiteToMove), toSquare.file).name
            }
        }

        // we got all information - create the new Position
        val nextPosition = createPosition(san, newEnPassantField, newHalfMoveCount)
        // do the actual move
        nextPosition.doMove(whiteToMove, fromSquare, toSquare, piece, shortCastles, longCastles, promotionPiece)
        // copy and possibly adjust castling rights
        nextPosition.setCastlingRights(*this.castlingRight.toTypedArray())
        nextPosition.checkCastleRights()
        // add the new Position to our list of `next` Positions
        next.add(if (asVariation) next.size - 1 else 0, nextPosition)
        return nextPosition

    } catch (ex: Exception) {
        throw IllegalArgumentException("failed to parse SAN $san", ex)
    }
}

private fun Position.createPosition(san: String, newEnPassantField: String?, newHalfMoveCount: Int): Position {
    val result = Position(
            previous = this, move = san, whiteToMove = !whiteToMove,
            enPassantField = newEnPassantField, halfMoveCount = newHalfMoveCount,
            moveNumber = if (whiteToMove) moveNumber else moveNumber + 1
    )
    // copy over piece placements
    for (rank in 1..8) {
        for (file in 1..8) {
            result.square(rank, file).piece = this.square(rank, file).piece
        }
    }
    return result
}

private fun Position.doMove(
    whiteToMove: Boolean, fromSquare: Square, toSquare: Square,
    piece: Piece, shortCastles: Boolean, longCastles: Boolean, promotionPiece: Piece?
) {
    if (longCastles) {
        val rank = backRank(whiteToMove)
        square(rank, 1).piece = null
        square(rank, 3).piece = Piece.get(KING, whiteToMove)
        square(rank, 4).piece = Piece.get(ROOK, whiteToMove)
        square(rank, 5).piece = null
    } else if (shortCastles) {
        val rank = backRank(whiteToMove)
        square(rank, 8).piece = null
        square(rank, 7).piece = Piece.get(KING, whiteToMove)
        square(rank, 6).piece = Piece.get(ROOK, whiteToMove)
        square(rank, 5).piece = null
    } else {
        square(fromSquare.name).piece = null
        square(toSquare.name).piece = promotionPiece ?: piece
    }
}

private val PGN_PIECES = listOf('K', 'Q', 'R', 'B', 'N')
private val PGN_PROMOTION_PIECES = listOf('Q', 'R', 'B', 'N')

