package de.toto.crt.game

import java.util.*

class Position {

    enum class CastlingRight { WHITE_SHORT, WHITE_LONG, BLACK_SHORT, BLACK_LONG }
    val castlingRight = EnumSet.noneOf(CastlingRight::class.java)

    var fen: String = FEN_EMPTY_BOARD

    val isWhiteToMove: Boolean

    /**
     * Constructs a Position with an empty board
     */
    constructor() { isWhiteToMove = true }

    /**
     * Constructs a Position according to the provided `fen`
     */
    constructor(fen: String) {
        setFen(fen, true)
        isWhiteToMove = false // TODO
    }

    /**
     * Constructs a Position based on it's `previous` Position and the `move` (as LAN) that led to this Position
     */
    constructor(previous: Position, move: String) {
        for (rank in 0..7) {
            for (file in 0..7) {
                squares[rank][file].piece = previous.squares[rank][file].piece
            }
        }
        isWhiteToMove = !previous.isWhiteToMove

        // TODO
    }

    private val squares = Array(8) { iOuter -> Array(8)
        { iInner -> Square(iOuter + 1, iInner + 1)}
    }

    /**
     * get a Square by 1-based rank and file
     */
    fun square(rank: Int, file: Int): Square {
        require (rank in 1..8 && file in 1..8) {
            "Illegal Square rank:$rank file:$file"
        }
        return squares[rank-1][file-1]
    }

    fun square(name: String): Square {
        val rankAndFile = Square.rankAndFileByName(name)
        return squares[rankAndFile.first-1][rankAndFile.second-1]
    }

    fun enPassantField(): String? {
        //TODO implement enPassantField()
        return null
    }

    fun squares() = squares.flatten()

    fun getPiecesByPiece(piece: Piece) = squares().filter { !it.isEmpty && it.piece == piece }

    fun getPiecesByColor(byWhitePieces: Boolean) =
            squares().filter { !it.isEmpty && it.piece?.isWhite == byWhitePieces }

    private fun setFen(_fen: String, setupPosition: Boolean) {
        this.fen = _fen.trim()
        if (setupPosition) {
            try {
                var rank = 8; var file = 1
                for (fenChar in fen.split(" ").first()) {
                    if ('/' == fenChar) {
                        rank--
                        file = 1
                    } else if (fenChar.isDigit()) {
                        file += fenChar.toInt()
                    } else {
                        square(rank, file).piece = Piece.getPieceByFenChar(fenChar)
                        file++
                    }
                }
            } catch (ex: Exception) {
                throw IllegalArgumentException("failed to parse FEN: " + fen, ex)
            }
        }
    }

    companion object {
        const val FEN_STARTPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        const val FEN_EMPTY_BOARD = "8/8/8/8/8/8/8/8 w KQkq - 0 1"

    }

}




