package de.toto.crt.game

import de.toto.crt.game.rules.CastlingRight
import de.toto.crt.game.rules.NAG
import de.toto.crt.game.rules.Square
import java.util.*

class Position(
    val move: String = "", // as SAN, or "" for the starting position or "--" for a null move
    val whiteToMove: Boolean = true,
    val enPassantField: String? = null,
    val halfMoveCount: Int = 0,
    val moveNumber: Int = 0,
    var previous: Position? = null,
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

    /**
     * Returns a breadth-first ordered List of all following Positions with all their variations.
     */
    fun breadthFirst(): List<Position> {
        val queue = LinkedList<Position>()
        val result = mutableListOf<Position>()
        queue.add(this)
        while (!queue.isEmpty()) {
            val p = queue.poll()
            if (p !== this) result.add(p)
            p.next.filterTo(queue) { it !in result }
        }
        return result
    }

    /**
     * Returns a preorder depth-first List of all following Positions with all their variations.
     */
    fun preOrderDepthFirst(filter: (Position) -> Boolean = { true }): List<Position> {
        val result = mutableListOf<Position>()

        fun preOrder(pos: Position) {
            pos.next.forEach {
                if (filter(it)) {
                    result.add(it)
                    preOrder(it)
                }
            }
        }

        preOrder(this)
        return result
    }

    /**
     * Two Positions are considered equal if
     * - the same side is to move
     * - all squares contain the same pieces
     * - they have equal castling rights
     * - they have the same en passant square
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (whiteToMove != other.whiteToMove) return false
        for (rank in 0..7) {
            for (file in 0..7) {
                if (squares[rank][file].piece != other.squares[rank][file].piece) return false
            }
        }
        if (castlingRight != other.castlingRight) return false
        if (enPassantField != other.enPassantField) return false

        return true
    }

    /**
     * The `hashCode` of a  Position is calculated based on
     * - the side to move
     * - all squares including their pieces
     * - castling rights
     * - en passant square
     */
    override fun hashCode(): Int {
        var result = whiteToMove.hashCode()
        result = 31 * result + (enPassantField?.hashCode() ?: 0)
        result = 31 * result + (castlingRight?.hashCode() ?: 0)
        for (rank in 0..7) {
            for (file in 0..7) {
                result = 31 * result + squares[rank][file].hashCode()
            }
        }
        return result
    }

    override fun toString() = move

    // TODO move somewhere else?
//    val moveWithMovenumber: String get() { return "$moveNumber${if (whiteToMove) "..." else "."} $move" }
    fun moveWithMovenumber() = "$moveNumber${if (whiteToMove) "..." else "."}$move"

}







