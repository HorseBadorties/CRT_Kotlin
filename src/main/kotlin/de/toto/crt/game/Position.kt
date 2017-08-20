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

    val squares = Array(8) { iOuter -> Array(8)
        { iInner -> Square(iOuter + 1, iInner + 1)}
    }

    /**
     * get a Square by 1-based `rank` and `file`
     */
    fun square(rank: Int, file: Int): Square {
        return squares[rank-1][file-1]
    }

    /**
     * get a Square by `name`
     */
    fun square(name: String): Square {
        val file = name[0] - 'a'
        val rank = name[1].toString().toInt() - 1
        return squares[rank][file]
    }

    /**
     * get a List of all Squares
     */
    fun squares() = squares.flatten()


    val hasNext: Boolean  get() { return !next.isEmpty() }

    fun hasVariation(san: String) = next.any { it.move == san }

    companion object

    // TODO move somewhere else?
    fun moveWithMovenumber() = "$moveNumber${if (whiteToMove) "..." else "."} $move"

    val moveWithMovenumber: String get() { return "$moveNumber${if (whiteToMove) "..." else "."} $move" }
}







