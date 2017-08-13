package de.toto.crt.game

class Square {

    // rank and file are intern Byte but visible as Int
    private var _rank: Byte = 0
    val rank get() = _rank.toInt()
    private var _file: Byte = 0
    val file get() = _file.toInt()
    var piece: Piece? = null

    constructor(rank: Int, file: Int)  {
        require (file in 1..8 && rank in 1..8) {
            "Illegal Square: rank:$rank file $file"
        }
        _rank = rank.toByte()
        _file = file.toByte()
    }

    constructor(rank: Int, file: Int, piece: Piece?) : this(rank, file) {
        this.piece = piece
    }

    companion object {
        /**
         * e.g. "b4" -> 2, 4;
         */
        fun rankAndFileByName(name: String) : Pair<Int, Int> {
            require (name.length == 2) {
                "Illegal Square name $name"
            }
            val file = name[0] - 'a' + 1
            val rank = name[1].toString().toInt()
            require (file in 1..8 && rank in 1..8) {
                "Illegal Square name $name"
            }
            return Pair(rank, file)
        }

        fun fromName(name: String, piece: Piece?) : Square {
            val rankAndFile = rankAndFileByName(name)
            return Square(rankAndFile.first, rankAndFile.second, piece)
        }

        fun fromName(name: String) = fromName(name, null)
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
    val nameWithPieceSuffix get() = (piece?.pgnChar ?: "").toString() + name

    /**
     * e.g. "♞f3"
     */
    val nameWithPieceFigurine get() = (piece?.figurine ?: "").toString() + name

    /**
     * e.g. "a1 black"
     */
    override fun toString() = "$nameWithPieceFigurine, ${if (isWhite) "white" else "black"} "

    /**
     * A Square equals another Square if they have the same coordinates.
     */
    override fun equals(other: Any?) = other is Square && this.hashCode() == other.hashCode()

    override fun hashCode() = 10*rank+file

}

fun Byte.isEven() = this % 2 == 0