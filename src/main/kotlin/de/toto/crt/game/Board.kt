package de.toto.crt.game

class Board {

    val squares = Array(8) { iOuter -> Array(8)
        { iInner -> Square((iOuter+1).toByte(), (iInner+1).toByte())} }

    fun square(rank: Int, file: Int): Square = squares[rank][file]

    fun square(name: String): Square {
        val rankAndFile = Square.rankAndFileByName(name)
        return squares[rankAndFile.first-1][rankAndFile.second-1]
    }

}