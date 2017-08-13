package de.toto.crt.game

import de.toto.crt.game.Piece.*
import de.toto.crt.game.Piece.PieceType.*
import de.toto.crt.game.CastlingRight.*

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
        var move = san.removeSuffix("+").removeSuffix("#")
        val longCastles = move.startsWith("0-0-0") || move.startsWith("O-O-O")
        val shortCastles = !longCastles && (move.startsWith("0-0") || move.startsWith("O-O"))
        val promotionPiece: Piece? =
                if (move.contains('=')) Piece.getPieceByPGNCharAndColor(move.last(), whiteToMove) else null
        if (promotionPiece != null) move = move.dropLast(2)
        var piece: Piece = if (whiteToMove) WHITE_KING else BLACK_KING
        if (!longCastles && !shortCastles) {
            if (move[0] in listOf('K', 'Q', 'R', 'B', 'N')) {
                piece = Piece.getPieceByPGNCharAndColor(move[0], whiteToMove)
                move = move.drop(1)
            } else {
                piece = if (whiteToMove) WHITE_PAWN else BLACK_PAWN
            }
        }
        val isCapture = move.contains('x')
        if (isCapture) move = move.replace("x", "")
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
        // create the new Position
        val newHalfMoveCount = if (piece.type == PAWN && !isCapture) halfMoveCount + 1 else 0
        var newEnPassantField: String? = null
        if (piece.type == PAWN && Math.abs(fromSquare.rank - toSquare.rank) == 2) {
            val opponentPawn = if (whiteToMove) BLACK_PAWN else WHITE_PAWN
            if (toSquare.file > 1 && square(toSquare.rank, toSquare.file -1).piece == opponentPawn
                    || toSquare.file < 8 && square(toSquare.rank, toSquare.file + 1).piece == opponentPawn)
            {
                newEnPassantField = square(if (whiteToMove) toSquare.rank - 1 else toSquare.rank + 1, toSquare.file).name
            }
        }

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
        // do the actual move
        if (longCastles) {
            val rank = if (whiteToMove) 1 else 8
            result.square(rank, 1).piece = null
            result.square(rank, 3).piece = if (whiteToMove) WHITE_KING else BLACK_KING
            result.square(rank, 4).piece = if (whiteToMove) WHITE_ROOK else BLACK_ROOK
            result.square(rank, 5).piece = null
        } else if (shortCastles) {
            val rank = if (whiteToMove) 1 else 8
            result.square(rank, 8).piece = null
            result.square(rank, 7).piece = if (whiteToMove) WHITE_KING else BLACK_KING
            result.square(rank, 6).piece = if (whiteToMove) WHITE_ROOK else BLACK_ROOK
            result.square(rank, 5).piece = null
        } else if (piece.type == PAWN) {
            result.square(fromSquare.name).piece = null
            result.square(toSquare.name).piece = promotionPiece ?: piece
        } else {
            result.square(fromSquare.name).piece = null
            result.square(toSquare.name).piece = piece
        }
        // adjust castling rights
        result.defineCastlingRights(*this.castlingRight.toTypedArray())
        checkCastleRight(result, SHORT_CASTLES, true)
        checkCastleRight(result, SHORT_CASTLES, false)
        checkCastleRight(result, LONG_CASTLES, true)
        checkCastleRight(result, LONG_CASTLES, false)
        // add the new Position to our list of `next` Positions
        next.add(if (asVariation) next.size - 1 else 0, result)
        return result
    } catch (ex: Exception) {
        throw IllegalArgumentException("failed to parse SAN $san", ex)
    }
}

private fun checkCastleRight(position: Position, shortCastles: Boolean, whiteToMove: Boolean) {
    if (position.hasCastlingRight(shortCastles, whiteToMove)) {
        val rookSquare = position.square(if (whiteToMove) 1 else 8, if (shortCastles) 8 else 1)
        val kingSquare = position.square(if (whiteToMove) 1 else 8, 5)
        if (rookSquare.piece != if (whiteToMove) WHITE_ROOK else BLACK_ROOK) {
            position.removeCastlingRight(shortCastles, whiteToMove)
        } else if (kingSquare.piece != if (whiteToMove) WHITE_KING else BLACK_KING) {
            position.removeCastlingRight(shortCastles, whiteToMove)
        }
    }
}
