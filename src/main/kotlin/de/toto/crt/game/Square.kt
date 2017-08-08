package de.toto.crt.game

class Square {

    // rank and file are intern Byte but visible as Int
    var _rank: Byte = 0
    val rank get() = _rank.toInt()
    var _file: Byte = 0
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
     * e.g. "a1 black"
     */
    override fun toString() = "$nameWithPieceSuffix, ${if (isWhite) "white" else "black"} "

    /**
     * A Square equals another Square if they have the same coordinates.
     */
    override fun equals(other: Any?) = other is Square && this.hashCode() == other.hashCode()

    override fun hashCode() = 10*rank+file

/*
    private fun getSquare(p: Position, rank: Int, file: Int): Square? {
        if (rank < 1 || rank > 8 || file < 1 || file > 8) return null
        return p.getSquare(rank, file)
    }

    fun isEnPassantPossible(to: Square, p: Position): Boolean {
        try {
            if (to.piece!!.isWhite && rank - to.rank == -2) {
                var epSquare = getSquare(p, rank + 2, file - 1)
                if (epSquare != null && epSquare.piece === Piece.BLACK_PAWN) return true
                epSquare = getSquare(p, rank + 2, file + 1)
                if (epSquare != null && epSquare.piece === Piece.BLACK_PAWN) return true
            } else if (!to.piece!!.isWhite && rank - to.rank == 2) {
                var epSquare = getSquare(p, rank - 2, file - 1)
                if (epSquare != null && epSquare.piece === Piece.WHITE_PAWN) return true
                epSquare = getSquare(p, rank - 2, file + 1)
                if (epSquare != null && epSquare.piece === Piece.WHITE_PAWN) return true
            }
        } catch (ignore: Exception) {
        }

        return false

    }

    /**
     * Does the piece on this Square attack the other square?
     */
    fun attacks(other: Square, p: Position, ignore: Square?): Boolean {
        if (piece == null) return false
        if (ignore == null && isPinned(p, other) && other.piece !== (if (isWhite) Piece.BLACK_KING else Piece.WHITE_KING))
            return false
        when (piece!!.type) {
            Piece.PieceType.KING -> return kingAttacks(other, p)
            Piece.PieceType.QUEEN -> return queenAttacks(other, p, ignore)
            Piece.PieceType.ROOK -> return rookAttacks(other, p, ignore)
            Piece.PieceType.BISHOP -> return bishopAttacks(other, p, ignore)
            Piece.PieceType.KNIGHT -> return knightAttacks(other, p)
            Piece.PieceType.PAWN -> return pawnAttacks(other, p)
        }
        return false
    }

    /**
     * Is the piece on this Square attacked by an enemy piece on another square?
     */
    fun isAttacked(p: Position): Boolean {
        if (piece == null) return false
        for (squareWithEnemyPiece in p.getSquaresWithPiecesByColor(!piece!!.isWhite)) {
            if (squareWithEnemyPiece.attacks(this, p, null)) return true
        }
        return false
    }


    /**
     * Does this Square share a diagonal with another Square?
     */
    fun onDiagonalWith(other: Square): Boolean {
        return bishopAttacks(other, Position.EMPTY_BOARD, null)
    }

    /**
     * Can a Knight move from this Square to another Square?
     */
    fun isKnightMove(other: Square): Boolean {
        return knightAttacks(other, Position.EMPTY_BOARD)
    }

    /**
     * Can the piece on this Square move to the other square?
     */
    fun canMoveTo(other: Square, p: Position, ignore: Square?): Boolean {
        if (piece == null) return false
        if (ignore == null && isPinned(p, other)) return false
        when (piece!!.type) {
            Piece.PieceType.KING -> return kingCanMoveTo(other, p)
            Piece.PieceType.QUEEN -> return queenAttacks(other, p, ignore)
            Piece.PieceType.ROOK -> return rookAttacks(other, p, ignore)
            Piece.PieceType.BISHOP -> return bishopAttacks(other, p, ignore)
            Piece.PieceType.KNIGHT -> return knightAttacks(other, p)
            Piece.PieceType.PAWN -> return pawnCanMoveTo(other, p)
        }
        return false
    }

