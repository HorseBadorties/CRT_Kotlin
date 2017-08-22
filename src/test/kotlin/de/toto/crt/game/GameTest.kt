package de.toto.crt.game

import de.toto.crt.game.rules.createNextFromSANs
import org.junit.Test

import org.junit.Assert.*
import java.nio.file.Paths

class GameTest {

    @Test
    fun mergeIn() {
        val game1 = fromMoves("1.e4 e5 2.Nf3 Nc6")
        val game2 = fromMoves("1.e4 e5 2.Nf3 d6")
        game1.mergeIn(game2)
        game1.gotoLast()
        game1.back()
        println(game1)
    }

    @Test
    fun mergeInRepertoire() {
        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
        val repertoire = games.first()
        games.forEach { if (it !== repertoire) repertoire.mergeIn(it) }
        repertoire.gotoStartPosition()
        println(repertoire)
    }

}

fun fromMoves(pgnMoves: String): Game {
    val result = Game()
    result.currentPosition.createNextFromSANs(pgnMoves)
    return result
}

fun Game.gotoLast(): Position {
    while (currentPosition.hasNext) next()
    return currentPosition
}