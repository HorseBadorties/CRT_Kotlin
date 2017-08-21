package de.toto.crt.game

import de.toto.crt.game.rules.CastlingRight
import de.toto.crt.game.rules.NAG
import de.toto.crt.game.rules.Square

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
        { iInner -> Square(iOuter + 1, iInner + 1) }
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
     * Returns a list of squares that match the `predicate`
     */
    inline fun filterSquares(predicate: (Square) -> Boolean): List<Square> {
        val result = mutableListOf<Square>()
        for (rank in 0..7) {
            for (file in 0..7) {
                with (squares[rank][file]) { if (predicate(this)) result.add(this) }
            }
        }
        return result
    }

    /**
     * Returns the first square that matches the `predicate`.
     * Throws an `IllegalArgumentException` if no match was found
     */
    inline fun findSquare(predicate: (Square) -> Boolean): Square {
        for (rank in 0..7) {
            for (file in 0..7) {
                with (squares[rank][file]) { if (predicate(this)) return this }
            }
        }
        throw IllegalArgumentException("no square matches the predicate")
    }



    val hasNext: Boolean  get() { return !next.isEmpty() }

    fun hasVariation(san: String) = next.any { it.move == san }

    // TODO move somewhere else?
    val moveWithMovenumber: String get() { return "$moveNumber${if (whiteToMove) "..." else "."} $move" }
}