    private fun kingAttacks(other: Square, p: Position): Boolean {
        if (other == getSquare(p, rank + 1, file.toInt())) return true
        if (other == getSquare(p, rank + 1, file - 1)) return true
        if (other == getSquare(p, rank + 1, file + 1)) return true
        if (other == getSquare(p, rank.toInt(), file - 1)) return true
        if (other == getSquare(p, rank.toInt(), file + 1)) return true
        if (other == getSquare(p, rank - 1, file.toInt())) return true
        if (other == getSquare(p, rank - 1, file - 1)) return true
        if (other == getSquare(p, rank - 1, file + 1)) return true
        return false
    }

    private fun kingCanMoveTo(other: Square, p: Position): Boolean {
        if (kingAttacks(other, p)) return true
        val castlingSquareNames = p.possibleCastlingSquareNames
        if (other.name == castlingSquareNames[0] || other.name == castlingSquareNames[1])
            return true
        return false
    }

    private fun queenAttacks(other: Square, p: Position, ignore: Square): Boolean {
        return rookAttacks(other, p, ignore) || bishopAttacks(other, p, ignore)
    }

    private fun rookAttacks(other: Square, p: Position, ignore: Square): Boolean {
        var _rank = rank.toInt()
        var _file = file.toInt()
        var s: Square? = this
        while (s != null) { //go up
            s = getSquare(p, ++_rank, _file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        s = this
        _rank = rank.toInt()
        _file = file.toInt()
        while (s != null) { //go right
            s = getSquare(p, _rank, ++_file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        s = this
        _rank = rank.toInt()
        _file = file.toInt()
        while (s != null) { //go down
            s = getSquare(p, --_rank, _file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        s = this
        _rank = rank.toInt()
        _file = file.toInt()
        while (s != null) { //go left
            s = getSquare(p, _rank, --_file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        return false
    }

    private fun bishopAttacks(other: Square, p: Position, ignore: Square?): Boolean {
        var _rank = rank.toInt()
        var _file = file.toInt()
        var s: Square? = this
        while (s != null) { //go up-right
            s = getSquare(p, ++_rank, ++_file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        s = this
        _rank = rank.toInt()
        _file = file.toInt()
        while (s != null) { //go up-left
            s = getSquare(p, ++_rank, --_file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        s = this
        _rank = rank.toInt()
        _file = file.toInt()
        while (s != null) { //go down-right
            s = getSquare(p, --_rank, ++_file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        s = this
        _rank = rank.toInt()
        _file = file.toInt()
        while (s != null) { //go down-left
            s = getSquare(p, --_rank, --_file)
            if (other == s) return true
            if (s != null && s.piece != null && s !== ignore) break
        }
        return false
    }

    private fun knightAttacks(other: Square, p: Position): Boolean {
        if (other == getSquare(p, rank + 2, file + 1)) return true
        if (other == getSquare(p, rank + 2, file - 1)) return true
        if (other == getSquare(p, rank + 1, file + 2)) return true
        if (other == getSquare(p, rank + 1, file - 2)) return true
        if (other == getSquare(p, rank - 1, file + 2)) return true
        if (other == getSquare(p, rank - 1, file - 2)) return true
        if (other == getSquare(p, rank - 2, file + 1)) return true
        if (other == getSquare(p, rank - 2, file - 1)) return true
        return false
    }

    private fun pawnAttacks(other: Square, p: Position): Boolean {
        val startRank = if (this.piece!!.isWhite) 2 else 7
        // move one square
        var s: Square = getSquare(p, if (this.piece!!.isWhite) rank + 1 else rank - 1, file.toInt())
        if (other == s && s.piece == null) return true
        if (rank.toInt() == startRank && s.piece == null) {
            // move two squares
            s = getSquare(p, if (this.piece!!.isWhite) rank + 2 else rank - 2, file.toInt())
            if (other == s && s.piece == null) return true
        }
        // try captures (with en passant)
        val fen = if (p.fen != null) p.fen else p.previous.fen
        val enPassantField = fen.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3]
        s = getSquare(p, if (this.piece!!.isWhite) rank + 1 else rank - 1, file + 1)
        if (other == s && (s.piece != null && s.piece!!.isWhite != this.piece!!.isWhite || s.name == enPassantField))
            return true
        s = getSquare(p, if (this.piece!!.isWhite) rank + 1 else rank - 1, file - 1)
        if (other == s && (s.piece != null && s.piece!!.isWhite != this.piece!!.isWhite || s.name == enPassantField))
            return true

        return false
    }

    private fun pawnCanMoveTo(other: Square, p: Position): Boolean {
        return pawnAttacks(other, p)
    }

    fun getPossibleTargetSquares(p: Position): List<Square> {
        val result = ArrayList<Square>()
        if (piece == null) return result
        when (piece!!.type) {
            Piece.PieceType.KING -> addPossibleTargetSquaresOfKing(result, p)
            Piece.PieceType.QUEEN -> addPossibleTargetSquaresOfQueen(result, p)
            Piece.PieceType.ROOK -> addPossibleTargetSquaresOfRook(result, p)
            Piece.PieceType.BISHOP -> addPossibleTargetSquaresOfBishop(result, p)
            Piece.PieceType.KNIGHT -> addPossibleTargetSquaresOfKnight(result, p)
            Piece.PieceType.PAWN -> addPossibleTargetSquaresOfPawn(result, p)
        }
        return result
    }

    private fun doAdd(squares: MutableList<Square>, s: Square?): Boolean {
        if (s != null && (s.piece == null || s.piece!!.isWhite != this.piece!!.isWhite)) {
            return squares.add(s)
        } else
            return false
    }

    private fun addPossibleTargetSquaresOfKing(squares: MutableList<Square>, p: Position) {
        doAdd(squares, getSquare(p, rank + 1, file.toInt()))
        doAdd(squares, getSquare(p, rank + 1, file - 1))
        doAdd(squares, getSquare(p, rank + 1, file + 1))
        doAdd(squares, getSquare(p, rank.toInt(), file - 1))
        doAdd(squares, getSquare(p, rank.toInt(), file + 1))
        doAdd(squares, getSquare(p, rank - 1, file.toInt()))
        doAdd(squares, getSquare(p, rank - 1, file - 1))
        doAdd(squares, getSquare(p, rank - 1, file + 1))
        // castling
        if (!p.isCheck) {
            val rank = if (this.piece!!.isWhite) 1 else 8
            for (castlingSquare in p.possibleCastlingSquareNames) {
                if (castlingSquare != null && castlingSquare.startsWith("g")) {
                    val f = getSquare(p, rank, 6)
                    val g = getSquare(p, rank, 7)
                    if (f!!.piece == null && !f.isAttacked(p) && g!!.piece == null && !g.isAttacked(p)) {
                        squares.add(g)
                    }
                } else if (castlingSquare != null && castlingSquare.startsWith("c")) {
                    val d = getSquare(p, rank, 4)
                    val c = getSquare(p, rank, 3)
                    if (d!!.piece == null && !d.isAttacked(p) && c!!.piece == null && !c.isAttacked(p)) {
                        squares.add(c)
                    }
                }
            }
        }

    }

    private fun addPossibleTargetSquaresOfQueen(squares: MutableList<Square>, p: Position) {
        addPossibleTargetSquaresOfRook(squares, p)
        addPossibleTargetSquaresOfBishop(squares, p)
    }

    private fun addPossibleTargetSquaresOfRook(squares: MutableList<Square>, p: Position) {
        var _rank = rank.toInt()
        var _file = file.toInt()
        while (true) { //go up
            val s = getSquare(p, ++_rank, _file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
        _rank = rank.toInt()
        _file = file.toInt()
        while (true) { //go right
            val s = getSquare(p, _rank, ++_file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
        _rank = rank.toInt()
        _file = file.toInt()
        while (true) { //go down
            val s = getSquare(p, --_rank, _file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
        _rank = rank.toInt()
        _file = file.toInt()
        while (true) { //go left
            val s = getSquare(p, _rank, --_file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
    }

    private fun addPossibleTargetSquaresOfBishop(squares: MutableList<Square>, p: Position) {
        var _rank = rank.toInt()
        var _file = file.toInt()
        while (true) { //go up-right
            val s = getSquare(p, ++_rank, ++_file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
        _rank = rank.toInt()
        _file = file.toInt()
        while (true) { //go down-right
            val s = getSquare(p, --_rank, ++_file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
        _rank = rank.toInt()
        _file = file.toInt()
        while (true) { //go up-left
            val s = getSquare(p, ++_rank, --_file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
        _rank = rank.toInt()
        _file = file.toInt()
        while (true) { //go down-left
            val s = getSquare(p, --_rank, --_file)
            if (!doAdd(squares, s)) break
            if (s!!.piece != null) break
        }
    }

    private fun addPossibleTargetSquaresOfKnight(squares: MutableList<Square>, p: Position) {
        doAdd(squares, getSquare(p, rank + 2, file + 1))
        doAdd(squares, getSquare(p, rank + 2, file - 1))
        doAdd(squares, getSquare(p, rank + 1, file + 2))
        doAdd(squares, getSquare(p, rank + 1, file - 2))
        doAdd(squares, getSquare(p, rank - 2, file + 1))
        doAdd(squares, getSquare(p, rank - 2, file - 1))
        doAdd(squares, getSquare(p, rank - 1, file + 2))
        doAdd(squares, getSquare(p, rank - 1, file - 2))
    }

    private fun addPossibleTargetSquaresOfPawn(squares: MutableList<Square>, p: Position) {
        val startRank = if (this.piece!!.isWhite) 2 else 7
        // move one square
        var s = getSquare(p, if (this.piece!!.isWhite) rank + 1 else rank - 1, file.toInt())
        if (s != null && s.piece == null) squares.add(s)
        if (rank.toInt() == startRank && s!!.piece == null) {
            // move two squares
            s = getSquare(p, if (this.piece!!.isWhite) rank + 2 else rank - 2, file.toInt())
            if (s != null && s.piece == null) squares.add(s)
        }
        // try captures (with en passant)
        val fen = if (p.fen != null) p.fen else p.previous.fen
        val enPassantField = fen.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3]
        s = getSquare(p, if (this.piece!!.isWhite) rank + 1 else rank - 1, file + 1)
        if (s != null && (s.piece != null && s.piece!!.isWhite != this.piece!!.isWhite || s.name == enPassantField))
            squares.add(s)
        s = getSquare(p, if (this.piece!!.isWhite) rank + 1 else rank - 1, file - 1)
        if (s != null && (s.piece != null && s.piece!!.isWhite != this.piece!!.isWhite || s.name == enPassantField))
            squares.add(s)

    }

    //TODO isPinned
    fun isPinned(p: Position, moveSquare: Square?): Boolean {
        if (piece!!.type === Piece.PieceType.KING) return false
        val kingsSquare = p.findKing(this.piece!!.isWhite)
        val originalPieceOnMoveSquare = moveSquare!!.piece
        try {
            if (moveSquare != null) {
                //temporarily change the position by adding a "ghost pawn" on our moveSquare...
                moveSquare.piece = if (piece!!.isWhite) Piece.WHITE_PAWN else Piece.BLACK_PAWN
            }
            val potentialAttackers = ArrayList<Square>()
            val enemyQueen = if (piece!!.isWhite) Piece.BLACK_QUEEN else Piece.WHITE_QUEEN
            val enemyRook = if (piece!!.isWhite) Piece.BLACK_ROOK else Piece.WHITE_ROOK
            // find potential pinning squares/pieces on same rank, file or diagonal behind us and our king...
            if (kingsSquare.rank == this.rank) {
                if (kingsSquare.file < this.file) {
                    for (_file in this.file + 1..8) {
                        val s = p.getSquare(rank.toInt(), _file)
                        if (s.piece === enemyQueen || s.piece === enemyRook) {
                            potentialAttackers.add(s)
                            break
                        } else if (s.piece != null) break
                    }
                } else {
                    for (_file in this.file - 1 downTo 1) {
                        val s = p.getSquare(rank.toInt(), _file)
                        if (s.piece === enemyQueen || s.piece === enemyRook) {
                            potentialAttackers.add(s)
                            break
                        } else if (s.piece != null) break
                    }
                }
            } else if (kingsSquare.file == this.file) {
                if (kingsSquare.rank < this.rank) {
                    for (_rank in this.rank + 1..8) {
                        val s = p.getSquare(_rank, this.file.toInt())
                        if (s.piece === enemyQueen || s.piece === enemyRook) {
                            potentialAttackers.add(s)
                            break
                        } else if (s.piece != null) break
                    }
                } else {
                    for (_rank in this.rank - 1 downTo 1) {
                        val s = p.getSquare(_rank, this.file.toInt())
                        if (s.piece === enemyQueen || s.piece === enemyRook) {
                            potentialAttackers.add(s)
                            break
                        } else if (s.piece != null) break
                    }
                }
            } else { //check diagonals..
                val enemyBishop = if (piece!!.isWhite) Piece.BLACK_BISHOP else Piece.WHITE_BISHOP
                var _rank = kingsSquare.rank.toInt()
                var _file = kingsSquare.file.toInt()
                var s: Square? = kingsSquare
                var foundMe = false
                while (true) { //go up-right
                    s = getSquare(p, ++_rank, ++_file)
                    if (s == null) break
                    if (foundMe && (s.piece === enemyQueen || s.piece === enemyBishop)) {
                        potentialAttackers.add(s)
                        break
                    } else if (s === this) {
                        foundMe = true
                    } else if (s.piece != null) break
                }
                if (!foundMe) {
                    s = kingsSquare
                    _rank = kingsSquare.rank.toInt()
                    _file = kingsSquare.file.toInt()
                    while (true) { //go up-left
                        s = getSquare(p, ++_rank, --_file)
                        if (s == null) break
                        if (foundMe && (s.piece === enemyQueen || s.piece === enemyBishop)) {
                            potentialAttackers.add(s)
                            break
                        } else if (s === this) {
                            foundMe = true
                        } else if (s.piece != null) break
                    }
                }
                if (!foundMe) {
                    s = kingsSquare
                    _rank = kingsSquare.rank.toInt()
                    _file = kingsSquare.file.toInt()
                    while (true) { //go down-right
                        s = getSquare(p, --_rank, ++_file)
                        if (s == null) break
                        if (foundMe && (s.piece === enemyQueen || s.piece === enemyBishop)) {
                            potentialAttackers.add(s)
                            break
                        } else if (s === this) {
                            foundMe = true
                        } else if (s.piece != null) break
                    }
                }
                if (!foundMe) {
                    s = kingsSquare
                    _rank = kingsSquare.rank.toInt()
                    _file = kingsSquare.file.toInt()
                    while (true) { //go down-left
                        s = getSquare(p, --_rank, --_file)
                        if (s == null) break
                        if (foundMe && (s.piece === enemyQueen || s.piece === enemyBishop)) {
                            potentialAttackers.add(s)
                            break
                        } else if (s === this) {
                            foundMe = true
                        } else if (s.piece != null) break
                    }
                }
            }
            // does any potential attacker in fact pin us?
            for (potentialAttacker in potentialAttackers) {
                if (potentialAttacker.attacks(kingsSquare, p, this)) return true
            }
            return false
        } finally {
            // restore original Position
            moveSquare.piece = originalPieceOnMoveSquare
        }
    }

    companion object {

        fun createEmpty8x8(): Array<Array<Square>> {
            val squares = Array<Array<Square>>(8) { arrayOfNulls<Square>(8) }
            for (rank in 1..8) {
                for (file in 1..8) {
                    squares[rank - 1][file - 1] = Square(rank.toByte(), file.toByte())
                }
            }
            return squares
        }

        /**
         * returns 1 for "a" and so forth
         */
        fun filenumberForName(fileName: String): Int {
            return fileName[0].toInt() - 96
        }
    }
*/

}

fun Byte.isEven() = this % 2 == 0