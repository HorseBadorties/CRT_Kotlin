package de.toto.crt.game

class Position {

    val move: String //in Long Algebraic Notation, or "" for the starting position or "--" for a null move
    val whiteToMove: Boolean
    val castlingRight = java.util.EnumSet.noneOf(CastlingRight::class.java)
    val enPassantField: Square?
    val halfMoveCount: Int
    val moveNumber: Int
    val previous: Position?
    val next = mutableListOf<Position>()

    /**
     * Constructs a Position with an empty board
     */
    constructor() {
        move = ""
        whiteToMove = true
        enPassantField = null
        halfMoveCount = 0
        moveNumber = 0
        previous = null
    }

    /**
     * Constructs a Position according to the provided `fen`
     */
    constructor(fen: String) {
        move = ""
        val fenValues = setFen(fen)
        whiteToMove = fenValues.whiteToMove
        defineCastleRights(fenValues.castlingRight)
        enPassantField = if (fenValues.enPassantField == "-") null else square(fenValues.enPassantField)
        halfMoveCount = fenValues.halfMoveCount
        moveNumber = fenValues.moveNumber
        previous = null
    }

    /**
     * Constructs a Position based on it's `previous` Position and the `move` (as LAN) that led to this Position
     */
    constructor(previous: Position, move: String) {
        this.move = move
        this.previous = previous
        for (rank in 0..7) {
            for (file in 0..7) {
                squares[rank][file].piece = previous.squares[rank][file].piece
            }
        }
        whiteToMove = !previous.whiteToMove
        // TODO
        enPassantField = null // if (move was double pawn move and ...) field else null
        halfMoveCount = 0 // if (move was pawn move and no capture) previous.moveNumber + 1 else 0
        moveNumber = if (whiteToMove) previous.moveNumber + 1 else previous.moveNumber
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

    fun squares() = squares.flatten()

    fun getPiecesByPiece(piece: Piece) = squares().filter { !it.isEmpty && it.piece == piece }

    fun getPiecesByColor(white: Boolean) =
            squares().filter { !it.isEmpty && it.piece?.isWhite == white }

}




