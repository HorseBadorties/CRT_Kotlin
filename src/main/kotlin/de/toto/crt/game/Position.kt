package de.toto.crt.game

import de.toto.crt.game.rules.CastlingRight
import de.toto.crt.game.rules.NAG
import de.toto.crt.game.rules.Square
import javafx.scene.paint.Color
import java.util.*

class Position(
    val move: String = "", // as SAN, or "" for the starting position or "--" for a null move
    val whiteToMove: Boolean = true,
    val enPassantField: String? = null,
    val halfMoveCount: Int = 0,
    val moveNumber: Int = 0, // movenumber of the move that let to this position (unlike FEN fullmove number!)
    var previous: Position? = null,
    val variationLevel: Int = 0
) {

    val castlingRight = java.util.EnumSet.noneOf(CastlingRight::class.java)
    val next = mutableListOf<Position>()
    var comment: String? = null
        set(value) {
            // may contain graphics comments such as [%csl Ge5][%cal Ge5b2]
            if (value != null) {
                with (Regex("\\[(.*?)\\]")) {
                    findAll(value).forEach { parseGraphicsComment(it.value) }
                    field = replace(value, "")
                }
            } else field = null
        }
    val graphicsComments = mutableListOf<GraphicsComment>()
    val nags = mutableListOf<NAG>()

    val squares = Array(8) { iOuter -> Array(8)
        { iInner -> Square(iOuter + 1, iInner + 1) }
    }

    /**
     * - square coloring like [%csl Rb5,Rd5,Rg2]
     * - colored arrows like [%cal Gf3d2,Gd2c4]
     */
    private fun parseGraphicsComment(graphicsComment: String) {
        try {
            graphicsComment
                    .drop(1)
                    .dropLast(1)
                    .split(" ")[1]
                    .split(",")
                    .forEach {
                        if (!it.isEmpty()) {
                            val color = when (it[0]) {
                                'R' -> Color.RED
                                'Y' -> Color.YELLOW
                                else -> Color.GREEN
                            }
                            val s1 = Square.fromName(it.substring(1, 3))
                            val s2 = if (it.length > 3) Square.fromName(it.substring(3, 5)) else null
                            val gc = if (s2 != null) ColoredArrow(s1, s2, color)
                                        else ColoredSquare(s1, color)
                            graphicsComments.add(gc)
                        }
                    }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
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
     * Invokes `action` on each square
     */
    inline fun forEachSquare(action: (square: Square) -> Unit) {
        forEachRankAndFile { rank, file ->  action(square(rank, file)) }
    }

    /**
     * Returns a list of squares that match the `predicate`
     */
    inline fun filterSquares(predicate: (Square) -> Boolean): List<Square> {
        val result = mutableListOf<Square>()
        forEachSquare { if (predicate(it)) result.add(it) }
        return result
    }

    /**
     * Returns the first square that matches the `predicate`.
     * Throws an `IllegalArgumentException` if no match was found
     */
    inline fun findSquare(predicate: (Square) -> Boolean): Square {
        forEachSquare { if (predicate(it)) return it }
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

    fun List<Position>.shuffle(): List<Position> {
        Collections.shuffle(this)
        return this
    }

    /**
     * Returns a preorder depth-first List of all following Positions with all their variations.
     * If `shuffle` is used the order of variations will be shuffled.
     */
    fun preOrderDepthFirst(shuffle: Boolean = false, filter: (Position) -> Boolean = { true }): List<Position> {

        val result = mutableListOf<Position>()

        fun preOrder(pos: Position) {
            val variations = pos.next
            if (shuffle) variations.shuffle()
            variations.forEach {
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
    val movenumberMove: String get() { return "$moveNumber${if (whiteToMove) "..." else "."} $move" }
    val movenumberMoveNAGs: String get() { return "$movenumberMove${nags.joinToString(separator = " ")}" }

}

inline fun forEachRankAndFile(action: (rank: Int, file: Int) -> Unit) {
    for (rank in 1..8) {
        for (file in 1..8) {
            action(rank, file)
        }
    }
}

sealed class GraphicsComment
data class ColoredSquare(val square: Square, val color: Color) : GraphicsComment()
data class ColoredArrow(val from: Square, val to: Square, val color: Color) : GraphicsComment()







