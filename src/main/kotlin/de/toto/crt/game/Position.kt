package de.toto.crt.game

class Position(
    val move: String = "", // as SAN, or "" for the starting position or "--" for a null move
    val whiteToMove: Boolean = true,
    val enPassantField: String? = null,
    val halfMoveCount: Int = 0,
    val moveNumber: Int = 0,
    val previous: Position? = null,
    val variationLevel: Int = 0
) {

    val castlingRight = java.util.EnumSet.noneOf(CastlingRight::class.java)
    val next = mutableListOf<Position>()
    var comment: String? = null
    val nags = mutableListOf<NAG>()

    private val squares = Array(8) { iOuter -> Array(8)
        { iInner -> Square(iOuter + 1, iInner + 1)}
    }

    /**
     * get a Square by 1-based `rank` and `file`
     */
    fun square(rank: Int, file: Int): Square {
        require (rank in 1..8 && file in 1..8) {
            "Illegal Square rank:$rank file:$file"
        }
        return squares[rank-1][file-1]
    }

    /**
     * get a Square by `name`
     */
    fun square(name: String): Square {
        with(Square.rankAndFileByName(name)) { return squares[first-1][second-1] }
    }

    /**
     * get a List of all Squares
     */
    fun squares() = squares.flatten()


    val hasNext: Boolean  get() { return !next.isEmpty() }

    val hasVariation: Boolean  get() { return next.size > 1 }

    companion object

    // TODO move somewhere else?
    val moveWithMovenumber: String get() = "$moveNumber${if (whiteToMove) "." else "..."}$move"


}







