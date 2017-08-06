package de.toto.crt.game

class Position {

    private val squares = Array(8) { iOuter -> Array(8)
        { iInner -> Square((iOuter+1).toByte(), (iInner+1).toByte())}
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

/*


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



    private fun kingCanMoveTo(other: Square, p: Position): Boolean {
        if (kingAttacks(other, p)) return true
        val castlingSquareNames = p.possibleCastlingSquareNames
        if (other.name == castlingSquareNames[0] || other.name == castlingSquareNames[1])
            return true
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

/**
 * With a KING on square does he attack the other Square?
 */
fun Position.kingAttacks(square: Square, other: Square): Boolean {
    return with (square) {
        other.match(rank + 1, file) ||
        other.match(rank + 1, file - 1) ||
        other.match(rank + 1, file + 1) ||
        other.match(rank, file - 1) ||
        other.match(rank, file + 1) ||
        other.match(rank - 1, file) ||
        other.match(rank - 1, file - 1) ||
        other.match(rank - 1, file + 1)
    }
}

/**
 * With a KING on square can he move to or capture on the other Square?
 */
fun Position.kingCanMoveTo(isWhiteKing: Boolean, square: Square, other: Square): Boolean {
    if (other.piece?.isWhite == isWhiteKing) return false
    if (kingAttacks(square, other)) return true
    TODO()
//    val castlingSquareNames = p.possibleCastlingSquareNames
//    if (other.name == castlingSquareNames[0] || other.name == castlingSquareNames[1])
//        return true
    return false
}

/**
 * With a ROOK on square does he attack the other Square?
 * Blocking pieces are considered, pins are not.
 */
fun Position.rookAttacks(square: Square, other: Square): Boolean {
    // up
    if (square.rank < other.rank && square.file == other.file) {
        for (_rank in (square.rank + 1)..8) {
            val s = square(_rank, square.file.toInt())
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // down
    if (square.rank > other.rank && square.file == other.file) {
        for (_rank in square.rank - 1 downTo 1) {
            val s = square(_rank, square.file.toInt())
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // right
    if (square.file < other.file && square.rank == other.rank) {
        for (_file in (square.file + 1)..8) {
            val s = square(square.rank.toInt(), _file)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // left
    if (square.file > other.file && square.rank == other.rank) {
        for (_file in square.file - 1 downTo 1) {
            val s = square(square.rank.toInt(), _file)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    return false
}

/**
 * With a BISHOP on square does he attack the other Square?
 * Blocking pieces are considered, pins are not.
 */
fun Position.bishopAttacks(square: Square, other: Square): Boolean {
    // up-right
    if (square.rank < other.rank && square.file < other.file) {
        var rank = square.rank + 1
        var file = square.file + 1
        while (rank <= 8 && file <= 8) {
            val s = square(rank++, file++)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // up-left
    if (square.rank < other.rank && square.file > other.file) {
        var rank = square.rank + 1
        var file = square.file - 1
        while (rank <= 8 && file >= 1) {
            val s = square(rank++, file--)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // down-right
    if (square.rank > other.rank && square.file < other.file) {
        var rank = square.rank - 1
        var file = square.file + 1
        while (rank >= 1 && file <= 8) {
            val s = square(rank--, file++)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    // down-left
    if (square.rank > other.rank && square.file > other.file) {
        var rank = square.rank - 1
        var file = square.file - 1
        while (rank >= 1 && file >= 1) {
            val s = square(rank--, file--)
            if (other == s) return true
            if (!s.isEmpty) break
        }
    }
    return false
}

/**
 * With a QUEEN on square does she attack the other Square?
 * Blocking pieces are considered, pins are not.
 */
fun Position.queenAttacks(square: Square, other: Square): Boolean {
    return rookAttacks(square, other) || bishopAttacks(square, other)
}

/**
 * With a KNIGHT on square does it attack the other Square?
 * Pins are not considered.
 */
fun Position.knightAttacks(square: Square, other: Square): Boolean {
    val otherRank = other.rank.toInt()
    val otherFile = other.file.toInt()
    if (square.rank + 2 == otherRank && square.file + 1 == otherFile) return true
    if (square.rank + 2 == otherRank && square.file - 1 == otherFile) return true
    if (square.rank + 1 == otherRank && square.file + 2 == otherFile) return true
    if (square.rank + 1 == otherRank && square.file - 2 == otherFile) return true
    if (square.rank - 1 == otherRank && square.file + 2 == otherFile) return true
    if (square.rank - 1 == otherRank && square.file - 2 == otherFile) return true
    if (square.rank - 2 == otherRank && square.file + 1 == otherFile) return true
    if (square.rank - 2 == otherRank && square.file - 1 == otherFile) return true
    return false
}

/**
 * With a PAWN on square does he attack the other Square?
 * Pins are not considered.
 */
fun Position.pawnAttacks(isWhitePawn: Boolean, square: Square, other: Square): Boolean {
    val rank = square.rank + if (isWhitePawn) 1 else -1
    return other.rank.toInt() == rank
            && (other.file.toInt() == square.file + 1 || other.file.toInt() == square.file - 1)
}

/**
 * With a PAWN on square can he move to or capture on the other Square?
 * Blocking pieces and en passant captures are considered, pins are not.
 */
fun Position.pawnCanMoveTo(isWhitePawn: Boolean, square: Square, other: Square): Boolean {
    // try capture
    if (pawnAttacks(isWhitePawn, square, other)) {
        if (other.piece != null && other.piece?.isWhite != isWhitePawn) return true
        // en passant
        if (other.name == enPassantField()) return true
    }
    // try move one square
    val inFrontOfUs: Square = square(if (isWhitePawn) square.rank + 1 else square.rank - 1, square.file.toInt())
    if (inFrontOfUs == other && inFrontOfUs.isEmpty) return true
    // try move two squares if we are on our start square and are not blocked
    if (inFrontOfUs.isEmpty && square.rank.toInt() == if (isWhitePawn) 2 else 7) {
        square(square.rank + if (isWhitePawn) 2 else -2, square.file.toInt()).let {
            if (it == other && it.isEmpty) return true
        }
    }
    return false
}

private fun Square.match(_rank: Int, _file: Int) = rank.toInt() == _rank && file.toInt() == _file
private fun Square.match(_rank: Byte, _file: Int) = match(_rank.toInt(), _file)
private fun Square.match(_rank: Int, _file: Byte) = match(_rank, _file.toInt())