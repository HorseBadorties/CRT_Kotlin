package de.toto.crt.game.rules

class Square {

    // rank and file are intern Byte but visible as Int
    private var _rank: Byte = 0
    val rank get() = _rank.toInt()
    private var _file: Byte = 0
    val file get() = _file.toInt()
    var piece: Piece? = null

    constructor(rank: Int, file: Int)  {
        _rank = rank.toByte()
        _file = file.toByte()
    }

    /**
     * is it a light-colored Square?
     */
    val isWhite get() = _file.isEven() && !_rank.isEven() || !_file.isEven() && _rank.isEven()

    /**
     * is there a Piece on this Square?
     */
    val isEmpty get() = piece == null

    private val fileName get() = ('a' + file - 1).toString()

    /**
     * e.g. "f3"
     */
    val name get() = fileName + rank

    /**
     * e.g. "Nf3"
     */
    val nameWithPiecePrefix get() = (piece?.pgnChar ?: "").toString() + name

    /**
     * e.g. "♞f3"
     */
    val nameWithPieceFigurine get() = (piece?.figurine ?: "").toString() + name

    /**
     * e.g. "a1 black"
     */
    override fun toString() = name

    /**
     * A Square equals another Square if they have the same coordinates.
     */
    override fun equals(other: Any?) = other is Square && this.hashCode() == other.hashCode()

    override fun hashCode() = 31 * (10 * rank + file) + (piece?.hashCode() ?: 0)

    companion object {
        fun fromName(name: String): Square = Square(name.drop(1).toInt(), name[0].toInt() - 'a'.toInt() + 1)
    }
}

fun backRank(white: Boolean) = if (white) 1 else 8

private fun Byte.isEven() = this % 2 == 0